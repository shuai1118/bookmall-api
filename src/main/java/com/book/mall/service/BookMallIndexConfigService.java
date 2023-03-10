
package com.book.mall.service;

import com.book.mall.api.mall.vo.BookMallIndexConfigGoodsVO;
import com.book.mall.entity.IndexConfig;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.PageResult;

import java.util.List;

public interface BookMallIndexConfigService {

    /**
     * 返回固定数量的首页配置商品对象(首页调用)
     *
     * @param number
     * @return
     */
    List<BookMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getConfigsPage(PageQueryUtil pageUtil);

    String saveIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    IndexConfig getIndexConfigById(Long id);

    Boolean deleteBatch(Long[] ids);
}
