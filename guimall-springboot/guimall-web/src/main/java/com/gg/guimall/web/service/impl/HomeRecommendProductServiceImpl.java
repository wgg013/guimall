package com.gg.guimall.web.service.impl;

import com.gg.guimall.common.domain.dos.PmsProductDO;
import com.gg.guimall.common.domain.dos.SmsHomeRecommendProductDO;
import com.gg.guimall.common.domain.mapper.PmsProductMapper;
import com.gg.guimall.common.domain.mapper.SmsHomeRecommendProductMapper;
import com.gg.guimall.common.utils.Response;
import com.gg.guimall.web.model.vo.home.HomeRecommendProductItemVO;
import com.gg.guimall.web.service.HomeRecommendProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 首页人气推荐 Service 实现类（基于 sms_home_recommend_product 关系表）
 */
@Service
@Slf4j
public class HomeRecommendProductServiceImpl implements HomeRecommendProductService {

    @Autowired
    private PmsProductMapper pmsProductMapper;

    @Autowired
    private SmsHomeRecommendProductMapper smsHomeRecommendProductMapper;

    @Override
    public Response listRecommendProducts() {
        List<SmsHomeRecommendProductDO> relations = smsHomeRecommendProductMapper.selectActiveList();
        if (relations == null || relations.isEmpty()) {
            return Response.success(Collections.emptyList());
        }

        List<Long> productIds = relations.stream()
                .map(SmsHomeRecommendProductDO::getProductId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (productIds.isEmpty()) {
            return Response.success(Collections.emptyList());
        }

        Map<Long, PmsProductDO> productMap = pmsProductMapper.selectBatchIds(productIds).stream()
                .filter(p -> Objects.equals(p.getPublishStatus(), 1) && Objects.equals(p.getIsDeleted(), 0))
                .collect(Collectors.toMap(PmsProductDO::getId, p -> p));

        List<HomeRecommendProductItemVO> voList = relations.stream()
                .map(rel -> {
                    PmsProductDO p = productMap.get(rel.getProductId());
                    if (p == null) {
                        return null;
                    }
                    BigDecimal price = p.getPromotionPrice() != null ? p.getPromotionPrice() : p.getPrice();
                    HomeRecommendProductItemVO vo = new HomeRecommendProductItemVO();
                    vo.setId(rel.getId());
                    vo.setProductId(p.getId());
                    vo.setProductName(p.getName());
                    vo.setProductPic(p.getPic());
                    vo.setPrice(price);
                    vo.setSort(rel.getSort());
                    return vo;
                })
                .filter(Objects::nonNull)
                .limit(8)
                .collect(Collectors.toList());

        return Response.success(voList);
    }
}