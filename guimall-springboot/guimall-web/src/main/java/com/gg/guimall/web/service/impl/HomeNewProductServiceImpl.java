package com.gg.guimall.web.service.impl;

import com.gg.guimall.common.domain.dos.PmsProductDO;
import com.gg.guimall.common.domain.dos.SmsHomeNewProductDO;
import com.gg.guimall.common.domain.mapper.PmsProductMapper;
import com.gg.guimall.common.domain.mapper.SmsHomeNewProductMapper;
import com.gg.guimall.common.utils.Response;
import com.gg.guimall.web.model.vo.home.HomeNewProductItemVO;
import com.gg.guimall.web.service.HomeNewProductService;
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
 * 首页新品推荐 Service 实现类（基于 sms_home_new_product 关系表）
 */
@Service
@Slf4j
public class HomeNewProductServiceImpl implements HomeNewProductService {

    @Autowired
    private PmsProductMapper pmsProductMapper;

    @Autowired
    private SmsHomeNewProductMapper smsHomeNewProductMapper;

    @Override
    public Response listNewProducts() {
        List<SmsHomeNewProductDO> relations = smsHomeNewProductMapper.selectActiveList();
        if (relations == null || relations.isEmpty()) {
            return Response.success(Collections.emptyList());
        }

        List<Long> productIds = relations.stream()
                .map(SmsHomeNewProductDO::getProductId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (productIds.isEmpty()) {
            return Response.success(Collections.emptyList());
        }

        Map<Long, PmsProductDO> productMap = pmsProductMapper.selectBatchIds(productIds).stream()
                .filter(p -> Objects.equals(p.getPublishStatus(), 1) && Objects.equals(p.getIsDeleted(), 0))
                .collect(Collectors.toMap(PmsProductDO::getId, p -> p));

        List<HomeNewProductItemVO> voList = relations.stream()
                .map(rel -> {
                    PmsProductDO p = productMap.get(rel.getProductId());
                    if (p == null) {
                        return null;
                    }
                    BigDecimal price = p.getPromotionPrice() != null ? p.getPromotionPrice() : p.getPrice();
                    return HomeNewProductItemVO.builder()
                            .id(rel.getId())
                            .productId(p.getId())
                            .productName(p.getName())
                            .productPic(p.getPic())
                            .price(price)
                            .sort(rel.getSort())
                            .build();
                })
                .filter(Objects::nonNull)
                .limit(8)
                .collect(Collectors.toList());

        return Response.success(voList);
    }
}