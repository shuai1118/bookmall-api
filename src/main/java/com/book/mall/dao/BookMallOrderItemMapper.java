
package com.book.mall.dao;

import com.book.mall.entity.BookMallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(BookMallOrderItem record);

    int insertSelective(BookMallOrderItem record);

    BookMallOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<BookMallOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<BookMallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<BookMallOrderItem> orderItems);

    int updateByPrimaryKeySelective(BookMallOrderItem record);

    int updateByPrimaryKey(BookMallOrderItem record);
}