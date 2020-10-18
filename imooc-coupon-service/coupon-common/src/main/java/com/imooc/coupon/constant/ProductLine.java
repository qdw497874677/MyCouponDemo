package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>产品线枚举</h1>
 */
@Getter
@AllArgsConstructor
public enum ProductLine {

    ZHIFU("支付", 1),
    LICAI("理财", 2),
    BAOXIAN("保险", 2);

    /** 产品线描述 */
    private String description;

    /** 产品线编码 */
    private Integer code;

    public static ProductLine of(Integer code) {

        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists!"));
    }
}
