package com.stylesmile.modules.system.repository;

import com.stylesmile.modules.system.entity.SysDepart;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chenye
 * @date 2018/11/18
 */
@Repository
public interface SysDepartRepository  extends ReactiveCrudRepository<SysDepart,Integer> {
    /**
     * 查询全部部门
     *
     * @return list
     */
    @Query("select * from sys_user")
    List<SysDepart> getDepartList();

    /**
     * 更新部门
     *
     * @param sysDepart 部门
     * @return Boolean
     */
    @Query("select * from sys_user")
    Boolean updateDepart(SysDepart sysDepart);

    /**
     * 删除
     *
     * @param id 主键
     * @return Boolean
     */
    @Query("select * from sys_user")
    Boolean deleteDepart(int id);
}
