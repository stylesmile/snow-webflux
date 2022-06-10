package com.snow.common.core.page;

import com.snow.common.core.text.Convert;
import com.snow.common.utils.WebServerUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * 表格数据处理
 * 
 * @author ruoyi
 */
public class TableSupport
{
    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 分页参数合理化
     */
    public static final String REASONABLE = "reasonable";

    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain(ServerHttpRequest request)
    {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(Convert.toInt(WebServerUtils.getParameter(request, PAGE_NUM), 1));
        pageDomain.setPageSize(Convert.toInt(WebServerUtils.getParameter(request, PAGE_SIZE), 10));
        pageDomain.setOrderByColumn(WebServerUtils.getParameter(request, ORDER_BY_COLUMN));
        pageDomain.setIsAsc(WebServerUtils.getParameter(request, IS_ASC));
        pageDomain.setReasonable(WebServerUtils.getParameterToBool(request, REASONABLE));
        return pageDomain;
    }

    public static PageDomain buildPageRequest(ServerHttpRequest request)
    {
        return getPageDomain(request);
    }
}
