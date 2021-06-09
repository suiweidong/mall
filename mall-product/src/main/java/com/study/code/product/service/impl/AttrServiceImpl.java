package com.study.code.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.study.code.commons.constant.ProductConstant;
import com.study.code.product.entity.*;
import com.study.code.product.mapper.AttrAttrgroupRelationMapper;
import com.study.code.product.mapper.AttrGroupMapper;
import com.study.code.product.mapper.CategoryMapper;
import com.study.code.product.service.AttrAttrgroupRelationService;
import com.study.code.product.service.CategoryService;
import com.study.code.product.vo.AttrReqVO;
import com.study.code.product.vo.AttrResVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.code.commons.util.PageUtils;
import com.study.code.commons.util.Query;

import com.study.code.product.mapper.AttrMapper;
import com.study.code.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrMapper, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationMapper attrAttrgroupRelationMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private AttrGroupMapper attrGroupMapper;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    @Transactional
    public void saveAttr(AttrReqVO attrVO) {
        // 保存属性信息
        AttrEntity attr = new AttrEntity();
        BeanUtil.copyProperties(attrVO, attr);
        this.save(attr);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 基本信息 保存关联信息
            AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
            relation.setAttrId(attr.getAttrId());
            relation.setAttrGroupId(attrVO.getAttrGroupId());
            this.attrAttrgroupRelationMapper.insert(relation);
        }
    }

    @Override
    public PageUtils queryAttrList(Map<String, Object> params, Long catelogId, String type) {
        log.info("queryAttrList--------->params----------->\n" + JSON.toJSONString(params, SerializerFeature.PrettyFormat));
        log.info("queryAttrList--------->catelogId----------->：{}", catelogId);
        log.info("queryAttrList--------->attrType----------->：{}", type);

        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equals(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }

        String key = MapUtil.getStr(params, "key");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.eq("attr_id", key).or().like("attr_name", key);
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        List<AttrEntity> attrs = page.getRecords();
        List<AttrResVO> result = attrs.stream().map(attr -> {
            AttrResVO attrResVO = new AttrResVO();
            BeanUtil.copyProperties(attr, attrResVO);
            // 所属分类
            CategoryEntity category = this.categoryMapper.selectById(attr.getCatelogId());
            if (ObjectUtil.isNotEmpty(category)) {
                attrResVO.setCatelogName(category.getName());
            }

            if ("base".equals(type)) {
                // 基本信息 所属分组
                AttrAttrgroupRelationEntity attrGroupRelation = this.attrAttrgroupRelationMapper.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
                if (ObjectUtil.isNotEmpty(attrGroupRelation)) {
                    AttrGroupEntity attrGroup = this.attrGroupMapper.selectById(attrGroupRelation.getAttrGroupId());
                    if (ObjectUtil.isNotEmpty(attrGroup)) {
                        attrResVO.setGroupName(attrGroup.getAttrGroupName());
                    }
                }
            }

            return attrResVO;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(result);

        log.info("queryAttrList------result-------->\n" + JSON.toJSONString(pageUtils, SerializerFeature.PrettyFormat));
        return pageUtils;
    }


    @Override
    public AttrResVO queryAttrInfo(Long attrId) {
        log.info("queryAttrInfo--------->attrId----------->：{}", attrId);

        AttrEntity attr = this.getById(attrId);
        AttrResVO attrRes = new AttrResVO();
        BeanUtil.copyProperties(attr, attrRes);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            // 基本信息 获取所属分组信息
            AttrAttrgroupRelationEntity attrRelation = this.attrAttrgroupRelationMapper.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (ObjectUtil.isNotEmpty(attrRelation)) {
                attrRes.setAttrGroupId(attrRelation.getAttrGroupId());
                AttrGroupEntity attrGroup = this.attrGroupMapper.selectById(attrRelation.getAttrGroupId());
                if (ObjectUtil.isNotEmpty(attrGroup)) {
                    attrRes.setGroupName(attrGroup.getAttrGroupName());
                }
            }
        }

        // 获取所属分类信息 全路径
        Long[] categoryPath = this.categoryService.getCategoryPath(attr.getCatelogId());
        attrRes.setCatelogPath(categoryPath);
        CategoryEntity category = this.categoryMapper.selectById(attr.getCatelogId());
        attrRes.setCatelogName(category.getName());

        log.info("queryAttrInfo------result-------->\n" + JSON.toJSONString(attrRes, SerializerFeature.PrettyFormat));
        return attrRes;
    }

    @Override
    @Transactional
    public void updateAttr(AttrReqVO attrVO) {
        log.info("updateAttr--------->attrVO----------->\n" + JSON.toJSONString(attrVO, SerializerFeature.PrettyFormat));

        // 修改属性信息
        AttrEntity attrNew = new AttrEntity();
        BeanUtil.copyProperties(attrVO, attrNew);
        this.updateById(attrNew);

        if (attrNew.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            // 修改关联信息
            AttrAttrgroupRelationEntity attrGroupRelation = this.attrAttrgroupRelationMapper.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVO.getAttrId()));
            Long oldGroupId = attrGroupRelation.getAttrGroupId() == null ? -1 : attrGroupRelation.getAttrGroupId();
            Long newGroupId = attrVO.getAttrGroupId() == null ? -1 : attrVO.getAttrGroupId();
            if (ObjectUtil.isNotEmpty(attrGroupRelation) && oldGroupId.longValue() != newGroupId.longValue()) {
                log.info("updateAttr-------->关联信息不为空且关联信息变化，更新操作--->Old：{}---New：{}", oldGroupId, newGroupId);
                attrGroupRelation.setAttrGroupId(attrVO.getAttrGroupId());
                this.attrAttrgroupRelationMapper.updateAttrgroupRelation(attrGroupRelation);
            }
        }
    }
}