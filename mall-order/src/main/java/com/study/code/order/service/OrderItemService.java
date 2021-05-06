package com.study.code.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.code.commons.util.PageUtils;
import com.study.code.order.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author suiweidong
 * @email 7334501@qq.com
 * @date 2021-05-06 02:12:14
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

