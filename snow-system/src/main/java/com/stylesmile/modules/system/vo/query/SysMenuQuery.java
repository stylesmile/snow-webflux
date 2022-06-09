package com.stylesmile.modules.system.vo.query;

import com.stylesmile.modules.system.entity.SysUser;
import lombok.Data;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * @author chenye
 * @date 2018/12/10
 */
@Data
public class SysMenuQuery extends PageImpl<SysUser> {

    private int id;
    private String name;
    private String code;
    private String sort;

    public SysMenuQuery(List<SysUser> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Sort getSort() {
        return null;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

}
