package com.imooc.coupon.executor.impl;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.ExecuteManager;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @PackageName:com.imooc.coupon.executor.impl
 * @ClassName: MultExecutor
 * @Description:
 * @date: 2020/10/30 0030 15:49
 */
public class MultExecutor extends AbstractExecutor implements RuleExecutor {

    private final Map<CouponCategory,Integer> map = new HashMap<>();
    {
        map.put(CouponCategory.LIJIAN,1);
        map.put(CouponCategory.MANJIAN,2);
        map.put(CouponCategory.ZHEKOU,3);
    }

    @Override
    public RuleFlag ruleConfig() {
        return null;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        List<CouponTemplateSDK> templates = settlement.getCouponAndTemplateInfos().stream()
                .map(SettlementInfo.CouponAndTemplateInfo::getTemplate)
                .sorted(Comparator.comparingInt(map::get))
                .collect(Collectors.toList());
        Map<RuleFlag, RuleExecutor> executorIndex = ExecuteManager.executorIndex;

        return null;
    }

}
