package com.stylesmile.modules.system.service.impl;

import com.stylesmile.common.constant.CacheConstant;
import com.stylesmile.modules.system.entity.SysDepart;
import com.stylesmile.modules.system.repository.SysDepartRepository;
import com.stylesmile.modules.system.service.SysDepartService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

/**
 * 部门管理
 *
 * @author chenye
 * @date 2019/1/8
 */
@Service("sysDepartService")
public class SysDepartServiceImpl implements SysDepartService {
    @Resource
    SysDepartRepository sysDepartRepository;

    /**
     * 查询部门
     *
     * @return List<SysDepart>
     */
    @Cacheable(value = CacheConstant.deptCache.DEPART_LIST_CACHE)
    @Override
    public List<SysDepart> getList(String source) {
        List<SysDepart> sysDepartList = sysDepartRepository.getDepartList();
        if ("user_dept".equals(source)) {
            for (SysDepart dept : sysDepartList) {

            }
        }
        return sysDepartList;
    }

    /**
     * 修改部门
     *
     * @param sysDepart 部门信息
     * @return Boolean
     */
    @Override
    public Boolean updateDepart(SysDepart sysDepart) {
        boolean b = sysDepartRepository.updateDepart(sysDepart);
        //清除缓存
        this.clearDepartListCache();
        this.clearDept(sysDepart.getId());
        return b;
    }

    /**
     * 删除部门
     *
     * @param id 主键
     * @return Boolean
     */
    @Override
    public Boolean deleteDepart(int id) {
        Boolean b = sysDepartRepository.deleteDepart(id);
        //清除缓存
        this.clearDepartListCache();
        this.clearDept(id);
        return b;
    }

    /**
     * 清除部门缓存
     */
    @CacheEvict(value = CacheConstant.deptCache.DEPART_LIST_CACHE)
    @Override
    public void clearDepartListCache() {
    }

    /**
     * 通过id 获取部门信息
     */

    @Cacheable(value = CacheConstant.deptCache.DEPART_CACHE, key = "#id")
    @Override
    public SysDepart getDeptById(Integer id) {
        return null;
    }

    /**
     * 清理单个部门缓存
     */
    @CacheEvict(value = CacheConstant.deptCache.DEPART_CACHE, key = "#id")
    @Override
    public void clearDept(Integer id) {
    }

    @Override
    public Mono<SysDepart> save(SysDepart depart) {
        return sysDepartRepository.save(depart);
    }

    @Override
    public Mono<SysDepart> updateById(SysDepart sysDepart) {
        return sysDepartRepository.save(sysDepart);
    }

}
