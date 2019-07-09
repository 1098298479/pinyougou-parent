package com.pinyougou.page.service;

/**
 * FreeMark网页静态化
 * 商品详情页接口
 */
public interface ItemPageService {

    /**
     * 获取商品详情页
     * @param goodsId
     * @return
     */
    public boolean getItemPage(Long goodsId);

    /**
     * 删除商品详情页
     * @param goodsId
     * @return
     */
    public boolean deleteItemPage(Long[] goodsIds);
}
