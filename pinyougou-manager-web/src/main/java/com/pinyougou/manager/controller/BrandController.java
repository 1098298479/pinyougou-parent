package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;
	
	/**
	 * 查询商品品牌列表信息
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}

	/**
	 * 分页查询
	 * @param page 当前页码
	 * @param size 每页记录数
	 * @return 分页实体类
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int size){
		return brandService.findPage(page, size);
	}

	@RequestMapping("/addBrand")
	public Result addBrand(@RequestBody TbBrand brand){

		try {
			brandService.add(brand);
			return new Result(true, "增加成功");
		}catch(Exception e){   //可做优化，对不同的异常情况返回各种信息
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 更新品牌信息
	 * @param brand 待更新的品牌对象
	 * @return
	 */

	@RequestMapping("/updateBrand")
	public Result update(@RequestBody TbBrand brand){
		try {
			brandService.update(brand);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	/**
	 * 根据主键ID获取实体对象
	 * @param id 主键ID
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbBrand findOne(Long id){
		return brandService.findOne(id);
	}

	/**
	 * 批量删除品牌信息
	 * @param ids
	 * @return
	 */
	@RequestMapping("/deleteSelected")
	public Result delete(Long[] ids){
		try {
			brandService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	/**
	 * 搜索查询
	 * @param brand
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand brand,int page,int size){
		return brandService.findPage(brand,page,size);
	}

	/**
	 * 下拉列表数据
	 * @return
	 */
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return brandService.selectOptionList();
	}


}
