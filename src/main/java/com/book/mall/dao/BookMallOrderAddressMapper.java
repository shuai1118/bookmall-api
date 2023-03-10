
package com.book.mall.dao;

import com.book.mall.entity.BookMallOrderAddress;

public interface BookMallOrderAddressMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(BookMallOrderAddress record);

    int insertSelective(BookMallOrderAddress record);

    BookMallOrderAddress selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(BookMallOrderAddress record);

    int updateByPrimaryKey(BookMallOrderAddress record);
}