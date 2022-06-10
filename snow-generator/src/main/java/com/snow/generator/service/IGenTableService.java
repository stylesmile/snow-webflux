package com.snow.generator.service;

import com.snow.common.biz.IService;
import com.snow.common.core.domain.model.LoginUser;
import com.snow.common.core.page.TableDataInfo;
import com.snow.generator.domain.GenTable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 业务 服务层
 *
 * @author ruoyi
 */
public interface IGenTableService extends IService<GenTable, Long> {

	/**
	 * 查询据库列表
	 *
	 * @param genTable 业务信息
	 * @return 数据库表集合
	 */
	public Mono<TableDataInfo> selectDbTableList(GenTable genTable);

	/**
	 * 查询据库列表
	 *
	 * @param tableNames 表名称组
	 * @return 数据库表集合
	 */
	public Mono<List<GenTable>> selectDbTableListByNames(String[] tableNames);

	/**
	 * 查询所有表信息
	 *
	 * @return 表信息集合
	 */
	public Mono<List<GenTable>> selectGenTableAll();

	/**
	 * 导入表结构
	 *
	 * @param loginUser 登录用户
	 * @param tableList 导入表列表
	 * @return
	 */
	public Mono<Long> importGenTable(LoginUser loginUser, List<GenTable> tableList);

	/**
	 * 预览代码
	 *
	 * @param tableId 表编号
	 * @return 预览数据列表
	 */
	public Mono<Map<String, String>> previewCode(Long tableId);

	/**
	 * 生成代码（下载方式）
	 *
	 * @param tableName 表名称
	 * @return 数据
	 */
	public Mono<byte[]> downloadCode(String tableName);

	/**
	 * 生成代码（自定义路径）
	 *
	 * @param tableName 表名称
	 * @return 数据
	 */
	public Mono<String> generatorCode(String tableName);

	/**
	 * 同步数据库
	 *
	 * @param tableName 表名称
	 * @return
	 */
	public Mono<Long> synchDb(String tableName);

	/**
	 * 批量生成代码（下载方式）
	 *
	 * @param tableNames 表数组
	 * @return 数据
	 */
	public Mono<byte[]> downloadCode(String[] tableNames);

	/**
	 * 修改保存参数校验
	 *
	 * @param genTable 业务信息
	 */
	public void validateEdit(GenTable genTable);
}
