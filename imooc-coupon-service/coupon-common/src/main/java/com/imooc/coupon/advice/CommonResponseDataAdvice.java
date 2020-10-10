package com.imooc.coupon.advice;

import com.imooc.coupon.annotation.IgnoreResponseAdvice;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一响应
 * 对ResponseBody进行增强
 */
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    /**
     * <h2>判断是否需要对响应进行处理</h2>
     * */
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {

        // 如果当前方法所在的类标识了 @IgnoreResponseAdvice 注解, 不需要处理
        if (methodParameter.getDeclaringClass().isAnnotationPresent(
                IgnoreResponseAdvice.class
        )) {
            return false;
        }

        // 如果当前方法标识了 @IgnoreResponseAdvice 注解, 不需要处理
        if (methodParameter.getMethod().isAnnotationPresent(
                IgnoreResponseAdvice.class
        )) {
            return false;
        }

        // 对响应进行处理, 执行 beforeBodyWrite 方法
        return true;
    }

    /**
     * <h2>响应返回之前的处理</h2>
     * */
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {

        // 定义最终的返回对象
        CommonResponse<Object> response = new CommonResponse<>(
                0, ""
        );

        // 如果 o 是 null, response 不需要设置 data，直接返回对象
        if (null == o) {
            return response;
            // 在controller中可以去自行返回CommonResponse对象，如果是这种情况就这里就不再封装
            // 比如想返回code和message不是上面默认的，就自己在创建CommonResponse
        } else if (o instanceof CommonResponse) {
            response = (CommonResponse<Object>) o;
        } else {// 否则, 把响应对象作为 CommonResponse 的 data 部分
            response.setData(o);
        }

        return response;
    }
}
