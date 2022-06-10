package com.snow.common.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PagingParam {
	/**
	 * 页码
	 */
	@JsonIgnore
	private Integer pageNum;

	/**
	 * 每页显示数量
	 */
	@JsonIgnore
	private Integer pageSize;

	/**
	 * 分页排序
	 */
	@JsonIgnore
	private String orderByStr;

	/**
	 * 分页语句
	 */
	@JsonIgnore
	private String pagingSql;

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
		genPagingSql();
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		genPagingSql();
	}

	public String getOrderByStr() {
		return orderByStr;
	}

	public void setOrderByStr(String orderByStr) {
		this.orderByStr = orderByStr;
	}

	public boolean isPaged() {
		Integer pageNum = this.getPageNum();
		Integer pageSize = this.getPageSize();
		if (pageNum != null
			&& pageSize != null
			&& pageNum > 0
			&& pageSize > 0) {
			return true;
		}
		return false;
	}

	public void genPagingSql() {
		Integer pageNum = this.getPageNum();
		Integer pageSize = this.getPageSize();
		if (pageNum != null
			&& pageSize != null
			&& pageNum > 0
			&& pageSize > 0) {
			pagingSql = String.format("LIMIT %d, %d", (pageNum - 1) * pageSize, pageSize);
			return;
		}
		pagingSql = null;
	}
}

