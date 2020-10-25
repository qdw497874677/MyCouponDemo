package com.imooc.coupon.vo;

import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.constant.PeriodType;
import com.imooc.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1>用户优惠券的分类, 根据优惠券状态</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {

    /** 可以使用的 */
    private List<Coupon> usable;

    /** 已使用的 */
    private List<Coupon> used;

    /** 已过期的 */
    private List<Coupon> expired;

    /**
     * <h2>对当前的优惠券进行分类</h2>
     * */
    public static CouponClassify classify(List<Coupon> coupons) {

        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(c -> {

            // 判断优惠券是否过期
            boolean isTimeExpire;
//            long curTime = new Date().getTime();
            long curTime = System.currentTimeMillis();

            if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(// 如果是不可变
                    PeriodType.REGULAR.getCode()
            )) {
                isTimeExpire = c.getTemplateSDK().getRule().getExpiration()
                        .getDeadline() <= curTime;
            } else {// 如果是可变
                // 领取时间加上gap，就是这个优惠券的过期时间，比较当前时间
                isTimeExpire = DateUtils.addDays(
                        c.getAssignTime(),
                        c.getTemplateSDK().getRule().getExpiration().getGap()
                ).getTime() <= curTime;

                // 再去比较最终的deadline
                isTimeExpire = isTimeExpire || c.getTemplateSDK().getRule().getExpiration().getDeadline() <= curTime;
            }

            if (c.getStatus() == CouponStatus.USED) {
                used.add(c);
            } else if (c.getStatus() == CouponStatus.EXPIRED || isTimeExpire) {// 把本来就是过期的和刚检测出过期的加入list
                expired.add(c);
            } else {
                usable.add(c);
            }
        });

        return new CouponClassify(usable, used, expired);
    }
}
