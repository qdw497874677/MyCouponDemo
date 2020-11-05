package com.imooc.coupon.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.feign.TemplateClient;
import com.imooc.coupon.vo.AcquireTemplateRequest;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.http.EurekaApplications;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <h1>用户服务功能测试用例</h1>
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class UserServiceTest {

    /** mock 一个 UserId */
    private Long mockUserId = 20001L;

    @Autowired
    private IUserService userService;
    @Autowired
    private TemplateClient templateClient;
    @Autowired
    /** 服务发现客户端 */
    private DiscoveryClient client;

    @Test
    public void test() throws Exception{
        List<String> services = client.getServices();
        System.out.println(services);
        System.out.println(client.description());
        for (String service : services) {
            ServiceInstance serviceInstance = client.getInstances(service).get(0);
            System.out.println(serviceInstance.getHost());
            System.out.println(serviceInstance.getUri());
            System.out.println(serviceInstance.getPort());
            System.out.println(serviceInstance.getServiceId());
            System.out.println();
        }

    }

    @Test
    public void test111(){
        System.out.println(templateClient.findAllUsableTemplate());
//        System.out.println(templateClient.getinfo());
    }

    @Test
    public void testgetC() throws CouponException {



//        CommonResponse<Map<Integer, CouponTemplateSDK>> ids2TemplateSDK = templateClient.findIds2TemplateSDK(Arrays.asList(1));
//        System.out.println(ids2TemplateSDK);
        List<CouponTemplateSDK> availableTemplate = userService.findAvailableTemplate(20001L);
        if (availableTemplate.isEmpty()){
            log.info("没有可领优惠券");
            return;
        }
        CouponTemplateSDK couponTemplateSDK = availableTemplate.get(0);
        String s = JSON.toJSONString(couponTemplateSDK);
        System.out.println(s);
        Coupon coupon = userService.acquireCoupon(new AcquireTemplateRequest(mockUserId, availableTemplate.get(0)));
        System.out.println(coupon);
    }

    @Test
    public void testFindCouponByStatus() throws CouponException {

        System.out.println(JSON.toJSONString(
                userService.findCouponsByStatus(
                        mockUserId,
                        CouponStatus.USABLE.getCode()
                )
        ));
        System.out.println(JSON.toJSONString(
                userService.findCouponsByStatus(
                        mockUserId,
                        CouponStatus.USED.getCode()
                )
        ));
        System.out.println(JSON.toJSONString(
                userService.findCouponsByStatus(
                        mockUserId,
                        CouponStatus.EXPIRED.getCode()
                )
        ));
    }

    @Test
    public void testFindAvailableTemplate() throws CouponException {

        System.out.println(JSON.toJSONString(
                userService.findAvailableTemplate(mockUserId)
        ));
    }
}
