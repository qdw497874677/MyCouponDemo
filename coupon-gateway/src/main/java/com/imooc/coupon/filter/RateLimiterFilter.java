package com.imooc.coupon.filter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 限流过滤器
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class RateLimiterFilter extends AbstractPreZuulFilter {
    // 通过缓存每个IP的限流器，设置超时时间。
    LoadingCache<String, RateLimiter> ipRequestCaches = CacheBuilder.newBuilder()
            .maximumSize(1000)// 设置缓存个数
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, RateLimiter>() {
                @Override
                public RateLimiter load(String s) throws Exception {
                    return RateLimiter.create(1000.0);// 新的IP初始化 (限流每秒0.1个令牌响应,即10s一个令牌)
                }
            });

    // 每秒可以获取到两个令牌
//    RateLimiter rateLimiter = RateLimiter.create(2.0);

    @Override
    protected Object cRun() {
        // 拿到request
        HttpServletRequest request = context.getRequest();

        //1. 对所有请求限流
        // 尝试获取令牌
//        if (rateLimiter.tryAcquire()) {
//            log.info("get rate token success");
//            return success();
//        } else {
//            log.error("rate limit: {}", request.getRequestURI());
//            return fail(402, "error: rate limit");
//        }
        //2. 对单个ip的请求限流
        try {
            String ip = request.getRemoteAddr();
            RateLimiter rateLimiter = ipRequestCaches.get(ip);
            if (rateLimiter.tryAcquire()){
                log.info("ip:"+ip+" 通过");
                return success();
            }else {
                log.info("ip:"+ip+" 被限流");
                return fail(402,"error: 限流");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * filterOrder() must also be defined for a filter. Filters may have the same  filterOrder if precedence is not
     * important for a filter. filterOrders do not need to be sequential.
     *
     * @return the int order of a filter
     */
    @Override
    public int filterOrder() {
        return 2;
    }
}
