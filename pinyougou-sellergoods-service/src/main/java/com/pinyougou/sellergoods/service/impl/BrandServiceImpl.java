package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbBrandExample;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品品牌的实现
 * @author Administrator
 */
@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    /**
     * 查询品牌列表信息
     * @return
     */
    @Override
    public List<TbBrand> findAll() {
        //查询所有
        return brandMapper.selectByExample(null);
    }

    /**
     * 品牌分页
     * @param pageNum 当前页号
     * @param pageSize 每页显示的记录数
     * @return
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page= (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 添加品牌
     * @param brand 品牌
     */
    @Override
    public void add(TbBrand brand) {
        //在插入前可以完成对品牌名称唯一性对数据库校验或者数据库name设置唯一约束
        brandMapper.insert(brand);
    }

    /**
     * 根据ID获取单个品牌实体
     * @param id 主键ID
     * @return 品牌
     */
    @Override
    public TbBrand findOne(Long id) {
       return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改品牌信息
     * @param brand 品牌
     */
    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    /**
     * 批量删除品牌
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids){
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    /**
     * 带条件的Brand实体类分页查询
     * @param brand 实体类
     * @param pageNum 当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);//分页

        TbBrandExample example=new TbBrandExample();

        TbBrandExample.Criteria criteria = example.createCriteria();
        if(brand!=null){
            if(brand.getName()!=null && brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);

        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 列表数据
     */
    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }


}
