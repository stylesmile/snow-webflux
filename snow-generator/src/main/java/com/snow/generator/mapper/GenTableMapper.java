package com.snow.generator.mapper;

import com.snow.common.biz.IMapper;
import com.snow.generator.domain.GenTable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 业务 数据层
 *
 * @author ruoyi
 */
public interface GenTableMapper extends IMapper<GenTable, Long> {
    /**
     * 根据主键ID查询
     *
     * @param id 主键ID
     * @return 返回数据对象
     */
    @Override
    public Mono<GenTable> selectById(Long id);

    /**
     * 分页查询
     *
     * @param where 条件
     * @return 查询结果
     */
    @Override
    public Flux<GenTable> selectList(GenTable where);

    /**
     * 查询据库列表总数
     *
     * @param genTable 业务信息
     * @return 总数
     */
    public Mono<Long> countDbTableList(GenTable genTable);

    /**
     * 查询据库列表
     *
     * @param genTable 业务信息
     * @return 数据库表集合
     */
    public Flux<GenTable> selectDbTableList(GenTable genTable);

    /**
     * 查询据库列表
     *
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    public Flux<GenTable> selectDbTableListByNames(String[] tableNames);

    /**
     * 查询所有表信息
     *
     * @return 表信息集合
     */
    public Flux<GenTable> selectGenTableAll();

    /**
     * 查询表名称业务信息
     *
     * @param tableName 表名称
     * @return 业务信息
     */
    public Mono<GenTable> selectGenTableByName(String tableName);

}
