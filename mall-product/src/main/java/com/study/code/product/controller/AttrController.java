package com.study.code.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.study.code.commons.vo.product.AttrReqVO;
import com.study.code.commons.vo.product.AttrResVO;
import com.study.code.product.entity.ProductAttrValueEntity;
import com.study.code.product.service.ProductAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.study.code.product.service.AttrService;
import com.study.code.commons.util.PageUtils;
import com.study.code.utils.util.R;


/**
 * 商品属性
 *
 * @author suiweidong
 * @email 7334501@qq.com
 * @date 2021-05-06 03:13:45
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 规格参数及销售属性列表
     */
    @GetMapping("/{type}/list/{catelogId}")
    public R attrList(@RequestParam Map<String, Object> params,
                      @PathVariable("catelogId") Long catelogId,
                      @PathVariable("type") String type) {
        PageUtils page = attrService.queryAttrList(params, catelogId, type);

        return R.ok().put("page", page);
    }

    /**
     * 规格维护，反显
     * @param spuId
     * @return
     */
    @RequestMapping("/base/listforspu/{spuId}")
    public R listforspu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> productAttrValue= this.productAttrValueService.listforspu(spuId);

        return R.ok().put("data", productAttrValue);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {

        AttrResVO attrRes = this.attrService.queryAttrInfo(attrId);

        return R.ok().put("attr", attrRes);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody AttrReqVO attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrReqVO attrVO) {

        attrService.updateAttr(attrVO);

        return R.ok();
    }

    /**
     * 规格维护-修改规格维护
     */
    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable Long spuId, @RequestBody List<ProductAttrValueEntity> productAttrValues) {

        productAttrValueService.updateSpuAttr(spuId, productAttrValues);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
