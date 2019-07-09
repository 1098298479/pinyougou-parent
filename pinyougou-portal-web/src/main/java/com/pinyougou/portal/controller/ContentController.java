package com.pinyougou.portal.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;

import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    /**
     * 根据分类ID查询所有的广告列表
     * @param contentCategoryId
     * @return
     */
    @RequestMapping("/findByContentCategoryId")
    public List<TbContent> findByContentCategoryId(Long contentCategoryId){

        return contentService.findByCategoryId(contentCategoryId);
    }
}
