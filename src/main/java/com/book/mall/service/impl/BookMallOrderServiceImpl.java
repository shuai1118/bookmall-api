
package com.book.mall.service.impl;

import com.book.mall.api.mall.vo.BookMallOrderDetailVO;
import com.book.mall.api.mall.vo.BookMallOrderItemVO;
import com.book.mall.api.mall.vo.BookMallOrderListVO;
import com.book.mall.api.mall.vo.BookMallShoppingCartItemVO;
import com.book.mall.common.*;
import com.book.mall.dao.*;
import com.book.mall.entity.*;
import com.book.mall.service.BookMallOrderService;
import com.book.mall.util.BeanUtil;
import com.book.mall.util.NumberUtil;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class BookMallOrderServiceImpl implements BookMallOrderService {

    @Autowired
    private BookMallOrderMapper bookMallOrderMapper;
    @Autowired
    private BookMallOrderItemMapper bookMallOrderItemMapper;
    @Autowired
    private BookMallShoppingCartItemMapper bookMallShoppingCartItemMapper;
    @Autowired
    private BookMallGoodsMapper bookMallGoodsMapper;
    @Autowired
    private BookMallOrderAddressMapper bookMallOrderAddressMapper;

    @Override
    public BookMallOrderDetailVO getOrderDetailByOrderId(Long orderId) {
        BookMallOrder bookMallOrder = bookMallOrderMapper.selectByPrimaryKey(orderId);
        if (bookMallOrder == null) {
            BookMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        List<BookMallOrderItem> orderItems = bookMallOrderItemMapper.selectByOrderId(bookMallOrder.getOrderId());
        //获取订单项数据
        if (!CollectionUtils.isEmpty(orderItems)) {
            List<BookMallOrderItemVO> bookMallOrderItemVOS = BeanUtil.copyList(orderItems, BookMallOrderItemVO.class);
            BookMallOrderDetailVO bookMallOrderDetailVO = new BookMallOrderDetailVO();
            BeanUtil.copyProperties(bookMallOrder, bookMallOrderDetailVO);
            bookMallOrderDetailVO.setOrderStatusString(BookMallOrderStatusEnum.getBookMallOrderStatusEnumByStatus(bookMallOrderDetailVO.getOrderStatus()).getName());
            bookMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(bookMallOrderDetailVO.getPayType()).getName());
            bookMallOrderDetailVO.setBookMallOrderItemVOS(bookMallOrderItemVOS);
            return bookMallOrderDetailVO;
        } else {
            BookMallException.fail(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
            return null;
        }
    }

    @Override
    public BookMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        BookMallOrder bookMallOrder = bookMallOrderMapper.selectByOrderNo(orderNo);
        if (bookMallOrder == null) {
            BookMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        if (!userId.equals(bookMallOrder.getUserId())) {
            BookMallException.fail(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        List<BookMallOrderItem> orderItems = bookMallOrderItemMapper.selectByOrderId(bookMallOrder.getOrderId());
        //获取订单项数据
        if (CollectionUtils.isEmpty(orderItems)) {
            BookMallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<BookMallOrderItemVO> bookMallOrderItemVOS = BeanUtil.copyList(orderItems, BookMallOrderItemVO.class);
        BookMallOrderDetailVO bookMallOrderDetailVO = new BookMallOrderDetailVO();
        BeanUtil.copyProperties(bookMallOrder, bookMallOrderDetailVO);
        bookMallOrderDetailVO.setOrderStatusString(BookMallOrderStatusEnum.getBookMallOrderStatusEnumByStatus(bookMallOrderDetailVO.getOrderStatus()).getName());
        bookMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(bookMallOrderDetailVO.getPayType()).getName());
        bookMallOrderDetailVO.setBookMallOrderItemVOS(bookMallOrderItemVOS);
        return bookMallOrderDetailVO;
    }


    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = bookMallOrderMapper.getTotalBookMallOrders(pageUtil);
        List<BookMallOrder> bookMallOrders = bookMallOrderMapper.findBookMallOrderList(pageUtil);
        List<BookMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(bookMallOrders, BookMallOrderListVO.class);
            //设置订单状态中文显示值
            for (BookMallOrderListVO bookMallOrderListVO : orderListVOS) {
                bookMallOrderListVO.setOrderStatusString(BookMallOrderStatusEnum.getBookMallOrderStatusEnumByStatus(bookMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = bookMallOrders.stream().map(BookMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<BookMallOrderItem> orderItems = bookMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<BookMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(BookMallOrderItem::getOrderId));
                for (BookMallOrderListVO bookMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(bookMallOrderListVO.getOrderId())) {
                        List<BookMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(bookMallOrderListVO.getOrderId());
                        //将BookMallOrderItem对象列表转换成BookMallOrderItemVO对象列表
                        List<BookMallOrderItemVO> bookMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, BookMallOrderItemVO.class);
                        bookMallOrderListVO.setBookMallOrderItemVOS(bookMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String cancelOrder(String orderNo, Long userId) {
        BookMallOrder bookMallOrder = bookMallOrderMapper.selectByOrderNo(orderNo);
        if (bookMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(bookMallOrder.getUserId())) {
                BookMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            //订单状态判断
            if (bookMallOrder.getOrderStatus().intValue() == BookMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || bookMallOrder.getOrderStatus().intValue() == BookMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || bookMallOrder.getOrderStatus().intValue() == BookMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || bookMallOrder.getOrderStatus().intValue() == BookMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            //修改订单状态&&恢复库存
            if (bookMallOrderMapper.closeOrder(Collections.singletonList(bookMallOrder.getOrderId()), BookMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0 && recoverStockNum(Collections.singletonList(bookMallOrder.getOrderId()))) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        BookMallOrder bookMallOrder = bookMallOrderMapper.selectByOrderNo(orderNo);
        if (bookMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(bookMallOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //订单状态判断 非出库状态下不进行修改操作
            if (bookMallOrder.getOrderStatus().intValue() != BookMallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            bookMallOrder.setOrderStatus((byte) BookMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            bookMallOrder.setUpdateTime(new Date());
            if (bookMallOrderMapper.updateByPrimaryKeySelective(bookMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        BookMallOrder bookMallOrder = bookMallOrderMapper.selectByOrderNo(orderNo);
        if (bookMallOrder != null) {
            //订单状态判断 非待支付状态下不进行修改操作
            if (bookMallOrder.getOrderStatus().intValue() != BookMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            bookMallOrder.setOrderStatus((byte) BookMallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            bookMallOrder.setPayType((byte) payType);
            bookMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            bookMallOrder.setPayTime(new Date());
            bookMallOrder.setUpdateTime(new Date());
            if (bookMallOrderMapper.updateByPrimaryKeySelective(bookMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(MallUser loginMallUser, MallUserAddress address, List<BookMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(BookMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(BookMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<BookMallGoods> bookMallGoods = bookMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<BookMallGoods> goodsListNotSelling = bookMallGoods.stream()
                .filter(bookMallGoodsTemp -> bookMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            BookMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, BookMallGoods> bookMallGoodsMap = bookMallGoods.stream().collect(Collectors.toMap(BookMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (BookMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!bookMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                BookMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > bookMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                BookMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(bookMallGoods)) {
            if (bookMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = bookMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    BookMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                BookMallOrder bookMallOrder = new BookMallOrder();
                bookMallOrder.setOrderNo(orderNo);
                bookMallOrder.setUserId(loginMallUser.getUserId());
                //总价
                for (BookMallShoppingCartItemVO bookMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += bookMallShoppingCartItemVO.getGoodsCount() * bookMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    BookMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                bookMallOrder.setTotalPrice(priceTotal);
                String extraInfo = "";
                bookMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (bookMallOrderMapper.insertSelective(bookMallOrder) > 0) {
                    //生成订单收货地址快照，并保存至数据库
                    BookMallOrderAddress bookMallOrderAddress = new BookMallOrderAddress();
                    BeanUtil.copyProperties(address, bookMallOrderAddress);
                    bookMallOrderAddress.setOrderId(bookMallOrder.getOrderId());
                    //生成所有的订单项快照，并保存至数据库
                    List<BookMallOrderItem> bookMallOrderItems = new ArrayList<>();
                    for (BookMallShoppingCartItemVO bookMallShoppingCartItemVO : myShoppingCartItems) {
                        BookMallOrderItem bookMallOrderItem = new BookMallOrderItem();
                        //使用BeanUtil工具类将bookMallShoppingCartItemVO中的属性复制到bookMallOrderItem对象中
                        BeanUtil.copyProperties(bookMallShoppingCartItemVO, bookMallOrderItem);
                        //BookMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        bookMallOrderItem.setOrderId(bookMallOrder.getOrderId());
                        bookMallOrderItems.add(bookMallOrderItem);
                    }
                    //保存至数据库
                    if (bookMallOrderItemMapper.insertBatch(bookMallOrderItems) > 0 && bookMallOrderAddressMapper.insertSelective(bookMallOrderAddress) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    BookMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                BookMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            BookMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        BookMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }


    @Override
    public PageResult getBookMallOrdersPage(PageQueryUtil pageUtil) {
        List<BookMallOrder> bookMallOrders = bookMallOrderMapper.findBookMallOrderList(pageUtil);
        int total = bookMallOrderMapper.getTotalBookMallOrders(pageUtil);
        PageResult pageResult = new PageResult(bookMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(BookMallOrder bookMallOrder) {
        BookMallOrder temp = bookMallOrderMapper.selectByPrimaryKey(bookMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(bookMallOrder.getTotalPrice());
            temp.setUpdateTime(new Date());
            if (bookMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<BookMallOrder> orders = bookMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (BookMallOrder bookMallOrder : orders) {
                if (bookMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += bookMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (bookMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += bookMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (bookMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<BookMallOrder> orders = bookMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (BookMallOrder bookMallOrder : orders) {
                if (bookMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += bookMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (bookMallOrder.getOrderStatus() != 1 && bookMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += bookMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (bookMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<BookMallOrder> orders = bookMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (BookMallOrder bookMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (bookMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += bookMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (bookMallOrder.getOrderStatus() == 4 || bookMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += bookMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间&&恢复库存
                if (bookMallOrderMapper.closeOrder(Arrays.asList(ids), BookMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0 && recoverStockNum(Arrays.asList(ids))) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public List<BookMallOrderItemVO> getOrderItems(Long orderId) {
        BookMallOrder bookMallOrder = bookMallOrderMapper.selectByPrimaryKey(orderId);
        if (bookMallOrder != null) {
            List<BookMallOrderItem> orderItems = bookMallOrderItemMapper.selectByOrderId(bookMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<BookMallOrderItemVO> bookMallOrderItemVOS = BeanUtil.copyList(orderItems, BookMallOrderItemVO.class);
                return bookMallOrderItemVOS;
            }
        }
        return null;
    }

    /**
     * 恢复库存
     *
     * @param orderIds
     * @return
     */
    public Boolean recoverStockNum(List<Long> orderIds) {
        //查询对应的订单项
        List<BookMallOrderItem> bookMallOrderItems = bookMallOrderItemMapper.selectByOrderIds(orderIds);
        //获取对应的商品id和商品数量并赋值到StockNumDTO对象中
        List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(bookMallOrderItems, StockNumDTO.class);
        //执行恢复库存的操作
        int updateStockNumResult = bookMallGoodsMapper.recoverStockNum(stockNumDTOS);
        if (updateStockNumResult < 1) {
            BookMallException.fail(ServiceResultEnum.CLOSE_ORDER_ERROR.getResult());
            return false;
        } else {
            return true;
        }
    }
}
