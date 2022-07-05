package com.snow.web.controller.system;

import com.snow.common.biz.EntityController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.snow.common.annotation.Log;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.enums.BusinessType;
import com.snow.web.domain.SysNotice;
import com.snow.web.service.ISysNoticeService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 公告 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends EntityController<SysNotice, Long, ISysNoticeService> {
	/**
	 * 获取通知公告列表
	 */
	@PreAuthorize("@ss.hasPermi('system:notice:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(ServerWebExchange exchange, SysNotice notice) {
        return super.list(exchange, notice);
	}

	/**
	 * 根据通知公告编号获取详细信息
	 */
	@PreAuthorize("@ss.hasPermi('system:notice:query')")
	@GetMapping(value = "/{noticeId}")
	public Mono<AjaxResult> getInfo(@PathVariable long noticeId) {
		return super.getInfo(noticeId);
	}

	/**
	 * 新增通知公告
	 */
	@PreAuthorize("@ss.hasPermi('system:notice:add')")
	@Log(title = "通知公告", businessType = BusinessType.INSERT)
	@PostMapping
	public Mono<AjaxResult> add(@Validated @RequestBody SysNotice notice) {
		return add(notice);
	}

	/**
	 * 修改通知公告
	 */
	@PreAuthorize("@ss.hasPermi('system:notice:edit')")
	@Log(title = "通知公告", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> edit(@Validated @RequestBody SysNotice notice) {
		return startLoginUserMono(loginUser -> {
			notice.setUpdateBy(loginUser.getUsername());
			return service.update(notice).map(this::toAjax);
		});
	}

	/**
	 * 删除通知公告
	 */
	@PreAuthorize("@ss.hasPermi('system:notice:remove')")
	@Log(title = "通知公告", businessType = BusinessType.DELETE)
	@DeleteMapping("/{noticeIds}")
	public Mono<AjaxResult> remove(@PathVariable Long[] noticeIds) {
		return super.remove(noticeIds);
	}
}
