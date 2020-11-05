package com.imooc.coupon.feign;

import com.imooc.coupon.config.FeignConfiguration;
import com.imooc.coupon.controller.RibbonController;
import com.imooc.coupon.feign.hystrix.TemplateClientHystrix;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.CouponTemplateSDK;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模板微服务 Feign 接口定义</h1>
 */
@FeignClient(value = "eureka-client-coupon-template", configuration = FeignConfiguration.class
//@FeignClient(value = "192.168.123.82:7001", configuration = FeignConfiguration.class
//@FeignClient(value = "EUREKA-CLIENT-COUPON-TEMPLATE",
//        fallback = TemplateClientHystrix.class)
        )
public interface TemplateClient {

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     * */
    @RequestMapping(value = "/coupon-template/template/sdk/all",
//    @RequestMapping(value = "/template/sdk/all",
            method = RequestMethod.GET)
    // 默认http接口提供的返回值通过CommonResponse包裹的，这里确定里面的data的类型
    CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate();

    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     * */
    @RequestMapping(value = "/coupon-template/template/sdk/infos",
            method = RequestMethod.GET)
    CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids
    );


    @RequestMapping(value = "/coupon-template/info",
            method = RequestMethod.GET)
    CommonResponse<RibbonController.TemplateInfo> getinfo();
}
