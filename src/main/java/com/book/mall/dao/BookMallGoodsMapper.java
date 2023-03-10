
package com.book.mall.dao;

import com.book.mall.entity.BookMallGoods;
import com.book.mall.entity.StockNumDTO;
import com.book.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(BookMallGoods record);

    int insertSelective(BookMallGoods record);

    BookMallGoods selectByPrimaryKey(Long goodsId);

    BookMallGoods selectByCategoryIdAndName(@Param("goodsName") String goodsName, @Param("goodsCategoryId") Long goodsCategoryId);

    int updateByPrimaryKeySelective(BookMallGoods record);

    int updateByPrimaryKeyWithBLOBs(BookMallGoods record);

    int updateByPrimaryKey(BookMallGoods record);

    List<BookMallGoods> findBookMallGoodsList(PageQueryUtil pageUtil);

    int getTotalBookMallGoods(PageQueryUtil pageUtil);

    List<BookMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<BookMallGoods> findBookMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalBookMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("bookMallGoodsList") List<BookMallGoods> bookMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int recoverStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}