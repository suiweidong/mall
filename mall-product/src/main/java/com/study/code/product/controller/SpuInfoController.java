package com.study.code.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.study.code.commons.vo.product.SpuSaveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.study.code.product.entity.SpuInfoEntity;
import com.study.code.product.service.SpuInfoService;
import com.study.code.commons.util.PageUtils;
import com.study.code.utils.util.R;



/**
 * spu信息
 *
 * @author suiweidong
 * @email 7334501@qq.com
 * @date 2021-05-06 03:13:44
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 商品发布保存
     */
    @PostMapping("/save")
    public R spuInfoSave(@RequestBody SpuSaveVO spuSaveVO){

		spuInfoService.spuInfoSave(spuSaveVO);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
