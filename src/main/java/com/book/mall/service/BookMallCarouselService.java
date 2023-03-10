
package com.book.mall.service;

import com.book.mall.api.mall.vo.BookMallIndexCarouselVO;
import com.book.mall.entity.Carousel;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.PageResult;

import java.util.List;

public interface BookMallCarouselService {

    /**
     * 返回固定数量的轮播图对象(首页调用)
     *
     * @param number
     * @return
     */
    List<BookMallIndexCarouselVO> getCarouselsForIndex(int number);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(Carousel carousel);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    Boolean deleteBatch(Long[] ids);
}
