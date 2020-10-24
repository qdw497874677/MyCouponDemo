package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>用户优惠券的状态</h1>
 */
@Getter
@AllArgsConstructor
public enum CouponStatus {

    USABLE("可用的", 1),
    USED("已使用的", 2),
    EXPIRED("过期的(未被使用的)", 3);

    /** 优惠券状态描述信息 */
    private String description;

    /** 优惠券状态编码 */
    private Integer code;

    /**
     * <h2>根据 code 获取到 CouponStatus</h2>
     * */
    public static CouponStatus of(Integer code) {

        Objects.requireNonNull(code);

        return Stream.of(values())
                // 过滤等于code的枚举
                .filter(bean -> bean.code.equals(code))
                // 拿出一个
                .findAny()
                // 如果没有拿出来，就抛出一个异常
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}
