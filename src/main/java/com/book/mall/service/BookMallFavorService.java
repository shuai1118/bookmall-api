
package com.book.mall.service;

import com.book.mall.api.mall.vo.BookMallIndexFavorVO;
import com.book.mall.entity.Favor;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.PageResult;

import java.util.List;

public interface BookMallFavorService {

    /**
     * 返回固定数量的轮播图对象(首页调用)
     *
     * @param userId
     * @return
     */
    List<BookMallIndexFavorVO> getFavorByUserId(Long userId);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getFavorPage(PageQueryUtil pageUtil);

    String saveFavor(Favor favor);

    String updateFavor(Favor favor);

    Favor getFavorById(Integer id);

    Boolean deleteBatch(Long[] ids);

    Boolean deleteByGoodsId(Long goodsId, Long userId);
}
