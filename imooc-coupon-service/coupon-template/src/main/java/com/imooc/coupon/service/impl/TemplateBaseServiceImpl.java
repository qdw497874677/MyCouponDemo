package com.imooc.coupon.service.impl;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <h1>优惠券模板基础服务接口实现</h1>
 */
@Slf4j
@Service
public class TemplateBaseServiceImpl implements ITemplateBaseService {

    /** CouponTemplate Dao */
    private final CouponTemplateDao templateDao;

    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /**
     * <h2>根据优惠券模板 id 获取优惠券模板信息</h2>
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        // 查找模板信息
        Optional<CouponTemplate> template = templateDao.findById(id);
        if (!template.isPresent()) {
            throw new CouponException("Template Is Not Exist: " + id);
        }

        return template.get();
    }

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     * @return {@link CouponTemplateSDK}s
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        // 模板状态可用并且未过期，就是可用的
        List<CouponTemplate> templates =
                templateDao.findAllByAvailableAndExpired(
                        true, false);


        // 用流操作的map对每个元素类型转换映射，然后放到list集合中
        return templates.stream()
                .map(this::template2TemplateSDK).collect(Collectors.toList());
    }

    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     * @param ids 模板 ids
     * @return Map<key: 模板 id, value: CouponTemplateSDK>
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(
            Collection<Integer> ids) {

        List<CouponTemplate> templates = templateDao.findAllById(ids);

        // 最终转换为Map
        return templates.stream().map(this::template2TemplateSDK)
                .collect(Collectors.toMap(
                        CouponTemplateSDK::getId, Function.identity()// 这个表示本身
                ));
    }

    /**
     * <h2>将 CouponTemplate 转换为 CouponTemplateSDK</h2>
     * */
    private CouponTemplateSDK template2TemplateSDK(CouponTemplate template) {
        String id = template.getId()+"";
        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
//                template.getKey(),  // 并不是拼装好的 Template Key
                template.getKey()+id.substring(id.length()-4),  // 也可以拼装好
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
