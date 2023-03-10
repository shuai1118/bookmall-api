
package com.book.mall.dao;

import com.book.mall.entity.BookMallOrder;
import com.book.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(BookMallOrder record);

    int insertSelective(BookMallOrder record);

    BookMallOrder selectByPrimaryKey(Long orderId);

    BookMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(BookMallOrder record);

    int updateByPrimaryKey(BookMallOrder record);

    List<BookMallOrder> findBookMallOrderList(PageQueryUtil pageUtil);

    int getTotalBookMallOrders(PageQueryUtil pageUtil);

    List<BookMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}