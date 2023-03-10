
package com.book.mall.service;

import com.book.mall.api.mall.vo.BookMallOrderDetailVO;
import com.book.mall.api.mall.vo.BookMallOrderItemVO;
import com.book.mall.api.mall.vo.BookMallShoppingCartItemVO;
import com.book.mall.entity.MallUser;
import com.book.mall.entity.MallUserAddress;
import com.book.mall.entity.BookMallOrder;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.PageResult;

import java.util.List;

public interface BookMallOrderService {
    /**
     * 获取订单详情
     *
     * @param orderId
     * @return
     */
    BookMallOrderDetailVO getOrderDetailByOrderId(Long orderId);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    BookMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * 手动取消订单
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    String paySuccess(String orderNo, int payType);

    String saveOrder(MallUser loginMallUser, MallUserAddress address, List<BookMallShoppingCartItemVO> itemsForSave);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getBookMallOrdersPage(PageQueryUtil pageUtil);

    /**
     * 订单信息修改
     *
     * @param bookMallOrder
     * @return
     */
    String updateOrderInfo(BookMallOrder bookMallOrder);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    List<BookMallOrderItemVO> getOrderItems(Long orderId);
}
