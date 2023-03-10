
package com.book.mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.book.mall.common.Constants;
import com.book.mall.common.IndexConfigTypeEnum;
import com.book.mall.api.mall.vo.IndexInfoVO;
import com.book.mall.api.mall.vo.BookMallIndexCarouselVO;
import com.book.mall.api.mall.vo.BookMallIndexConfigGoodsVO;
import com.book.mall.service.BookMallCarouselService;
import com.book.mall.service.BookMallIndexConfigService;
import com.book.mall.util.Result;
import com.book.mall.util.ResultGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(value = "v1", tags = "1.图书商城首页接口")
@RequestMapping("/api/v1")
public class BookMallIndexAPI {

    @Resource
    private BookMallCarouselService bookMallCarouselService;

    @Resource
    private BookMallIndexConfigService bookMallIndexConfigService;

    @GetMapping("/index-infos")
    @ApiOperation(value = "获取首页数据", notes = "轮播图、新品、推荐等")
    public Result<IndexInfoVO> indexInfo() {
        IndexInfoVO indexInfoVO = new IndexInfoVO();
        List<BookMallIndexCarouselVO> carousels = bookMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<BookMallIndexConfigGoodsVO> hotGoodses = bookMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<BookMallIndexConfigGoodsVO> newGoodses = bookMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<BookMallIndexConfigGoodsVO> recommendGoodses = bookMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        indexInfoVO.setCarousels(carousels);
        indexInfoVO.setHotGoodses(hotGoodses);
        indexInfoVO.setNewGoodses(newGoodses);
        indexInfoVO.setRecommendGoodses(recommendGoodses);
        return ResultGenerator.genSuccessResult(indexInfoVO);
    }
}
