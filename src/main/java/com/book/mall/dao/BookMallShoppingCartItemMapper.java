
package com.book.mall.dao;

import com.book.mall.entity.BookMallShoppingCartItem;
import com.book.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(BookMallShoppingCartItem record);

    int insertSelective(BookMallShoppingCartItem record);

    BookMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    BookMallShoppingCartItem selectByUserIdAndGoodsId(@Param("bookMallUserId") Long bookMallUserId, @Param("goodsId") Long goodsId);

    List<BookMallShoppingCartItem> selectByUserId(@Param("bookMallUserId") Long bookMallUserId, @Param("number") int number);

    List<BookMallShoppingCartItem> selectByUserIdAndCartItemIds(@Param("bookMallUserId") Long bookMallUserId, @Param("cartItemIds") List<Long> cartItemIds);

    int selectCountByUserId(Long bookMallUserId);

    int updateByPrimaryKeySelective(BookMallShoppingCartItem record);

    int updateByPrimaryKey(BookMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);

    List<BookMallShoppingCartItem> findMyBookMallCartItems(PageQueryUtil pageUtil);

    int getTotalMyBookMallCartItems(PageQueryUtil pageUtil);
}