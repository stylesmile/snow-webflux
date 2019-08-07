package com.stylesmile.modules.system.service.impl;

import com.stylesmile.common.service.BaseServiceImpl;
import com.stylesmile.constant.CacheConstant;
import com.stylesmile.modules.system.entity.SysDepart;
import com.stylesmile.modules.system.mapper.SysDepartMapper;
import com.stylesmile.modules.system.service.SysDepartService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门管理
 *
 * @author chenye
 * @date 2019/1/8
 */
@Service("sysDepartService")
public class SysDepartServiceImpl extends BaseServiceImpl<SysDepartMapper, SysDepart> implements SysDepartService {
    /**
     * 查询部门
     *
     * @return List<SysDepart>
     */
    @Cacheable(value = CacheConstant.DEPART_LIST_CACHE)
    @Override
    public List<SysDepart> getList() {
        return baseMapper.getDepartList();
    }

    /**
     * 修改部门
     *
     * @param sysDepart 部门信息
     * @return Boolean
     */
    @Override
    public Boolean updateDepart(SysDepart sysDepart) {
        boolean b = baseMapper.updateDepart(sysDepart);
        //清除缓存
        this.clearDepartListCache();
        return b;
    }

    /**
     * 删除部门
     *
     * @param id 主键
     * @return Boolean
     */
    @Override
    public Boolean deleteDepart(String id) {
        Boolean b = baseMapper.deleteDepart(id);
        //清除缓存
        this.clearDepartListCache();
        return b;
    }

    /**
     * 清除部门缓存
     */
    @CacheEvict(value = CacheConstant.DEPART_LIST_CACHE)
    @Override
    public void clearDepartListCache() {
    }

}