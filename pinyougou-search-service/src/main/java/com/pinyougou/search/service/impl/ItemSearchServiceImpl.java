package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 普通搜索
     * 搜索框按关键字搜索
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map searchMap) {

        Map<String,Object> map=new HashMap<>();
        //去掉空格
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));
        //1.查询商品列表
        map.putAll(searchList(searchMap));
        System.out.println("111111111111111111111");
        //2.根据关键字查询商品分类
        List categoryList=SearchCategoryList(searchMap);
        System.out.println("22222222222222222222222");

        map.put("categoryList",categoryList);

                //注意这两种写法的区别
       /* //3.查询品牌和规格列表
        if(categoryList.size()>0){
           map.putAll(searchBrandAndSpecList((String)categoryList.get(0))) ;
        }*/
        System.out.println("33333333333333333333333333");

        //3.查询品牌和规格列表
        String category= (String) searchMap.get("category");
        System.out.println("打印:category:"+category);
        if(!category.equals("")){//如果有分类名称
            map.putAll(searchBrandAndSpecList(category));
        }else{//如果没有分类名称，按照第一个查询
            if(categoryList.size()>0){
                System.out.println("分类列表对应的第一个元素:"+categoryList.get(0));
                map.putAll(searchBrandAndSpecList((String)categoryList.get(0)));
            }
        }
        System.out.println("4444444444444444");

        return map;
    }

    /**
     * 高亮搜索 查询商品列表
     * @param searchMap
     * @return
     */
    private Map<String,Object> searchList(Map searchMap){

        Map<String,Object> map=new HashMap<>();
        HighlightQuery query=new SimpleHighlightQuery();
        //设置高亮的域
        HighlightOptions options=new HighlightOptions().addField("item_title");
        options.setSimplePostfix("<em style='color:red'>");
        options.setSimplePostfix("</em>");
        query.setHighlightOptions(options);

        //1.1关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2按商品分类过滤
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.3按品牌分类过滤
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.4过滤规格
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap= (Map) searchMap.get("spec");
            for(String key:specMap.keySet() ){
                Criteria filterCriteria=new Criteria("item_spec_"+key).is( specMap.get(key) );
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.5按价格筛选.....
        if(!"".equals(searchMap.get("price"))){
            String[] price = ((String) searchMap.get("price")).split("-");
            if(!price[0].equals("0")){//如果区间起点不等于0
                Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){//如果区间终点不等于*
                Criteria filterCriteria=new  Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.6分页查询
        Integer pageNo= (Integer) searchMap.get("pageNo");//提取页码
        if(pageNo==null){
            pageNo=1;//默认第一页
        }
        Integer pageSize=(Integer) searchMap.get("pageSize");//每页记录数
        if(pageSize==null){
            pageSize=20;//默认20
        }
        query.setOffset((pageNo-1)*pageSize);//从第几条记录查询
        query.setRows(pageSize);

        //1.7排序
        String sortValue= (String) searchMap.get("sort");//ASC  DESC
        String sortField= (String) searchMap.get("sortField");//排序字段
        if(sortValue!=null && !sortValue.equals("")){
            if(sortValue.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
                query.addSort(sort);
            }
        }

        //执行查询
        HighlightPage<TbItem> page=solrTemplate.queryForHighlightPage(query,TbItem.class);

        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for (HighlightEntry<TbItem> h : highlighted){

            //获得原实体类
            TbItem item=h.getEntity();

            //这两行核心代码解读下
            /**
             * h.getHighlights() 获取每条高亮记录的所有高亮域
             * 在new HighlightOptions().addField("item_title")是添加的高亮域
             * h.getHighlights().get(0).getSnipplets()获取第一个高亮域的内容
             * h.getHighlights().get(0).getSnipplets().get(0) 一个高亮域中可能存在多值
             * 取决于solr中的配置域的是否配置了multiValued是否为true
             */
            if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
            }

        }
        map.put("rows",page.getContent());
        map.put("totalPages", page.getTotalPages());//返回总页数
        map.put("total", page.getTotalElements());//返回总记录数
        return map;
    }

    /**
     * 根据关键字查询分类列表
     */
    private List SearchCategoryList(Map searchMap){

        List<String> list=new ArrayList();
        Query query=new SimpleQuery();
        //按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for(GroupEntry<TbItem> entry:content){
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }
        return list;
    }

    /**
     * 从redis中查数据:
     * 根据分类名称查询商品品牌和规格列表
     * @param category
     * @return
     */
    private Map searchBrandAndSpecList(String category){

        Map<String,List> map=new HashMap<String,List>();
        //根据商品分类名称，获取对应的模板ID
       Long typeTemplateId=(Long) redisTemplate.boundHashOps("itemCate").get(category);
       System.out.println("打印:商品模板ID"+typeTemplateId);
       if(typeTemplateId!=null){
           List<TbBrand> brandList=  (List<TbBrand>) redisTemplate.boundHashOps("brand").get(typeTemplateId);
           List<TbSpecification> specList=(List<TbSpecification>) redisTemplate.boundHashOps("spec").get(typeTemplateId);
           System.out.println("品牌的大小:"+brandList.size());
           System.out.println("规格的大小:"+specList.size());
           map.put("brandList",brandList);
           map.put("specList",specList);
       }
       return map;
    }


    /**
     * 导入数据
     * @param list
     */
    public void importList(List list){
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 删除数据
     * 从索引库中删除对应的数据
     * @param ids
     */
    public void deleteByGoodsIds(List goodsIdList){

        System.out.println("删除商品ID"+goodsIdList);
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();

    }

}
