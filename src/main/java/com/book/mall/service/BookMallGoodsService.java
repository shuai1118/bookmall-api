
package com.book.mall.service;

import com.book.mall.entity.BookMallGoods;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.PageResult;

import java.util.List;

public interface BookMallGoodsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getBookMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String saveBookMallGoods(BookMallGoods goods);

    /**
     * 批量新增商品数据
     *
     * @param bookMallGoodsList
     * @return
     */
    void batchSaveBookMallGoods(List<BookMallGoods> bookMallGoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updateBookMallGoods(BookMallGoods goods);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    BookMallGoods getBookMallGoodsById(Long id);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchBookMallGoods(PageQueryUtil pageUtil);
}
