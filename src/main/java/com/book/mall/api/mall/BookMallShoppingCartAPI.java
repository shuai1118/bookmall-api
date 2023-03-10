
package com.book.mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.book.mall.api.mall.param.SaveCartItemParam;
import com.book.mall.api.mall.param.UpdateCartItemParam;
import com.book.mall.common.Constants;
import com.book.mall.common.BookMallException;
import com.book.mall.common.ServiceResultEnum;
import com.book.mall.config.annotation.TokenToMallUser;
import com.book.mall.api.mall.vo.BookMallShoppingCartItemVO;
import com.book.mall.entity.MallUser;
import com.book.mall.entity.BookMallShoppingCartItem;
import com.book.mall.service.BookMallShoppingCartService;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.PageResult;
import com.book.mall.util.Result;
import com.book.mall.util.ResultGenerator;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "5.图书商城购物车相关接口")
@RequestMapping("/api/v1")
public class BookMallShoppingCartAPI {

    @Resource
    private BookMallShoppingCartService bookMallShoppingCartService;

    @GetMapping("/shop-cart/page")
    @ApiOperation(value = "购物车列表(每页默认5条)", notes = "传参为页码")
    public Result<PageResult<List<BookMallShoppingCartItemVO>>> cartItemPageList(Integer pageNumber, @TokenToMallUser MallUser loginMallUser) {
        Map params = new HashMap(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("userId", loginMallUser.getUserId());
        params.put("page", pageNumber);
        params.put("limit", Constants.SHOPPING_CART_PAGE_LIMIT);
        //封装分页请求参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(bookMallShoppingCartService.getMyShoppingCartItems(pageUtil));
    }

    @GetMapping("/shop-cart")
    @ApiOperation(value = "购物车列表(网页移动端不分页)", notes = "")
    public Result<List<BookMallShoppingCartItemVO>> cartItemList(@TokenToMallUser MallUser loginMallUser) {
        return ResultGenerator.genSuccessResult(bookMallShoppingCartService.getMyShoppingCartItems(loginMallUser.getUserId()));
    }

    @PostMapping("/shop-cart")
    @ApiOperation(value = "添加商品到购物车接口", notes = "传参为商品id、数量")
    public Result saveBookMallShoppingCartItem(@RequestBody SaveCartItemParam saveCartItemParam,
                                                 @TokenToMallUser MallUser loginMallUser) {
        String saveResult = bookMallShoppingCartService.saveBookMallCartItem(saveCartItemParam, loginMallUser.getUserId());
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ApiOperation(value = "修改购物项数据", notes = "传参为购物项id、数量")
    public Result updateBookMallShoppingCartItem(@RequestBody UpdateCartItemParam updateCartItemParam,
                                                   @TokenToMallUser MallUser loginMallUser) {
        String updateResult = bookMallShoppingCartService.updateBookMallCartItem(updateCartItemParam, loginMallUser.getUserId());
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{bookMallShoppingCartItemId}")
    @ApiOperation(value = "删除购物项", notes = "传参为购物项id")
    public Result updateBookMallShoppingCartItem(@PathVariable("bookMallShoppingCartItemId") Long bookMallShoppingCartItemId,
                                                   @TokenToMallUser MallUser loginMallUser) {
        BookMallShoppingCartItem bookMallCartItemById = bookMallShoppingCartService.getBookMallCartItemById(bookMallShoppingCartItemId);
        if (!loginMallUser.getUserId().equals(bookMallCartItemById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        Boolean deleteResult = bookMallShoppingCartService.deleteById(bookMallShoppingCartItemId,loginMallUser.getUserId());
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    @ApiOperation(value = "根据购物项id数组查询购物项明细", notes = "确认订单页面使用")
    public Result<List<BookMallShoppingCartItemVO>> toSettle(Long[] cartItemIds, @TokenToMallUser MallUser loginMallUser) {
        if (cartItemIds.length < 1) {
            BookMallException.fail("参数异常");
        }
        int priceTotal = 0;
        List<BookMallShoppingCartItemVO> itemsForSettle = bookMallShoppingCartService.getCartItemsForSettle(Arrays.asList(cartItemIds), loginMallUser.getUserId());
        if (CollectionUtils.isEmpty(itemsForSettle)) {
            //无数据则抛出异常
            BookMallException.fail("参数异常");
        } else {
            //总价
            for (BookMallShoppingCartItemVO bookMallShoppingCartItemVO : itemsForSettle) {
                priceTotal += bookMallShoppingCartItemVO.getGoodsCount() * bookMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                BookMallException.fail("价格异常");
            }
        }
        return ResultGenerator.genSuccessResult(itemsForSettle);
    }
}
