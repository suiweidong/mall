package com.study.code.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.code.commons.util.PageUtils;
import com.study.code.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author suiweidong
 * @email 7334501@qq.com
 * @date 2021-05-06 03:13:44
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

