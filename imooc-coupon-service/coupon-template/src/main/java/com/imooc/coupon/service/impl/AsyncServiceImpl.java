package com.imooc.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.service.IAsyncService;
import com.sun.jmx.remote.internal.ArrayQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <h1>异步服务接口实现</h1>
 * Created by Qinyi.
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    /** CouponTemplate Dao */
    private final CouponTemplateDao templateDao;

    /** 注入 Redis 模板类 */
    private final StringRedisTemplate redisTemplate;

    // 基于构造函数的自动注入方式
    @Autowired
    public AsyncServiceImpl(CouponTemplateDao templateDao,
                            StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }

    /**
     * <h2>根据模板异步的创建优惠券码</h2>
     * @param template {@link CouponTemplate} 优惠券模板实体
     */
    @Async("getAsyncExecutor")
    @Override
    @SuppressWarnings("all")
    public void asyncConstructCouponByTemplate(CouponTemplate template) {

        Stopwatch watch = Stopwatch.createStarted();
        // 这里就是模板对应的所有优惠券码
        Set<String> couponCodes = buildCouponCode(template);

        // 把优惠券码放到Redis中，用list存储
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());
        log.info("Push CouponCode To Redis: {}",
                redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));
        // 修改模板的状态为可用
        template.setAvailable(true);
        templateDao.save(template);

        watch.stop();
        log.info("创建优惠券码，并且存储到Redis中 总耗时: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        // TODO 发送短信或者邮件通知优惠券模板已经可用
        log.info("CouponTemplate({}) Is Available!", template.getId());
    }

    @Override
    public void asyncSendEmails(String s) {
        // TODO 去做邮件通知
    }

    /**
     * <h2>构造优惠券码</h2>
     * 优惠券码(对应于每一张优惠券, 18位)
     *  前四位: 产品线 + 类型
     *  中间六位: 日期随机(190101)
     *  后八位: 0 ~ 9 随机数构成
     * @param template {@link CouponTemplate} 实体类
     * @return Set<String> 与 template.count 相同个数的优惠券码
     * */
    @SuppressWarnings("all")
    private Set<String> buildCouponCode(CouponTemplate template) {
        // 用来记录耗时
        Stopwatch watch = Stopwatch.createStarted();

        Set<String> result = new HashSet<>(template.getCount());
        Map<Integer,Integer> map = new LinkedHashMap<>();
        Queue<Integer> deap = new PriorityQueue<>((a,b)->{
            int x = map.get(a);
            int y = map.get(b);
            if (x==y){
                return a-b;
            }
            return y-x;
        });
        deap.add(1);

        deap.poll();
        // 前四位
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd")
                .format(template.getCreateTime());

        // 先用for去创建一定数量优惠券
        for (int i = 0; i != template.getCount(); ++i) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        // 用while去补充之前创建重复的
        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        assert result.size() == template.getCount();

        watch.stop();
        log.info("Build Coupon Code Cost: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }

    /**
     * <h2>构造优惠券码的后 14 位</h2>
     * @param date 创建优惠券的日期
     * @return 14 位优惠券码
     * */
    private String buildCouponCodeSuffix14(String date) {
        // 对日期进行洗牌，加上随机后八位
        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};

        // 中间六位
        List<Character> chars = date.chars()
                .mapToObj(e -> (char) e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6 = chars.stream()
                .map(Object::toString).collect(Collectors.joining());

        // 后八位
        String suffix8 = RandomStringUtils.random(1, bases)
                + RandomStringUtils.randomNumeric(7);

        return mid6 + suffix8;
    }
}
