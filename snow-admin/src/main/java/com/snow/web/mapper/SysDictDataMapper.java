package com.snow.web.mapper;

import com.snow.common.biz.IMapper;
import org.apache.ibatis.annotations.Param;
import com.snow.common.core.domain.entity.SysDictData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 字典表 数据层
 * 
 * @author ruoyi
 */
public interface SysDictDataMapper extends IMapper<SysDictData, Long>
{
    /**
     * 根据主键ID查询
     *
     * @param id 主键ID
     * @return 返回数据对象
     */
    @Override
    public Mono<SysDictData> selectById(Long id);

    /**
     * 分页查询
     *
     * @param where 条件
     * @return 查询结果
     */
    @Override
    public Flux<SysDictData> selectList(SysDictData where);

    /**
     * 根据字典类型查询字典数据
     * 
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    public Flux<SysDictData> selectDictDataByType(String dictType);

    /**
     * 根据字典类型和字典键值查询字典数据信息
     * 
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    public Mono<String> selectDictLabel(@Param("dictType") String dictType, @Param("dictValue") String dictValue);

    /**
     * 查询字典数据
     * 
     * @param dictType 字典类型
     * @return 字典数据
     */
    public Mono<Long> countDictDataByType(String dictType);


    /**
     * 同步修改字典类型
     * 
     * @param oldDictType 旧字典类型
     * @param newDictType 新旧字典类型
     * @return 结果
     */
    public Mono<Long> updateDictDataType(@Param("oldDictType") String oldDictType, @Param("newDictType") String newDictType);
}
