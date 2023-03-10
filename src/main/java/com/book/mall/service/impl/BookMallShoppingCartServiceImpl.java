
package com.book.mall.service.impl;

import com.book.mall.api.mall.param.SaveCartItemParam;
import com.book.mall.api.mall.param.UpdateCartItemParam;
import com.book.mall.common.Constants;
import com.book.mall.common.BookMallException;
import com.book.mall.common.ServiceResultEnum;
import com.book.mall.api.mall.vo.BookMallShoppingCartItemVO;
import com.book.mall.dao.BookMallGoodsMapper;
import com.book.mall.dao.BookMallShoppingCartItemMapper;
import com.book.mall.entity.BookMallGoods;
import com.book.mall.entity.BookMallShoppingCartItem;
import com.book.mall.service.BookMallShoppingCartService;
import com.book.mall.util.BeanUtil;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookMallShoppingCartServiceImpl implements BookMallShoppingCartService {

    @Autowired
    private BookMallShoppingCartItemMapper bookMallShoppingCartItemMapper;

    @Autowired
    private BookMallGoodsMapper bookMallGoodsMapper;

    @Override
    public String saveBookMallCartItem(SaveCartItemParam saveCartItemParam, Long userId) {
        BookMallShoppingCartItem temp = bookMallShoppingCartItemMapper.selectByUserIdAndGoodsId(userId, saveCartItemParam.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            BookMallException.fail(ServiceResultEnum.SHOPPING_CART_ITEM_EXIST_ERROR.getResult());
        }
        BookMallGoods bookMallGoods = bookMallGoodsMapper.selectByPrimaryKey(saveCartItemParam.getGoodsId());
        //商品为空
        if (bookMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = bookMallShoppingCartItemMapper.selectCountByUserId(userId);
        //超出单个商品的最大数量
        if (saveCartItemParam.getGoodsCount() < 1) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_NUMBER_ERROR.getResult();
        }
        //超出单个商品的最大数量
        if (saveCartItemParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        BookMallShoppingCartItem bookMallShoppingCartItem = new BookMallShoppingCartItem();
        BeanUtil.copyProperties(saveCartItemParam, bookMallShoppingCartItem);
        bookMallShoppingCartItem.setUserId(userId);
        //保存记录
        if (bookMallShoppingCartItemMapper.insertSelective(bookMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateBookMallCartItem(UpdateCartItemParam updateCartItemParam, Long userId) {
        BookMallShoppingCartItem bookMallShoppingCartItemUpdate = bookMallShoppingCartItemMapper.selectByPrimaryKey(updateCartItemParam.getCartItemId());
        if (bookMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (!bookMallShoppingCartItemUpdate.getUserId().equals(userId)) {
            BookMallException.fail(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        //超出单个商品的最大数量
        if (updateCartItemParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //当前登录账号的userId与待修改的cartItem中userId不同，返回错误
        if (!bookMallShoppingCartItemUpdate.getUserId().equals(userId)) {
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }
        //数值相同，则不执行数据操作
        if (updateCartItemParam.getGoodsCount().equals(bookMallShoppingCartItemUpdate.getGoodsCount())) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        bookMallShoppingCartItemUpdate.setGoodsCount(updateCartItemParam.getGoodsCount());
        bookMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (bookMallShoppingCartItemMapper.updateByPrimaryKeySelective(bookMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public BookMallShoppingCartItem getBookMallCartItemById(Long bookMallShoppingCartItemId) {
        BookMallShoppingCartItem bookMallShoppingCartItem = bookMallShoppingCartItemMapper.selectByPrimaryKey(bookMallShoppingCartItemId);
        if (bookMallShoppingCartItem == null) {
            BookMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return bookMallShoppingCartItem;
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId, Long userId) {
        BookMallShoppingCartItem bookMallShoppingCartItem = bookMallShoppingCartItemMapper.selectByPrimaryKey(shoppingCartItemId);
        if (bookMallShoppingCartItem == null) {
            return false;
        }
        //userId不同不能删除
        if (!userId.equals(bookMallShoppingCartItem.getUserId())) {
            return false;
        }
        return bookMallShoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public List<BookMallShoppingCartItemVO> getMyShoppingCartItems(Long bookMallUserId) {
        List<BookMallShoppingCartItemVO> bookMallShoppingCartItemVOS = new ArrayList<>();
        List<BookMallShoppingCartItem> bookMallShoppingCartItems = bookMallShoppingCartItemMapper.selectByUserId(bookMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        return getBookMallShoppingCartItemVOS(bookMallShoppingCartItemVOS, bookMallShoppingCartItems);
    }

    @Override
    public List<BookMallShoppingCartItemVO> getCartItemsForSettle(List<Long> cartItemIds, Long bookMallUserId) {
        List<BookMallShoppingCartItemVO> bookMallShoppingCartItemVOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(cartItemIds)) {
            BookMallException.fail("购物项不能为空");
        }
        List<BookMallShoppingCartItem> bookMallShoppingCartItems = bookMallShoppingCartItemMapper.selectByUserIdAndCartItemIds(bookMallUserId, cartItemIds);
        if (CollectionUtils.isEmpty(bookMallShoppingCartItems)) {
            BookMallException.fail("购物项不能为空");
        }
        if (bookMallShoppingCartItems.size() != cartItemIds.size()) {
            BookMallException.fail("参数异常");
        }
        return getBookMallShoppingCartItemVOS(bookMallShoppingCartItemVOS, bookMallShoppingCartItems);
    }

    /**
     * 数据转换
     *
     * @param bookMallShoppingCartItemVOS
     * @param bookMallShoppingCartItems
     * @return
     */
    private List<BookMallShoppingCartItemVO> getBookMallShoppingCartItemVOS(List<BookMallShoppingCartItemVO> bookMallShoppingCartItemVOS, List<BookMallShoppingCartItem> bookMallShoppingCartItems) {
        if (!CollectionUtils.isEmpty(bookMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> bookMallGoodsIds = bookMallShoppingCartItems.stream().map(BookMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<BookMallGoods> bookMallGoods = bookMallGoodsMapper.selectByPrimaryKeys(bookMallGoodsIds);
            Map<Long, BookMallGoods> bookMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(bookMallGoods)) {
                bookMallGoodsMap = bookMallGoods.stream().collect(Collectors.toMap(BookMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (BookMallShoppingCartItem bookMallShoppingCartItem : bookMallShoppingCartItems) {
                BookMallShoppingCartItemVO bookMallShoppingCartItemVO = new BookMallShoppingCartItemVO();
                BeanUtil.copyProperties(bookMallShoppingCartItem, bookMallShoppingCartItemVO);
                if (bookMallGoodsMap.containsKey(bookMallShoppingCartItem.getGoodsId())) {
                    BookMallGoods bookMallGoodsTemp = bookMallGoodsMap.get(bookMallShoppingCartItem.getGoodsId());
                    bookMallShoppingCartItemVO.setGoodsCoverImg(bookMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = bookMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    bookMallShoppingCartItemVO.setGoodsName(goodsName);
                    bookMallShoppingCartItemVO.setSellingPrice(bookMallGoodsTemp.getSellingPrice());
                    bookMallShoppingCartItemVOS.add(bookMallShoppingCartItemVO);
                }
            }
        }
        return bookMallShoppingCartItemVOS;
    }

    @Override
    public PageResult getMyShoppingCartItems(PageQueryUtil pageUtil) {
        List<BookMallShoppingCartItemVO> bookMallShoppingCartItemVOS = new ArrayList<>();
        List<BookMallShoppingCartItem> bookMallShoppingCartItems = bookMallShoppingCartItemMapper.findMyBookMallCartItems(pageUtil);
        int total = bookMallShoppingCartItemMapper.getTotalMyBookMallCartItems(pageUtil);
        PageResult pageResult = new PageResult(getBookMallShoppingCartItemVOS(bookMallShoppingCartItemVOS, bookMallShoppingCartItems), total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
