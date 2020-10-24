package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>商品类型枚举</h1>
 */
@Getter
@AllArgsConstructor
public enum GoodsType {

    WENYU("文娱", 1),
    SHUMA("数码", 2),
    SHENGXIAN("生鲜", 3),
    JIAJU("家居", 4),
    OTHERS("其他", 5),
    ALL("全品类", 6);

    /** 商品类型描述 */
    private String description;

    /** 商品类型编码 */
    private Integer code;

    public static GoodsType of(Integer code) {

        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists!")
                );
    }
}
