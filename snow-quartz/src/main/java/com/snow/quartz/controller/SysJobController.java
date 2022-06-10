package com.snow.quartz.controller;

import com.snow.common.annotation.Log;
import com.snow.common.biz.EntityController;
import com.snow.common.constant.Constants;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.enums.BusinessType;
import com.snow.common.utils.StringUtils;
import com.snow.quartz.domain.SysJob;
import com.snow.quartz.service.ISysJobService;
import com.snow.quartz.util.CronUtils;
import com.snow.quartz.util.ScheduleUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 调度任务信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends EntityController<SysJob, Long, ISysJobService> {
	/**
	 * 查询定时任务列表
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(ServerWebExchange exchange, SysJob where) {
		return super.list(exchange, where);
	}

	/**
	 * 导出定时任务列表
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:export')")
	@Log(title = "定时任务", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
	public Mono<Void> export(ServerHttpResponse response, SysJob where) {
		return super.export(response, where, SysJob.class, "定时任务");
	}

	/**
	 * 获取定时任务详细信息
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:query')")
	@GetMapping(value = "/{id}")
	public Mono<AjaxResult> getInfo(@PathVariable("id") long id) {
		return super.getInfo(id);
	}

	/**
	 * 新增定时任务
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:add')")
	@Log(title = "定时任务", businessType = BusinessType.INSERT)
	@PostMapping
	public Mono<AjaxResult> add(@RequestBody SysJob job) {
		return startLoginUserMono(loginUser -> {

			if (!CronUtils.isValid(job.getCronExpression())) {
				return errorMono("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
			} else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
				return errorMono("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
			} else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
				return errorMono("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
			} else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.HTTP, Constants.HTTPS})) {
				return errorMono("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
			} else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
				return errorMono("新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
			} else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) {
				return errorMono("新增任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
			}
			job.setCreateBy(loginUser.getUsername());
			return service.insert(job).map(this::toAjax);
		});
	}

	/**
	 * 修改定时任务
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:edit')")
	@Log(title = "定时任务", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> edit(@RequestBody SysJob job) {
		return startLoginUserMono(loginUser -> {

			if (!CronUtils.isValid(job.getCronExpression())) {
				return errorMono("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
			} else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
				return errorMono("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
			} else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
				return errorMono("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
			} else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.HTTP, Constants.HTTPS})) {
				return errorMono("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
			} else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
				return errorMono("修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
			} else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) {
				return errorMono("修改任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
			}
			job.setUpdateBy(loginUser.getUsername());
			return service.update(job).map(this::toAjax);
		});
	}

	/**
	 * 定时任务状态修改
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
	@Log(title = "定时任务", businessType = BusinessType.UPDATE)
	@PutMapping("/changeStatus")
	public Mono<AjaxResult> changeStatus(@RequestBody SysJob job) {
		return startMono(() -> {
			return service.selectById(job.getJobId()).flatMap(newJob -> {
				newJob.setStatus(job.getStatus());
				return service.changeStatus(newJob).map(this::toAjax);
			});
		});
	}

	/**
	 * 定时任务立即执行一次
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
	@Log(title = "定时任务", businessType = BusinessType.UPDATE)
	@PutMapping("/run")
	public Mono<AjaxResult> run(@RequestBody SysJob job) {
		return startMono(() -> {
			return service.run(job).thenReturn(AjaxResult.success());
		});
	}

	/**
	 * 删除定时任务
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:remove')")
	@Log(title = "定时任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{jobIds}")
	public Mono<AjaxResult> remove(@PathVariable Long[] ids) {
		return super.remove(ids);
	}
}
