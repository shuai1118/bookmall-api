
package com.book.mall.service;

import com.book.mall.api.mall.param.SaveCartItemParam;
import com.book.mall.api.mall.param.UpdateCartItemParam;
import com.book.mall.api.mall.vo.BookMallShoppingCartItemVO;
import com.book.mall.entity.BookMallShoppingCartItem;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.PageResult;

import java.util.List;

public interface BookMallShoppingCartService {

    /**
     * 保存商品至购物车中
     *
     * @param saveCartItemParam
     * @param userId
     * @return
     */
    String saveBookMallCartItem(SaveCartItemParam saveCartItemParam, Long userId);

    /**
     * 修改购物车中的属性
     *
     * @param updateCartItemParam
     * @param userId
     * @return
     */
    String updateBookMallCartItem(UpdateCartItemParam updateCartItemParam, Long userId);

    /**
     * 获取购物项详情
     *
     * @param bookMallShoppingCartItemId
     * @return
     */
    BookMallShoppingCartItem getBookMallCartItemById(Long bookMallShoppingCartItemId);

    /**
     * 删除购物车中的商品
     *
     *
     * @param shoppingCartItemId
     * @param userId
     * @return
     */
    Boolean deleteById(Long shoppingCartItemId, Long userId);

    /**
     * 获取我的购物车中的列表数据
     *
     * @param bookMallUserId
     * @return
     */
    List<BookMallShoppingCartItemVO> getMyShoppingCartItems(Long bookMallUserId);

    /**
     * 根据userId和cartItemIds获取对应的购物项记录
     *
     * @param cartItemIds
     * @param bookMallUserId
     * @return
     */
    List<BookMallShoppingCartItemVO> getCartItemsForSettle(List<Long> cartItemIds, Long bookMallUserId);

    /**
     * 我的购物车(分页数据)
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyShoppingCartItems(PageQueryUtil pageUtil);
}
