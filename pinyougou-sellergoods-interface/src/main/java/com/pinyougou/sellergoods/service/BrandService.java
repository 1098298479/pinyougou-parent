package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

/**
 * 商品品牌的接口
 * @author Administrator
 */
public interface BrandService {
	
	/**
	 * 查询品牌列表信息
	 * @return 
	 */
	public List<TbBrand> findAll();

	/**
	 * 品牌分页
	 * @param pageNum 当前页号
	 * @param pageSize 每页显示的记录数
	 * @return 返回品牌列表
	 */
	public PageResult findPage(int pageNum,int pageSize);

	/**
	 * 添加品牌
	 * @param brand 品牌
	 */
	public void add(TbBrand brand);

	/**
	 * 修改品牌信息
	 * @param brand 品牌
	 */
	public void update(TbBrand brand);


	/**
	 * 根据ID获取单个品牌实体
	 * @param id 主键ID
	 * @return 品牌
	 */
	public TbBrand findOne(Long id);

	/**
	 * 批量删除品牌信息
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param brand 实体类
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbBrand brand, int pageNum,int pageSize);


	/**
	 * 品牌下拉框数据
	 * @return
	 */
	List<Map> selectOptionList();

}
