
package com.book.mall.service.impl;

import com.book.mall.api.mall.vo.BookMallSearchGoodsVO;
import com.book.mall.common.BookMallCategoryLevelEnum;
import com.book.mall.common.BookMallException;
import com.book.mall.common.ServiceResultEnum;
import com.book.mall.dao.GoodsCategoryMapper;
import com.book.mall.dao.BookMallGoodsMapper;
import com.book.mall.entity.GoodsCategory;
import com.book.mall.entity.BookMallGoods;
import com.book.mall.service.BookMallGoodsService;
import com.book.mall.util.BeanUtil;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookMallGoodsServiceImpl implements BookMallGoodsService {

    @Autowired
    private BookMallGoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getBookMallGoodsPage(PageQueryUtil pageUtil) {
        List<BookMallGoods> goodsList = goodsMapper.findBookMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalBookMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveBookMallGoods(BookMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != BookMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        if (goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId()) != null) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveBookMallGoods(List<BookMallGoods> bookMallGoodsList) {
        if (!CollectionUtils.isEmpty(bookMallGoodsList)) {
            goodsMapper.batchInsert(bookMallGoodsList);
        }
    }

    @Override
    public String updateBookMallGoods(BookMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != BookMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        BookMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        BookMallGoods temp2 = goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId());
        if (temp2 != null && !temp2.getGoodsId().equals(goods.getGoodsId())) {
            //name和分类id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public BookMallGoods getBookMallGoodsById(Long id) {
        BookMallGoods bookMallGoods = goodsMapper.selectByPrimaryKey(id);
        if (bookMallGoods == null) {
            BookMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        return bookMallGoods;
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchBookMallGoods(PageQueryUtil pageUtil) {
        List<BookMallGoods> goodsList = goodsMapper.findBookMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalBookMallGoodsBySearch(pageUtil);
        List<BookMallSearchGoodsVO> bookMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            bookMallSearchGoodsVOS = BeanUtil.copyList(goodsList, BookMallSearchGoodsVO.class);
            for (BookMallSearchGoodsVO bookMallSearchGoodsVO : bookMallSearchGoodsVOS) {
                String goodsName = bookMallSearchGoodsVO.getGoodsName();
                String goodsIntro = bookMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    bookMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    bookMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(bookMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
