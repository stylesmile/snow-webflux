package com.snow.generator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.snow.common.biz.BaseServiceImpl;
import com.snow.common.constant.Constants;
import com.snow.common.constant.GenConstants;
import com.snow.common.core.domain.model.LoginUser;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.core.text.CharsetKit;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.PageUtils;
import com.snow.common.utils.StringUtils;
import com.snow.generator.domain.GenTable;
import com.snow.generator.domain.GenTableColumn;
import com.snow.generator.mapper.GenTableColumnMapper;
import com.snow.generator.mapper.GenTableMapper;
import com.snow.generator.util.GenUtils;
import com.snow.generator.util.VelocityInitializer;
import com.snow.generator.util.VelocityUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 业务 服务层实现
 *
 * @author ruoyi
 */
@Service
public class GenTableServiceImpl
	extends BaseServiceImpl<GenTable, Long, GenTableMapper>
	implements IGenTableService {
	private static final Logger log = LoggerFactory.getLogger(GenTableServiceImpl.class);

	@Autowired
	private GenTableColumnMapper genTableColumnMapper;

	/**
	 * 查询业务信息
	 *
	 * @param id 业务ID
	 * @return 业务信息
	 */
	@Override
	public Mono<GenTable> selectById(Long id) {
		return mapper.selectById(id).map(genTable -> {
			setTableFromOptions(genTable);
			return genTable;
		});
	}

	/**
	 * 查询据库列表
	 *
	 * @param cond 业务信息
	 * @return 数据库表集合
	 */
	@Override
	public Mono<TableDataInfo> selectDbTableList(GenTable cond) {
		return mapper.selectDbTableList(cond).collectList().flatMap(list -> {
			if (cond.isPaged()) {
				return mapper.countDbTableList(cond).map(total -> {
					return PageUtils.getDataTable(list, total);
				});
			}
			return Mono.just(PageUtils.getDataTable(list, list.size()));
		});

	}

	/**
	 * 查询据库列表
	 *
	 * @param tableNames 表名称组
	 * @return 数据库表集合
	 */
	@Override
	public Mono<List<GenTable>> selectDbTableListByNames(String[] tableNames) {
		return mapper.selectDbTableListByNames(tableNames).collectList();
	}

	/**
	 * 查询所有表信息
	 *
	 * @return 表信息集合
	 */
	@Override
	public Mono<List<GenTable>> selectGenTableAll() {
		return mapper.selectGenTableAll().collectList();
	}

	/**
	 * 修改业务
	 *
	 * @param genTable 业务信息
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> update(GenTable genTable) {
		String options = JSON.toJSONString(genTable.getParams());
		genTable.setOptions(options);
		return mapper.update(genTable).flatMap(row -> {
			if (row > 0) {
				return Flux.fromIterable(genTable.getColumns()
				).flatMap(cenTableColumn -> {
					return genTableColumnMapper.update(cenTableColumn);
				}).count().thenReturn(row);
			}
			return Mono.just(row);
		});
	}

	/**
	 * 删除业务对象
	 *
	 * @param tableIds 需要删除的数据ID
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> deleteByIds(Long[] tableIds) {
		return mapper.deleteByIds(tableIds).flatMap(rows -> {
			return genTableColumnMapper.deleteByIds(tableIds).thenReturn(rows);
		});
	}

	/**
	 * 导入表结构
	 *
	 * @param tableList 导入表列表
	 * @return
	 */
	@Override
	@Transactional
	public Mono<Long> importGenTable(LoginUser loginUser,
									 List<GenTable> tableList) {
		String operName = loginUser.getUsername();

		return Flux.fromIterable(tableList).flatMap(table -> {
			String tableName = table.getTableName();
			GenUtils.initTable(table, operName);
			return mapper.insert(table).flatMap(row -> {
				if (row > 0) {
					// 保存列信息
					return genTableColumnMapper.selectDbTableColumnsByName(tableName).flatMap(column -> {
						GenUtils.initColumnField(column, table);
						return genTableColumnMapper.insert(column);
					}).count();
				}
				return Mono.just(0);
			});
		}).count(
		).doOnError(e -> {
			throw new ServiceException("导入失败：" + e.getMessage());
		});

	}

	/**
	 * 预览代码
	 *
	 * @param tableId 表编号
	 * @return 预览数据列表
	 */
	@Override
	public Mono<Map<String, String>> previewCode(Long tableId) {
		// 查询表信息
		return mapper.selectById(tableId).flatMap(table -> {
			// 设置主子表信息
			return setSubTable(table).thenReturn(table);
		}).map(table -> {
			// 设置主键列信息
			setPkColumn(table);
			VelocityInitializer.initVelocity();

			VelocityContext context = VelocityUtils.prepareContext(table);

			Map<String, String> dataMap = new LinkedHashMap<>();
			// 获取模板列表
			List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
			for (String template : templates) {
				// 渲染模板
				StringWriter sw = new StringWriter();
				Template tpl = Velocity.getTemplate(template, Constants.UTF8);
				tpl.merge(context, sw);
				dataMap.put(template, sw.toString());
			}
			return dataMap;
		});
	}

	/**
	 * 生成代码（下载方式）
	 *
	 * @param tableName 表名称
	 * @return 数据
	 */
	@Override
	public Mono<byte[]> downloadCode(final String tableName) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);

		return generatorCode(tableName, zip
		).doOnSuccess(o -> {
			IOUtils.closeQuietly(zip);
		}).map(o -> {
			return outputStream.toByteArray();
		});

	}

	/**
	 * 生成代码（自定义路径）
	 *
	 * @param tableName 表名称
	 * @return 表名称
	 */
	@Override
	public Mono<String> generatorCode(final String tableName) {
		// 查询表信息
		return mapper.selectGenTableByName(tableName
		).flatMap(table -> {
			// 设置主子表信息
			return setSubTable(table).thenReturn(table);
		}).doOnSuccess(table -> {
			// 设置主键列信息
			setPkColumn(table);

			VelocityInitializer.initVelocity();

			VelocityContext context = VelocityUtils.prepareContext(table);

			// 获取模板列表
			List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
			for (String template : templates) {
				if (!StringUtils.containsAny(template, "sql.vm", "api.js.vm", "index.vue.vm", "index-tree.vue.vm")) {
					// 渲染模板
					StringWriter sw = new StringWriter();
					Template tpl = Velocity.getTemplate(template, Constants.UTF8);
					tpl.merge(context, sw);
					try {
						String path = getGenPath(table, template);
						FileUtils.writeStringToFile(new File(path), sw.toString(), CharsetKit.UTF_8);
					} catch (IOException e) {
						throw new ServiceException("渲染模板失败，表名：" + table.getTableName());
					}
				}
			}
		}).thenReturn(tableName);
	}

	/**
	 * 同步数据库
	 *
	 * @param tableName 表名称
	 */
	@Override
	@Transactional
	public Mono<Long> synchDb(final String tableName) {
		return mapper.selectGenTableByName(tableName
		).flatMap(table -> {
			return genTableColumnMapper
				.selectDbTableColumnsByName(tableName)
				.collectList()
				.flatMap(dbTableColumns -> {

					if (StringUtils.isEmpty(dbTableColumns)) {
						throw new ServiceException("同步数据失败，原表结构不存在");
					}

					List<GenTableColumn> tableColumns = table.getColumns();
					Map<String, GenTableColumn> tableColumnMap = tableColumns.stream()
						.collect(Collectors.toMap(GenTableColumn::getColumnName, Function.identity()));
					List<String> dbTableColumnNames = dbTableColumns.stream()
						.map(GenTableColumn::getColumnName)
						.collect(Collectors.toList());

					return Flux.fromIterable(dbTableColumns).flatMap(column -> {
						GenUtils.initColumnField(column, table);
						if (tableColumnMap.containsKey(column.getColumnName())) {
							GenTableColumn prevColumn = tableColumnMap.get(column.getColumnName());
							column.setColumnId(prevColumn.getColumnId());
							if (column.isList()) {
								// 如果是列表，继续保留查询方式/字典类型选项
								column.setDictType(prevColumn.getDictType());
								column.setQueryType(prevColumn.getQueryType());
							}
							if (StringUtils.isNotEmpty(prevColumn.getIsRequired()) && !column.isPk()
								&& (column.isInsert() || column.isEdit())
								&& ((column.isUsableColumn()) || (!column.isSuperColumn()))) {
								// 如果是(新增/修改&非主键/非忽略及父属性)，继续保留必填/显示类型选项
								column.setIsRequired(prevColumn.getIsRequired());
								column.setHtmlType(prevColumn.getHtmlType());
							}
							return genTableColumnMapper.update(column);
						} else {
							return genTableColumnMapper.insert(column);
						}
					}).collectList().flatMap(o -> {
						List<GenTableColumn> delColumns = tableColumns.stream()
							.filter(column -> !dbTableColumnNames.contains(column.getColumnName()))
							.collect(Collectors.toList());
						if (StringUtils.isNotEmpty(delColumns)) {
							return genTableColumnMapper.deleteGenTableColumns(delColumns);
						}
						return Mono.just(0);
					});
				});
		}).thenReturn(1L);
	}

	/**
	 * 批量生成代码（下载方式）
	 *
	 * @param tableNames 表数组
	 * @return 数据
	 */
	@Override
	public Mono<byte[]> downloadCode(String[] tableNames) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);
		return Flux.fromArray(tableNames).flatMap(tableName -> {
			return generatorCode(tableName, zip);
		}).collectList().map(o -> {
			IOUtils.closeQuietly(zip);
			return outputStream.toByteArray();
		});
	}

	/**
	 * 查询表信息并生成代码
	 */
	private Mono<Long> generatorCode(String tableName, ZipOutputStream zip) {
		// 查询表信息
		return mapper.selectGenTableByName(tableName).flatMap(table -> {
			// 设置主子表信息
			return setSubTable(table).thenReturn(table);
		}).flatMap(table -> {
			// 设置主键列信息
			setPkColumn(table);

			VelocityInitializer.initVelocity();

			VelocityContext context = VelocityUtils.prepareContext(table);

			// 获取模板列表
			List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
			for (String template : templates) {
				// 渲染模板
				StringWriter sw = new StringWriter();
				Template tpl = Velocity.getTemplate(template, Constants.UTF8);
				tpl.merge(context, sw);
				try {
					// 添加到zip
					zip.putNextEntry(new ZipEntry(VelocityUtils.getFileName(template, table)));
					IOUtils.write(sw.toString(), zip, Constants.UTF8);
					IOUtils.closeQuietly(sw);
					zip.flush();
					zip.closeEntry();
				} catch (IOException e) {
					log.error("渲染模板失败，表名：" + table.getTableName(), e);
				}
			}
			return Mono.empty();
		});
	}

	/**
	 * 修改保存参数校验
	 *
	 * @param genTable 业务信息
	 */
	@Override
	public void validateEdit(GenTable genTable) {
		if (GenConstants.TPL_TREE.equals(genTable.getTplCategory())) {
			String options = JSON.toJSONString(genTable.getParams());
			JSONObject paramsObj = JSONObject.parseObject(options);
			if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_CODE))) {
				throw new ServiceException("树编码字段不能为空");
			} else if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_PARENT_CODE))) {
				throw new ServiceException("树父编码字段不能为空");
			} else if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_NAME))) {
				throw new ServiceException("树名称字段不能为空");
			} else if (GenConstants.TPL_SUB.equals(genTable.getTplCategory())) {
				if (StringUtils.isEmpty(genTable.getSubTableName())) {
					throw new ServiceException("关联子表的表名不能为空");
				} else if (StringUtils.isEmpty(genTable.getSubTableFkName())) {
					throw new ServiceException("子表关联的外键名不能为空");
				}
			}
		}
	}

	/**
	 * 设置主键列信息
	 *
	 * @param table 业务表信息
	 */
	public void setPkColumn(GenTable table) {
		for (GenTableColumn column : table.getColumns()) {
			if (column.isPk()) {
				table.setPkColumn(column);
				break;
			}
		}
		if (StringUtils.isNull(table.getPkColumn())) {
			table.setPkColumn(table.getColumns().get(0));
		}
		if (GenConstants.TPL_SUB.equals(table.getTplCategory())) {
			for (GenTableColumn column : table.getSubTable().getColumns()) {
				if (column.isPk()) {
					table.getSubTable().setPkColumn(column);
					break;
				}
			}
			if (StringUtils.isNull(table.getSubTable().getPkColumn())) {
				table.getSubTable().setPkColumn(table.getSubTable().getColumns().get(0));
			}
		}
	}

	/**
	 * 设置主子表信息
	 *
	 * @param table 业务表信息
	 */
	public Mono<Long> setSubTable(GenTable table) {
		String subTableName = table.getSubTableName();
		if (StringUtils.isNotEmpty(subTableName)) {
			return mapper.selectGenTableByName(subTableName).doOnSuccess(subTable -> {
				table.setSubTable(subTable);
			}).thenReturn(1L);
		}
		return Mono.just(1L);
	}

	/**
	 * 设置代码生成其他选项值
	 *
	 * @param genTable 设置后的生成对象
	 */
	public void setTableFromOptions(GenTable genTable) {
		JSONObject paramsObj = JSONObject.parseObject(genTable.getOptions());
		if (StringUtils.isNotNull(paramsObj)) {
			String treeCode = paramsObj.getString(GenConstants.TREE_CODE);
			String treeParentCode = paramsObj.getString(GenConstants.TREE_PARENT_CODE);
			String treeName = paramsObj.getString(GenConstants.TREE_NAME);
			String parentMenuId = paramsObj.getString(GenConstants.PARENT_MENU_ID);
			String parentMenuName = paramsObj.getString(GenConstants.PARENT_MENU_NAME);

			genTable.setTreeCode(treeCode);
			genTable.setTreeParentCode(treeParentCode);
			genTable.setTreeName(treeName);
			genTable.setParentMenuId(parentMenuId);
			genTable.setParentMenuName(parentMenuName);
		}
	}

	/**
	 * 获取代码生成地址
	 *
	 * @param table    业务表信息
	 * @param template 模板文件路径
	 * @return 生成地址
	 */
	public static String getGenPath(GenTable table, String template) {
		String genPath = table.getGenPath();
		if (StringUtils.equals(genPath, "/")) {
			return System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, table);
		}
		return genPath + File.separator + VelocityUtils.getFileName(template, table);
	}
}