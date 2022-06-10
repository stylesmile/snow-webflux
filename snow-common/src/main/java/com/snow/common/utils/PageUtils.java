package com.snow.common.utils;

import com.snow.common.constant.HttpStatusCode;
import com.snow.common.core.domain.PagingParam;
import com.snow.common.core.page.PageDomain;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.core.page.TableSupport;
import com.snow.common.utils.sql.SqlUtil;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;

/**
 * 分页工具类
 *
 * @author ruoyi
 */
public class PageUtils {
	/**
	 * 设置请求分页数据
	 */
	public static void startPage(ServerHttpRequest request, PagingParam paging) {
		PageDomain pageDomain = TableSupport.buildPageRequest(request);
		Integer pageNum = pageDomain.getPageNum();
		Integer pageSize = pageDomain.getPageSize();
		String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
		paging.setPageNum(pageNum);
		paging.setPageSize(pageSize);
		paging.setOrderByStr(orderBy);
	}

	public static TableDataInfo getDataTable(List<?> list, final long total) {
		TableDataInfo tableData = new TableDataInfo();
		tableData.setCode(HttpStatusCode.SUCCESS);
		tableData.setMsg("查询成功");
		tableData.setRows(list);
		tableData.setTotal(total);
		return tableData;
	}
}
