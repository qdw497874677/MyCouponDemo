package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>fake 商品信息</h1>
 * Created by Qinyi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfo {

    /** 商品类型*/
    private Integer type;

    /** 商品价格 */
    private Double price;

    /** 商品数量 */
    private Integer count;

    // TODO 名称, 使用信息
}
