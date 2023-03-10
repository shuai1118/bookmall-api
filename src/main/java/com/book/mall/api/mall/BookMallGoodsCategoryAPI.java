
package com.book.mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.book.mall.common.BookMallException;
import com.book.mall.common.ServiceResultEnum;
import com.book.mall.api.mall.vo.BookMallIndexCategoryVO;
import com.book.mall.service.BookMallCategoryService;
import com.book.mall.util.Result;
import com.book.mall.util.ResultGenerator;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(value = "v1", tags = "3.图书商城分类页面接口")
@RequestMapping("/api/v1")
public class BookMallGoodsCategoryAPI {

    @Resource
    private BookMallCategoryService bookMallCategoryService;

    @GetMapping("/categories")
    @ApiOperation(value = "获取分类数据", notes = "分类页面使用")
    public Result<List<BookMallIndexCategoryVO>> getCategories() {
        List<BookMallIndexCategoryVO> categories = bookMallCategoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            BookMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(categories);
    }
}
