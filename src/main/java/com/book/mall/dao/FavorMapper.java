
package com.book.mall.dao;

import com.book.mall.entity.Favor;
import com.book.mall.entity.BookMallFavors;
import com.book.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FavorMapper {
    int deleteByPrimaryKey(Integer favorId);

    int insert(Favor record);

    int insertSelective(Favor record);

    Favor selectByPrimaryKey(Integer favorId);

    int updateByPrimaryKeySelective(Favor record);

    int updateByPrimaryKey(Favor record);

    List<Favor> findFavorList(PageQueryUtil pageUtil);

    int getTotalFavors(PageQueryUtil pageUtil);

    int deleteBatch(Long[] ids);

    int deleteByGoodsId(Favor record);
    List<BookMallFavors> findFavorsByUser(@Param("userId") Long userId);
}