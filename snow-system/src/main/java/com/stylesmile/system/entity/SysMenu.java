package com.stylesmile.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenye
 * @date 2018/12/10
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysMenu {
    /**
     * 主键
     */
    private Long id;
    /**
     * 父级id
     */
    private String parentId;
    /**
     * 名称
     */
    private String name;
    /**
     * 编号
     */
    private String code;
    /**
     * 资源定位地址
     */
    private String url;
    /**
     * 类型
     */
    private String type;
    /**
     * 排序
     */
    private String sort;
}