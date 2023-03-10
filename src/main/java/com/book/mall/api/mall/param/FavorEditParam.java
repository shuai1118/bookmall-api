
package com.book.mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class FavorEditParam {

    @ApiModelProperty("待修改收藏id")
    @NotNull(message = "收藏id不能为空")
    @Min(1)
    private Integer favorId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("商品id")
    private Long goodsId;

}
