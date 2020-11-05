package com.imooc.coupon.controller;

import com.imooc.coupon.annotation.IgnoreResponseAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <h1>Ribbon 应用 Controller</h1>
 */
@Slf4j
@RestController
public class RibbonController {

    /** rest 客户端 */
    private final RestTemplate restTemplate;

    @Autowired
    public RibbonController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * <h2>通过 Ribbon 组件调用模板微服务</h2>
     * /coupon-distribution/info
     * */
    @GetMapping("/info")
    // 为了避免新的包装类TemplateInfo，在controller中返回时在被通用处理，这里通过这个注解来忽略包装
    @IgnoreResponseAdvice
    public TemplateInfo getTemplateInfo() {
        // url中ip地址的位置用被调用服务的服务应用名代替，后面是调用http接口的路径。
        String infoUrl = "http://eureka-client-coupon-template" +
                "/coupon-template/info";

        infoUrl = "http://eureka-client-coupon-template/coupon-template/template/sdk/all";
        // 调用http接口的返回值为通用返回类型，这里把Ribbon返回值中的body反序列化成TemplateInfo（泛型转换成了list）
        return restTemplate.getForEntity(infoUrl, TemplateInfo.class).getBody();
    }


    @GetMapping("/sdk")
    @IgnoreResponseAdvice
    public ArrayList getTemplatesdk() {
        // url中ip地址的位置用被调用服务的服务应用名代替，后面是调用http接口的路径。
        String infoUrl = "http://eureka-client-coupon-template/coupon-template/template/sdk/all";
        // 调用http接口的返回值为通用返回类型，这里把Ribbon返回值中的body反序列化成TemplateInfo（泛型转换成了list）
        return restTemplate.getForEntity(infoUrl, ArrayList.class).getBody();
    }

    /**
     * <h2>模板微服务的元信息</h2>
     * */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateInfo {

        private Integer code;
        private String message;
        private List<Map<String, Object>> data;
    }
}
