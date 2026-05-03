package com.gg.guimall.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gg.guimall.admin.model.vo.pms.*;
import com.gg.guimall.admin.service.PmsProductService;
import com.gg.guimall.admin.service.TraceQrcodeService;
import com.gg.guimall.common.domain.dos.PmsProductDO;
import com.gg.guimall.common.domain.dos.PmsProductCategoryDO;
import com.gg.guimall.common.domain.dos.PmsProductParamDO;
import com.gg.guimall.common.domain.dos.PmsParamDefinitionDO;
import com.gg.guimall.common.domain.dos.PmsFarmerDO;
import com.gg.guimall.common.domain.dos.PmsSkuSpecDO;
import com.gg.guimall.common.domain.dos.PmsSkuStockDO;
import com.gg.guimall.common.domain.mapper.PmsFarmerMapper;
import com.gg.guimall.common.domain.mapper.PmsProductCategoryMapper;
import com.gg.guimall.common.domain.mapper.PmsProductMapper;
import com.gg.guimall.common.domain.mapper.PmsProductParamMapper;
import com.gg.guimall.common.domain.mapper.PmsParamDefinitionMapper;
import com.gg.guimall.common.domain.mapper.PmsSkuSpecMapper;
import com.gg.guimall.common.domain.mapper.PmsSkuStockMapper;
import com.gg.guimall.common.enums.ResponseCodeEnum;
import com.gg.guimall.common.exception.BizException;
import com.gg.guimall.common.utils.PageResponse;
import com.gg.guimall.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 闁哥喎妫楅幖褏绮婚敍鍕€?Service 閻庡湱鍋熼獮鍥╃尵?
 *
 * 閻犳劗鍠曢惌妤呭疮閸℃鎯傞柣銊ュ椤ゅ啴宕氶悩铏毉闁?
 *
 * @author wly
 */
@Service("adminPmsProductServiceImpl")
@Slf4j
public class PmsProductServiceImpl implements PmsProductService {

    @Autowired
    private PmsProductMapper pmsProductMapper;
    @Autowired
    private PmsProductCategoryMapper pmsProductCategoryMapper;
    @Autowired
    private PmsFarmerMapper pmsFarmerMapper;
    @Autowired
    private PmsProductParamMapper pmsProductParamMapper;
    @Autowired
    private PmsParamDefinitionMapper pmsParamDefinitionMapper;
    @Autowired
    private PmsSkuStockMapper pmsSkuStockMapper;
    @Autowired
    private PmsSkuSpecMapper pmsSkuSpecMapper;
    @Autowired
    private TraceQrcodeService traceQrcodeService;

    /**
     * 闁告帗绋戠紓鎾诲疮閸℃鎯?
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response createProduct(PmsProductCreateReqVO reqVO) {
        // 闁哄稄绻濋悰娆撳疮閸℃鎯傞柛鎺戞鐞氼偊寮伴姘剨閻庢稒锚濠€?
        if (Objects.nonNull(reqVO.getProductCategoryId())) {
            PmsProductCategoryDO categoryDO = pmsProductCategoryMapper.selectById(reqVO.getProductCategoryId());
            if (Objects.isNull(categoryDO)) {
                throw new BizException(ResponseCodeEnum.PRODUCT_CATEGORY_NOT_FOUND);
            }
        }

        // 闁哄稄绻濋悰娆撳礃濠婂嫬鐓曢柡鍕靛灠閹胶鈧稒锚濠€?
        if (Objects.nonNull(reqVO.getFarmerId())) {
            PmsFarmerDO farmerDO = pmsFarmerMapper.selectById(reqVO.getFarmerId());
            if (Objects.isNull(farmerDO)) {
                throw new BizException(ResponseCodeEnum.FARMER_NOT_FOUND);
            }
        }

        // 闁哄瀚紓鎾诲疮閸℃鎯?DO
        PmsProductDO productDO = PmsProductDO.builder()
                .productCategoryId(reqVO.getProductCategoryId())
                .farmerId(reqVO.getFarmerId())
                .name(reqVO.getName())
                .subTitle(reqVO.getSubTitle())
                .productSn(reqVO.getProductSn())
                .pic(reqVO.getPic())
                .albumPics(normalizeAlbumPics(reqVO.getAlbumPics(), reqVO.getAlbumPicList()))
                .description(reqVO.getDescription())
                .price(reqVO.getPrice())
                .marketPrice(reqVO.getMarketPrice())
                .stock(reqVO.getStock())
                .unit(reqVO.getUnit())
                .weight(reqVO.getWeight())
                .keywords(reqVO.getKeywords())
                .note(reqVO.getNote())
                .detailHtml(reqVO.getDetailHtml())
                .publishStatus(0) // 濮掓稒顭堥缁樼▔鐎ｎ偆浠搁柣妯垮煐閳?
                .isNew(reqVO.getIsNew() != null ? reqVO.getIsNew() : 0)
                .isRecommend(reqVO.getIsRecommend() != null ? reqVO.getIsRecommend() : 0)
                .isAidAgriculture(reqVO.getIsAidAgriculture() != null ? reqVO.getIsAidAgriculture() : 0)
                .isDeleted(0) // 濮掓稒顭堥濠氬嫉椤忓嫬鐏╅梻?
                .sort(reqVO.getSort() != null ? reqVO.getSort() : 0)
                .build();

        // 闁圭粯甯掗崣鍡涘极閻楀牆绁﹂幖?
        pmsProductMapper.insert(productDO);

        // 濞ｅ洦绻傞悺銊╁疮閸℃鎯傞柛娆忓€归弳?
        saveProductParams(productDO.getId(), reqVO.getProductParams());

        // 濞ｅ洦绻傞悺鈯縆U閹煎瓨鎸搁悺?
        saveSkuStockList(productDO.getId(), reqVO.getSkuStockList());

        // 闁煎浜滄慨鈺呮偨閻旂鐏囨繝褜鍨辩花顔界瀹€鈧ǎ顕€鎯?
        try {
            traceQrcodeService.generate(productDO.getId());
            log.info("闁哥喎妫楅幖褔宕氬☉妯肩处闁瑰瓨鍔曟慨娑㈡晬鐏炶棄鍤掗柤濂変簻婵晠鎮介悢绋跨亣婵犙屽灡缁喗绂嶅畝鈧ǎ顕€鎯嶆笟濠勭productId: {}", productDO.getId());
        } catch (Exception e) {
            log.error("闁煎浜滄慨鈺呮偨閻旂鐏囨繝褜鍨辩花顔界瀹€鈧ǎ顕€鎯嶆担鎼炰杭閻犳劑鍎荤槐婕皉oductId: {}", productDO.getId(), e);
            // 濞戞挸绉存總鏍传瀹ュ懏娅岄柛婵呯閸ㄥ崬顕欓悮瀵哥闁告瑯浜ｉ鍥亹閺囩喐锛夐煫?
        }

        return Response.success(productDO.getId());
    }

    /**
     * 闁哥喎妫楅幖褔宕氶崱娑栤偓澶愬蓟閵夘煈鍤勯柨娑樼墕閸亞鎮伴妸鈹库偓宥夊触椤愩垹褰犻柤杈ㄦ煥閻秶绮堥悮瀵哥獥闁告帒妫涚悮顐﹀触瀹ュ啠鍋撴担绋挎櫩闁规潙鍢查幃鏇㈠Υ娴ｅ摜娼ｉ柟顑啫鐎荤紒顐ヮ嚙閹洟鏁?
     */
    @Override
    public PageResponse findProductPageList(FindPmsProductPageListReqVO reqVO) {

        Page<PmsProductDO> page = pmsProductMapper.selectPageList(
                reqVO.getCurrent(),
                reqVO.getSize(),
                reqVO.getName(),
                reqVO.getCategoryId(),
                reqVO.getPublishStatus()
        );

        if (page.getRecords().isEmpty()) {
            return PageResponse.success(page, Collections.emptyList());
        }

        // 闁归潧缍婇崳娲蓟閵夘煈鍤勯柛鎺戞鐞氼偊宕?
        List<Long> catIds = page.getRecords().stream()
                .map(PmsProductDO::getProductCategoryId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> catNameMap = catIds.isEmpty() ? Collections.emptyMap() :
                pmsProductCategoryMapper.selectBatchIds(catIds).stream()
                        .collect(Collectors.toMap(PmsProductCategoryDO::getId, PmsProductCategoryDO::getName));

        // 闁归潧缍婇崳娲蓟閵夘煈鍤勯柛鎰矋閸╂盯宕?
        List<Long> farmerIds = page.getRecords().stream()
                .map(PmsProductDO::getFarmerId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> farmerNameMap = farmerIds.isEmpty() ? Collections.emptyMap() :
                pmsFarmerMapper.selectBatchIds(farmerIds).stream()
                        .collect(Collectors.toMap(PmsFarmerDO::getId, PmsFarmerDO::getName));

        List<FindPmsProductPageListRspVO> voList = page.getRecords().stream()
                .map(product -> FindPmsProductPageListRspVO.builder()
                        .id(product.getId())
                        .productCategoryId(product.getProductCategoryId())
                        .categoryName(catNameMap.get(product.getProductCategoryId()))
                        .farmerId(product.getFarmerId())
                        .farmerName(farmerNameMap.get(product.getFarmerId()))
                        .name(product.getName())
                        .subTitle(product.getSubTitle())
                        .productSn(product.getProductSn())
                        .pic(product.getPic())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .publishStatus(product.getPublishStatus())
                        .isAidAgriculture(product.getIsAidAgriculture())
                        .sale(product.getSale())
                        .createTime(product.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return PageResponse.success(page, voList);
    }

    /**
     * 闁哄被鍎撮妤呭疮閸℃鎯傞悹鍥烽檮閸庡繘鏁嶉崼婵囧創闁稿繐鐤囨禒鍫沪閺囩姰浠涢柨娑欒壘閸ㄥ海鐚剧拠鍙夊€崇紒澶庡焽閳ь兛绀侀崯姗€骞嬪畡鐗堝€崇紒澶婂簻缁?
     */
    @Override
    public Response findProductDetail(Long id) {

        if (Objects.isNull(id) || id <= 0) {
            throw new BizException(ResponseCodeEnum.INVALID_PRODUCT_DATA);
        }

        PmsProductDO productDO = pmsProductMapper.selectById(id);

        if (Objects.isNull(productDO)) {
            throw new BizException(ResponseCodeEnum.PRODUCT_NOT_FOUND);
        }

        FindPmsProductDetailRspVO rspVO = new FindPmsProductDetailRspVO();
        BeanUtils.copyProperties(productDO, rspVO);
        rspVO.setAlbumPicList(parseAlbumPicList(productDO.getAlbumPics()));

        // 闁稿繐鐤囨禒鍫ュ蓟閵夘煈鍤勬鐐舵硾閿濈偤宕楅崨顓炵€荤紒顐ヮ嚙閹洜绮?
        if (Objects.nonNull(productDO.getProductCategoryId())) {
            PmsProductCategoryDO category = pmsProductCategoryMapper.selectById(productDO.getProductCategoryId());
            if (category != null) {
                rspVO.setCategoryName(category.getName());
            }
        }

        // 闁稿繐鐤囨禒鍫ュ蓟閵夘煈鍤勬鐐舵硾閿濈偤宕楅崨顓炴櫩闁规潙鍢查幃鏇犵矓?
        if (Objects.nonNull(productDO.getFarmerId())) {
            PmsFarmerDO farmer = pmsFarmerMapper.selectById(productDO.getFarmerId());
            if (farmer != null) {
                rspVO.setFarmerName(farmer.getName());
            }
        }

        // 闁哄被鍎撮妤呭疮閸℃鎯傞柛娆忓€归弳鐔兼晬閸繂褰犻柤杈ㄦ煥瀵剟寮弶璺ㄦ毎濞戞柨顦抽妴鍐箯閸喖妫橀柡浣规緲閹洟宕仦钘夋闁轰焦婢橀埀顒傘€嬬槐?
        List<PmsProductParamDO> paramDOs = pmsProductParamMapper.selectByProductId(id);
        if (!CollectionUtils.isEmpty(paramDOs)) {
            // 闁归潧缍婇崳娲蓟閵夘煈鍤勯柛娆忓€归弳鐔衡偓瑙勭煯缁犵喖鏁嶉崼婵嗙樁闁告凹鍋勫顒勫极閺夋寧鍊抽柛婊冭嫰瀵剟寮弶搴撳亾绾绀?
            List<Long> paramIds = paramDOs.stream()
                    .map(PmsProductParamDO::getParamId).distinct().collect(Collectors.toList());
            Map<Long, PmsParamDefinitionDO> paramDefMap = paramIds.isEmpty() ? Collections.emptyMap() :
                    pmsParamDefinitionMapper.selectBatchIds(paramIds).stream()
                            .collect(Collectors.toMap(PmsParamDefinitionDO::getId, p -> p));

            List<ProductParamItemVO> paramVOs = paramDOs.stream()
                    .map(p -> {
                        PmsParamDefinitionDO def = paramDefMap.get(p.getParamId());
                        return ProductParamItemVO.builder()
                                .paramId(p.getParamId())
                                .key(def != null ? def.getParamName() : "")
                                .value(def != null ? def.getParamValue() : "")
                                .build();
                    })
                    .collect(Collectors.toList());
            rspVO.setProductParams(paramVOs);
        }

        return Response.success(rspVO);
    }

    /**
     * 濞ｅ浂鍠楅弫濂稿疮閸℃鎯?
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateProduct(PmsProductUpdateReqVO reqVO) {
        // 闁告瑥鍊归弳鐔煎冀閿熺姷宕?
        if (Objects.isNull(reqVO.getId()) || reqVO.getId() <= 0) {
            throw new BizException(ResponseCodeEnum.INVALID_PRODUCT_DATA);
        }

        // 闁哄稄绻濋悰娆撳疮閸℃鎯傞柡鍕靛灠閹胶鈧稒锚濠€?
        PmsProductDO existProduct = pmsProductMapper.selectById(reqVO.getId());
        if (Objects.isNull(existProduct)) {
            throw new BizException(ResponseCodeEnum.PRODUCT_NOT_FOUND);
        }

        // 闁哄稄绻濋悰娆撳疮閸℃鎯傞柛鎺戞鐞氼偊寮伴姘剨閻庢稒锚濠€?
        if (Objects.nonNull(reqVO.getProductCategoryId())) {
            PmsProductCategoryDO categoryDO = pmsProductCategoryMapper.selectById(reqVO.getProductCategoryId());
            if (Objects.isNull(categoryDO)) {
                throw new BizException(ResponseCodeEnum.PRODUCT_CATEGORY_NOT_FOUND);
            }
        }

        // 闁哄稄绻濋悰娆撳礃濠婂嫬鐓曢柡鍕靛灠閹胶鈧稒锚濠€?
        if (Objects.nonNull(reqVO.getFarmerId())) {
            PmsFarmerDO farmerDO = pmsFarmerMapper.selectById(reqVO.getFarmerId());
            if (Objects.isNull(farmerDO)) {
                throw new BizException(ResponseCodeEnum.FARMER_NOT_FOUND);
            }
        }

        PmsProductDO productDO = PmsProductDO.builder()
                .id(reqVO.getId())
                .productCategoryId(reqVO.getProductCategoryId())
                .farmerId(reqVO.getFarmerId())
                .name(reqVO.getName())
                .subTitle(reqVO.getSubTitle())
                .productSn(reqVO.getProductSn())
                .pic(reqVO.getPic())
                .albumPics(normalizeAlbumPics(reqVO.getAlbumPics(), reqVO.getAlbumPicList()))
                .description(reqVO.getDescription())
                .price(reqVO.getPrice())
                .marketPrice(reqVO.getMarketPrice())
                .stock(reqVO.getStock())
                .unit(reqVO.getUnit())
                .weight(reqVO.getWeight())
                .keywords(reqVO.getKeywords())
                .note(reqVO.getNote())
                .detailHtml(reqVO.getDetailHtml())
                .isNew(reqVO.getIsNew())
                .isRecommend(reqVO.getIsRecommend())
                .isAidAgriculture(reqVO.getIsAidAgriculture() != null ? reqVO.getIsAidAgriculture() : existProduct.getIsAidAgriculture())
                .publishStatus(reqVO.getPublishStatus())
                .sort(reqVO.getSort())
                .updateTime(LocalDateTime.now())
                .build();

        pmsProductMapper.updateById(productDO);

        // 闁哄洤鐡ㄩ弻濠囧疮閸℃鎯傞柛娆忓€归弳鐔兼晬閸繂甯ラ柛鎺斿Т閹骞撻幒鐐电
        saveProductParams(reqVO.getId(), reqVO.getProductParams());

        return Response.success();
    }

    @Override
    public Response deleteProduct(Long id) {

        // 闁告瑥鍊归弳鐔煎冀閿熺姷宕?
        if (Objects.isNull(id) || id <= 0) {
            throw new BizException(ResponseCodeEnum.INVALID_PRODUCT_DATA);
        }

        // 闁哄稄绻濋悰娆撳疮閸℃鎯傞柡鍕靛灠閹胶鈧稒锚濠€?
        PmsProductDO productDO = pmsProductMapper.selectById(id);
        if (Objects.isNull(productDO)) {
            throw new BizException(ResponseCodeEnum.PRODUCT_NOT_FOUND);
        }

        // 闁圭瑳鍡╂斀闁告帞濞€濞?
        pmsProductMapper.deleteById(id);

        return Response.success();
    }

    /**
     * 濞戞挸锕ラ悘锕傚疮閸℃鎯?
     */
    @Override
    public Response publishProduct(Long id) {
        return updatePublishStatus(id, 1);
    }

    /**
     * 濞戞挸顑嗛悘锕傚疮閸℃鎯?
     */
    @Override
    public Response unpublishProduct(Long id) {
        return updatePublishStatus(id, 0);
    }

    /**
     * 设为助农商品
     */
    @Override
    public Response enableAidAgriculture(Long id) {
        return updateAidAgricultureStatus(id, 1);
    }

    /**
     * 取消助农商品
     */
    @Override
    public Response disableAidAgriculture(Long id) {
        return updateAidAgricultureStatus(id, 0);
    }

    /**
     * 闁哄洤鐡ㄩ弻濠冪▔婵犲啰浠搁柣妯垮煐閳?
     */
    private Response updatePublishStatus(Long id, Integer publishStatus) {
        // 闁告瑥鍊归弳鐔煎冀閿熺姷宕?
        if (Objects.isNull(id) || id <= 0) {
            throw new BizException(ResponseCodeEnum.INVALID_PRODUCT_DATA);
        }

        // 闁哄稄绻濋悰娆撳疮閸℃鎯傞柡鍕靛灠閹胶鈧稒锚濠€?
        PmsProductDO productDO = pmsProductMapper.selectById(id);
        if (Objects.isNull(productDO)) {
            throw new BizException(ResponseCodeEnum.PRODUCT_NOT_FOUND);
        }

        // 闁哄洤鐡ㄩ弻濠冪▔婵犲啰浠搁柣妯垮煐閳?
        PmsProductDO updateDO = PmsProductDO.builder()
                .id(id)
                .publishStatus(publishStatus)
                .build();
        pmsProductMapper.updateById(updateDO);

        return Response.success();
    }

    /**
     * 更新助农状态
     */
    private Response updateAidAgricultureStatus(Long id, Integer isAidAgriculture) {
        if (Objects.isNull(id) || id <= 0) {
            throw new BizException(ResponseCodeEnum.INVALID_PRODUCT_DATA);
        }

        PmsProductDO productDO = pmsProductMapper.selectById(id);
        if (Objects.isNull(productDO)) {
            throw new BizException(ResponseCodeEnum.PRODUCT_NOT_FOUND);
        }

        PmsProductDO updateDO = PmsProductDO.builder()
                .id(id)
                .isAidAgriculture(isAidAgriculture)
                .updateTime(LocalDateTime.now())
                .build();
        pmsProductMapper.updateById(updateDO);
        return Response.success();
    }

    /**
     * 闁归潧缍婇崳娲礆閻樼粯鐝熼柛鐔锋閹?
     */
    @Override
    public Response batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        pmsProductMapper.deleteBatchIds(ids);
        return Response.success();
    }

    /**
     * 闁归潧缍婇崳娲即鐎涙ɑ鐓€濞戞挸锕ラ悘锕傛偐閼哥鍋?
     */
    @Override
    public Response batchUpdatePublishStatus(List<Long> ids, Integer publishStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        for (Long id : ids) {
            PmsProductDO update = new PmsProductDO();
            update.setId(id);
            update.setPublishStatus(publishStatus);
            pmsProductMapper.updateById(update);
        }
        return Response.success();
    }

    /**
     * 濞ｅ洦绻傞悺銊╁疮閸℃鎯傞柛娆忓€归弳鐔兼晬閸繂甯ラ柛鎺斿Т閹骞撻幒鐐电
     */
    private void saveProductParams(Long productId, List<ProductParamItemVO> productParams) {
        // 闁稿繐鐗嗛崹褰掓⒔閵堝棙锛嬮柛娆忓€归弳?
        pmsProductParamMapper.deleteByProductId(productId);

        if (CollectionUtils.isEmpty(productParams)) {
            System.out.println("No product params to save, skip.");
            return;
        }

        System.out.println("Saving product params, productId: " + productId + ", count: " + productParams.size());

        // 闁归潧缍婇崳娲箵閹烘垵寮抽柡鍌涙緲瀵剟寮?
        for (int i = 0; i < productParams.size(); i++) {
            ProductParamItemVO item = productParams.get(i);
            System.out.println("Param item " + i + ": paramId=" + item.getParamId() + ", key=" + item.getKey() + ", value=" + item.getValue());

            if (item.getParamId() == null) {
                System.out.println("Param item paramId is null, skip.");
                continue;
            }

            PmsProductParamDO paramDO = PmsProductParamDO.builder()
                    .productId(productId)
                    .paramId(item.getParamId())
                    .sort(i)
                    .build();
            pmsProductParamMapper.insert(paramDO);
            System.out.println("Param persisted: " + paramDO);
        }
    }
    /**
     * 濞ｅ洦绻傞悺鈯縆U閹煎瓨鎸搁悺銊╁礆濡ゅ嫨鈧?
     */
    private void saveSkuStockList(Long productId, List<PmsProductCreateReqVO.SkuStockItemVO> skuStockList) {
        if (CollectionUtils.isEmpty(skuStockList)) {
            log.warn("SKU stock list is empty, skip save. productId={}", productId);
            return;
        }

        log.info("Saving SKU stock list, productId: {}, count: {}", productId, skuStockList.size());

        for (PmsProductCreateReqVO.SkuStockItemVO skuItem : skuStockList) {
            List<PmsProductCreateReqVO.SkuSpecItemVO> specItems = resolveSkuSpecs(skuItem);

            // 閹兼潙绻愰崹顏堝礌閺嶎剦娼愰柡宥呭悑閺嗙喖骞戦鏄忕JSON
            String spDataJson = null;
            if (!CollectionUtils.isEmpty(specItems)) {
                try {
                    // 闁哄瀚紓鎻揝ON闁哄秶鍘х槐? [{"key":"閻熸瑥瀚悧鎼佸触?,"value":"閻熸瑥瀚悧鎼佸磹?}]
                    StringBuilder jsonBuilder = new StringBuilder("[");
                    for (int i = 0; i < specItems.size(); i++) {
                        PmsProductCreateReqVO.SkuSpecItemVO spec = specItems.get(i);
                        if (i > 0) jsonBuilder.append(",");
                        jsonBuilder.append("{\"key\":\"").append(spec.getSpecKey())
                                .append("\",\"value\":\"").append(spec.getSpecValue()).append("\"}");
                    }
                    jsonBuilder.append("]");
                    spDataJson = jsonBuilder.toString();
                } catch (Exception e) {
                    log.error("Failed to build SKU spec JSON, skuCode={}", skuItem.getSkuCode(), e);
                }
            }

            PmsSkuStockDO skuStockDO = PmsSkuStockDO.builder()
                    .productId(productId)
                    .skuCode(skuItem.getSkuCode())
                    .price(skuItem.getPrice())
                    .stock(skuItem.getStock())
                    .promotionPrice(skuItem.getPromotionPrice())
                    .lowStock(skuItem.getLowStock() != null ? skuItem.getLowStock() : 0)
                    .lockStock(0)
                    .pic(skuItem.getPic())
                    .sale(0)
                    .spData(spDataJson)
                    .build();

            pmsSkuStockMapper.insert(skuStockDO);
            log.info("SKU persisted: skuCode={}, price={}, stock={}", skuItem.getSkuCode(), skuItem.getPrice(), skuItem.getStock());

            // 鍚屾鍐欏叆 SKU 瑙勬牸琛紝淇濊瘉鏂板鍚庡湪鍓嶅悗鍙伴兘鑳芥纭鍙栬鏍?
            if (!CollectionUtils.isEmpty(specItems)) {
                int sort = 0;
                for (PmsProductCreateReqVO.SkuSpecItemVO spec : specItems) {
                    if (spec == null) {
                        continue;
                    }
                    String specValue = spec.getSpecValue();
                    if (specValue == null || specValue.trim().isEmpty()) {
                        continue;
                    }
                    pmsSkuSpecMapper.insert(PmsSkuSpecDO.builder()
                            .skuId(skuStockDO.getId())
                            .productId(productId)
                            .specKey(spec.getSpecKey())
                            .specValue(specValue.trim())
                            .sort(sort++)
                            .build());
                }
            }
        }
    }

    private List<PmsProductCreateReqVO.SkuSpecItemVO> resolveSkuSpecs(PmsProductCreateReqVO.SkuStockItemVO skuItem) {
        List<PmsProductCreateReqVO.SkuSpecItemVO> normalized = new ArrayList<>();
        if (!CollectionUtils.isEmpty(skuItem.getSpecs())) {
            for (PmsProductCreateReqVO.SkuSpecItemVO spec : skuItem.getSpecs()) {
                if (spec == null) {
                    continue;
                }
                String specKey = trimToNull(spec.getSpecKey());
                String specValue = trimToNull(spec.getSpecValue());
                if (specKey == null && specValue == null) {
                    continue;
                }
                normalized.add(PmsProductCreateReqVO.SkuSpecItemVO.builder()
                        .specKey(specKey)
                        .specValue(specValue)
                        .build());
            }
        }

        String fallbackSpecKey = trimToNull(skuItem.getSpecKey());
        String fallbackSpecValue = trimToNull(skuItem.getSpecValue());
        if (fallbackSpecKey != null || fallbackSpecValue != null) {
            boolean existed = normalized.stream().anyMatch(item ->
                    Objects.equals(item.getSpecKey(), fallbackSpecKey)
                            && Objects.equals(item.getSpecValue(), fallbackSpecValue));
            if (!existed) {
                normalized.add(PmsProductCreateReqVO.SkuSpecItemVO.builder()
                        .specKey(fallbackSpecKey)
                        .specValue(fallbackSpecValue)
                        .build());
            }
        }

        return normalized;
    }

    private String trimToNull(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
    private String normalizeAlbumPics(String albumPics, List<String> albumPicList) {
        if (!CollectionUtils.isEmpty(albumPicList)) {
            List<String> cleanedList = albumPicList.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(item -> !item.isEmpty())
                    .collect(Collectors.toList());
            return cleanedList.isEmpty() ? null : String.join(",", cleanedList);
        }
        if (albumPics == null) {
            return null;
        }
        String normalized = albumPics.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private List<String> parseAlbumPicList(String albumPics) {
        if (albumPics == null || albumPics.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return java.util.Arrays.stream(albumPics.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }
}
