
package com.book.mall.api.mall;

import com.book.mall.api.admin.param.BatchIdParam;
import com.book.mall.api.mall.param.FavorAddParam;
import com.book.mall.api.mall.param.FavorEditParam;
import com.book.mall.api.mall.vo.BookMallIndexFavorVO;
import com.book.mall.api.mall.vo.BookMallShoppingCartItemVO;
import com.book.mall.common.ServiceResultEnum;
import com.book.mall.config.annotation.TokenToAdminUser;
import com.book.mall.config.annotation.TokenToMallUser;
import com.book.mall.entity.AdminUserToken;
import com.book.mall.entity.Favor;
import com.book.mall.entity.MallUser;
import com.book.mall.service.BookMallFavorService;
import com.book.mall.util.BeanUtil;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.Result;
import com.book.mall.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "9-1.收藏模块接口")
@RequestMapping("/api/v1")
public class BookFavorAPI {

    private static final Logger logger = LoggerFactory.getLogger(BookFavorAPI.class);

    @Resource
    BookMallFavorService bookMallFavorService;

//    /**
//     * 列表
//     */
//    @RequestMapping(value = "/favors", method = RequestMethod.GET)
//    @ApiOperation(value = "收藏列表", notes = "收藏列表")
//    public Result list(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
//                       @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize, @TokenToAdminUser AdminUserToken adminUser) {
//        logger.info("adminUser:{}", adminUser.toString());
//        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
//            return ResultGenerator.genFailResult("分页参数异常！");
//        }
//        Map params = new HashMap(4);
//        params.put("page", pageNumber);
//        params.put("limit", pageSize);
//        PageQueryUtil pageUtil = new PageQueryUtil(params);
//        return ResultGenerator.genSuccessResult(bookMallFavorService.getFavorPage(pageUtil));
//    }

    @GetMapping("/favors")
    @ApiOperation(value = "收藏列表(网页移动端不分页)", notes = "")
    public Result<List<BookMallIndexFavorVO>> favorList(@TokenToMallUser MallUser loginMallUser) {
        return ResultGenerator.genSuccessResult(bookMallFavorService.getFavorByUserId(loginMallUser.getUserId()));
    }
    /**
     * 添加
     */
    @RequestMapping(value = "/favors", method = RequestMethod.POST)
    @ApiOperation(value = "新增收藏", notes = "新增收藏")
    public Result save(@RequestBody @Valid FavorAddParam favorAddParam, @TokenToMallUser MallUser loginMallUser) {
        logger.info("loginMallUser:{}", loginMallUser.toString());
        Favor favor = new Favor();
        favorAddParam.setUserId(loginMallUser.getUserId());
        BeanUtil.copyProperties(favorAddParam, favor);
        String result = bookMallFavorService.saveFavor(favor);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 删除
     */
    @DeleteMapping("/favors/{goodsId}")
    @ApiOperation(value = "批量删除收藏信息", notes = "批量删除收藏信息")
    public Result delete(@PathVariable("goodsId") Long goodsId, @TokenToMallUser MallUser loginMallUser) {
        logger.info("loginMallUser:{}", loginMallUser.toString());
        if (!loginMallUser.getUserId().equals(loginMallUser.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        Boolean deleteResult = bookMallFavorService.deleteByGoodsId(goodsId,loginMallUser.getUserId());
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

}