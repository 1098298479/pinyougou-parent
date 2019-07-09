package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	private RedisTemplate redisTemplate;


	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);
        saveDataToRedis();//存入模板数据及其相关的品牌和规格数据到缓存中
		return new PageResult(page.getTotal(), page.getResult());
	}


    /**
     *主键查询规格列表
     * @param id 模板类主键
     * @return
     */

	@Override
	public List<Map> findSpecList(Long id) {

        //1.主键查询
	   TbTypeTemplate typeTemplate= typeTemplateMapper.selectByPrimaryKey(id);
	   System.out.println("打印前的数据:"+typeTemplate.getSpecIds());
	   //2.获取specList:[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        List<Map> specList=JSON.parseArray(typeTemplate.getSpecIds(),Map.class);
        System.out.println("打印后的数据:"+specList);
        //[{id=27, text=网络}, {id=32, text=机身内存}]
        for(Map map:specList){
			//3.根据获取的specList中的key查询规格选项对应的集合
			//注意以下两行代码要放在循环体内，作用域问题
			TbSpecificationOptionExample specificationOptionExample=new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria=specificationOptionExample.createCriteria();
        	//注意区别在哪
           // criteria.andSpecIdEqualTo((Long)map.get("id"));
			criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
            List<TbSpecificationOption> options=specificationOptionMapper.selectByExample(specificationOptionExample);
            map.put("options",options);
        }
        System.out.println("TypeTemplateServiceImpl中FindSpecList方法打印出specList:"+specList);
		return specList;
	}

    /**
     * 将模板数据存入缓存中
     */
    //@TODO
    //需要了解FastJson的使用
	public void saveDataToRedis(){

	    List<TbTypeTemplate> list=findAll();
        for (TbTypeTemplate typeTemplate : list) {
            //商品规格Json化
          // List<Map> specList=JSON.parseArray(typeTemplate.getSpecIds(),Map.class);
			//好好理解下为啥不用上面，改用下面
			List<Map> specList = findSpecList(typeTemplate.getId());//根据模板ID查询规格列表
            redisTemplate.boundHashOps("spec").put(typeTemplate.getId(),specList);
            //商品品牌Json化
            List<Map> brandList=JSON.parseArray(typeTemplate.getBrandIds(),Map.class);
            redisTemplate.boundHashOps("brand").put(typeTemplate.getId(),brandList);
        }
    }
}
