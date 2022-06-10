package com.snow.system.mapper;

import java.util.List;

import com.snow.common.biz.IMapper;
import org.apache.ibatis.annotations.Param;
import com.snow.common.core.domain.entity.SysDept;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 部门管理 数据层
 * 
 * @author ruoyi
 */
public interface SysDeptMapper extends IMapper<SysDept, Long>
{
    /**
     * 根据主键ID查询
     *
     * @param id 主键ID
     * @return 返回数据对象
     */
    @Override
    public Mono<SysDept> selectById(Long id);

    /**
     * 分页查询
     *
     * @param where 条件
     * @return 查询结果
     */
    @Override
    public Flux<SysDept> selectList(SysDept where);

    /**
     * 根据角色ID查询部门树信息
     * 
     * @param roleId 角色ID
     * @param deptCheckStrictly 部门树选择项是否关联显示
     * @return 选中部门列表
     */
    public Flux<Long> selectDeptListByRoleId(@Param("roleId") Long roleId, @Param("deptCheckStrictly") boolean deptCheckStrictly);

    /**
     * 根据ID查询所有子部门
     * 
     * @param deptId 部门ID
     * @return 部门列表
     */
    public Flux<SysDept> selectChildrenDeptById(Long deptId);

    /**
     * 根据ID查询所有子部门（正常状态）
     * 
     * @param deptId 部门ID
     * @return 子部门数
     */
    public Mono<Long> selectNormalChildrenDeptById(Long deptId);

    /**
     * 是否存在子节点
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    public Mono<Long> hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    public Mono<Long> checkDeptExistUser(Long deptId);

    /**
     * 校验部门名称是否唯一
     * 
     * @param deptName 部门名称
     * @param parentId 父部门ID
     * @return 结果
     */
    public Mono<SysDept> checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") Long parentId);


    /**
     * 修改所在部门正常状态
     * 
     * @param deptIds 部门ID组
     */
    public Mono<Long> updateDeptStatusNormal(Long[] deptIds);

    /**
     * 修改子元素关系
     * 
     * @param depts 子元素
     * @return 结果
     */
    public Mono<Long> updateDeptChildren(@Param("depts") List<SysDept> depts);

}
