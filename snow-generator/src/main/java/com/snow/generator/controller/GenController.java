package com.snow.generator.controller;

import com.snow.common.annotation.Log;
import com.snow.common.core.controller.BaseController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.core.text.Convert;
import com.snow.common.enums.BusinessType;
import com.snow.common.utils.WebServerUtils;
import com.snow.generator.domain.GenTable;
import com.snow.generator.service.IGenTableColumnService;
import com.snow.generator.service.IGenTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成 操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/tool/gen")
public class GenController extends BaseController {
	@Autowired
	private IGenTableService service;

	@Autowired
	private IGenTableColumnService genTableColumnService;

	/**
	 * 查询代码生成列表
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> genList(ServerWebExchange exchange, GenTable cond) {
		return startMono(() -> {
			startPage(exchange.getRequest(), cond);
			return service.selectList(cond).flatMap(list -> {
				if (cond.isPaged()) {
					return service.countList(cond).map(total -> {
						return getDataTable(list, total);
					});
				}
				return Mono.just(getDataTable(list, list.size()));
			});
		});
	}

	/**
	 * 修改代码生成业务
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:query')")
	@GetMapping(value = "/{tableId}")
	public Mono<AjaxResult> getInfo(@PathVariable Long tableId) {
		return startMono(() -> {
			return service.selectById(tableId).flatMap(table -> {
				return service.selectGenTableAll().flatMap(tables -> {
					return genTableColumnService.selectGenTableColumnListByTableId(tableId).map(list -> {
						Map<String, Object> map = new HashMap<>();
						map.put("info", table);
						map.put("rows", list);
						map.put("tables", tables);
						return AjaxResult.success(map);
					});
				});
			});
		});
	}

	/**
	 * 查询数据库列表
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:list')")
	@GetMapping("/db/list")
	public Mono<TableDataInfo> dataList(ServerWebExchange exchange, GenTable cond) {
		return startMono(() -> {
			startPage(exchange.getRequest(), cond);
			return service.selectDbTableList(cond);
		});
	}

	/**
	 * 查询数据表字段列表
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:list')")
	@GetMapping(value = "/column/{tableId}")
	public Mono<TableDataInfo> columnList(Long tableId) {
		return startMono(() -> {
			return genTableColumnService.selectGenTableColumnListByTableId(tableId).map(list -> {
				TableDataInfo dataInfo = new TableDataInfo();
				dataInfo.setRows(list);
				dataInfo.setTotal(list.size());
				return dataInfo;
			});
		});
	}

	/**
	 * 导入表结构（保存）
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:import')")
	@Log(title = "代码生成", businessType = BusinessType.IMPORT)
	@PostMapping("/importTable")
	public Mono<AjaxResult> importTableSave(String tables) {
		return startLoginUserMono(loginUser -> {
			String[] tableNames = Convert.toStrArray(tables);
			// 查询表信息
			return service.selectDbTableListByNames(tableNames)
				.flatMap(tableList -> {
					return service.importGenTable(loginUser, tableList).thenReturn(AjaxResult.success());
				});
		});
	}

	/**
	 * 修改保存代码生成业务
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:edit')")
	@Log(title = "代码生成", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> editSave(@Validated @RequestBody GenTable genTable) {
		return startMono(() -> {
			service.validateEdit(genTable);
			return service.update(genTable).map(AjaxResult::success);
		});
	}

	/**
	 * 删除代码生成
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:remove')")
	@Log(title = "代码生成", businessType = BusinessType.DELETE)
	@DeleteMapping("/{tableIds}")
	public Mono<AjaxResult> remove(@PathVariable Long[] tableIds) {
		return startMono(() -> {
			return service.deleteByIds(tableIds).map(AjaxResult::success);
		});
	}

	/**
	 * 预览代码
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:preview')")
	@GetMapping("/preview/{tableId}")
	public Mono<AjaxResult> preview(@PathVariable("tableId") Long tableId) {
		return startMono(() -> {
			return service.previewCode(tableId).map(AjaxResult::success);
		});
	}

	/**
	 * 生成代码（下载方式）
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:code')")
	@Log(title = "代码生成", businessType = BusinessType.GENCODE)
	@GetMapping("/download/{tableName}")
	public Mono<ResponseEntity<InputStreamResource>> download(
		ServerWebExchange exchange,
		@PathVariable("tableName") String tableName) {
		return startMono(() -> {
			return service.downloadCode(tableName).flatMap(data -> {
				return downloadFile(exchange, data);
			});
		});
	}

	/**
	 * 生成代码（自定义路径）
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:code')")
	@Log(title = "代码生成", businessType = BusinessType.GENCODE)
	@GetMapping("/genCode/{tableName}")
	public Mono<AjaxResult> genCode(@PathVariable("tableName") String tableName) {
		return startMono(() -> {
			return service.generatorCode(tableName).thenReturn(AjaxResult.success());
		});
	}

	/**
	 * 同步数据库
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:edit')")
	@Log(title = "代码生成", businessType = BusinessType.UPDATE)
	@GetMapping("/synchDb/{tableName}")
	public Mono<AjaxResult> synchDb(@PathVariable("tableName") String tableName) {
		return startMono(() -> {
			return service.synchDb(tableName).thenReturn(AjaxResult.success());
		});
	}

	/**
	 * 批量生成代码
	 */
	@PreAuthorize("@ss.hasPermi('tool:gen:code')")
	@Log(title = "代码生成", businessType = BusinessType.GENCODE)
	@GetMapping("/batchGenCode")
	public Mono<ResponseEntity<InputStreamResource>> batchGenCode(ServerWebExchange exchange, String tables) {
		return startMono(() -> {
			String[] tableNames = Convert.toStrArray(tables);
			return service.downloadCode(tableNames).flatMap(data -> {
				return downloadFile(exchange, data);
			});
		});
	}

	/**
	 * 生成zip文件
	 */
	private Mono<ResponseEntity<InputStreamResource>> downloadFile(
		ServerWebExchange exchange,
		byte[] data) {
		return WebServerUtils.downloadFile(exchange.getRequest(),
			data,
			"ruoyi.zip");
	}
}