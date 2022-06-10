package com.snow.quartz.service.impl;

import com.snow.common.biz.BaseServiceImpl;
import com.snow.common.constant.HttpStatusCode;
import com.snow.common.constant.ScheduleConstants;
import com.snow.common.exception.ServiceException;
import com.snow.common.exception.job.TaskException;
import com.snow.quartz.domain.SysJob;
import com.snow.quartz.mapper.SysJobMapper;
import com.snow.quartz.service.ISysJobService;
import com.snow.quartz.util.CronUtils;
import com.snow.quartz.util.ScheduleUtils;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

/**
 * 定时任务调度信息 服务层
 *
 * @author ruoyi
 */
@Service
public class SysJobServiceImpl
	extends BaseServiceImpl<SysJob, Long, SysJobMapper>
	implements ISysJobService {

	@Autowired
	private Scheduler scheduler;

	/**
	 * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
	 */
	@PostConstruct
	public void init() throws SchedulerException, TaskException {
		scheduler.clear();
		mapper.selectJobAll().map(job -> {
			try {
				ScheduleUtils.createScheduleJob(scheduler, job);
			} catch (Exception e) {
				throw new ServiceException(e.getMessage(), HttpStatusCode.ERROR);
			}
			return Mono.just(0);
		}).subscribe();
	}

	/**
	 * 暂停任务
	 *
	 * @param job 调度信息
	 * @return 数量
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Mono<Long> pauseJob(SysJob job) {
		Long jobId = job.getJobId();
		String jobGroup = job.getJobGroup();
		job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
		return mapper.update(job).flatMap(rows -> {
			if (rows > 0) {
				try {
					scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
				} catch (SchedulerException e) {
					throw new ServiceException(e.getMessage(), HttpStatusCode.ERROR);
				}
			}
			return Mono.just(rows);
		});
	}

	/**
	 * 恢复任务
	 *
	 * @param job 调度信息
	 * @return 数量
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Mono<Long> resumeJob(SysJob job) {
		Long jobId = job.getJobId();
		String jobGroup = job.getJobGroup();
		job.setStatus(ScheduleConstants.Status.NORMAL.getValue());

		return mapper.update(job).flatMap(rows -> {
			if (rows > 0) {
				try {
					scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
				} catch (SchedulerException e) {
					throw new ServiceException(e.getMessage(), HttpStatusCode.ERROR);
				}
			}
			return Mono.just(rows);
		});
	}

	/**
	 * 删除任务后，所对应的trigger也将被删除
	 *
	 * @param job 调度信息
	 * @return 数量
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Mono<Long> deleteJob(SysJob job) {
		Long jobId = job.getJobId();
		String jobGroup = job.getJobGroup();

		return mapper.deleteById(jobId).flatMap(rows -> {
			if (rows > 0) {
				try {
					scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, jobGroup));
				} catch (SchedulerException e) {
					throw new ServiceException(e.getMessage(), HttpStatusCode.ERROR);
				}
			}
			return Mono.just(rows);
		});
	}

	/**
	 * 批量删除调度信息
	 *
	 * @param jobIds 需要删除的任务ID
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Mono<Long> deleteByIds(Long[] jobIds) {
		return Flux.fromArray(jobIds).flatMap(jobId -> {
			return mapper.selectById(jobId);
		}).flatMap(this::deleteJob).count();
	}

	/**
	 * 任务调度状态修改
	 *
	 * @param job 调度信息
	 * @return 数量
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Mono<Long> changeStatus(SysJob job) {
		String status = job.getStatus();
		if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
			return resumeJob(job);
		} else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
			return pauseJob(job);
		}
		return Mono.just(0L);
	}

	/**
	 * 立即运行任务
	 *
	 * @param job 调度信息
	 * @return 数量
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Mono<Long> run(SysJob job) {
		Long jobId = job.getJobId();
		String jobGroup = job.getJobGroup();
		return selectById(job.getJobId()).flatMap(properties -> {
			// 参数
			JobDataMap dataMap = new JobDataMap();
			dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
			try {
				scheduler.triggerJob(ScheduleUtils.getJobKey(jobId, jobGroup), dataMap);
			} catch (SchedulerException e) {
				throw new ServiceException(e.getMessage(), HttpStatusCode.ERROR);
			}
			return Mono.just(1L);
		});
	}

	/**
	 * 新增任务
	 *
	 * @param job 调度信息 调度信息
	 * @return 新增数量
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Mono<Long> insert(SysJob job) {
		job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
		return mapper.insert(job).flatMap(rows -> {
			if (rows > 0) {

				try {
					ScheduleUtils.createScheduleJob(scheduler, job);
				} catch (SchedulerException | TaskException e) {
					throw new ServiceException(e.getMessage(), HttpStatusCode.ERROR);
				}

			}
			return Mono.just(rows);
		});
	}

	/**
	 * 更新任务的时间表达式
	 *
	 * @param job 调度信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Mono<Long> update(SysJob job) {
		return selectById(job.getJobId()).flatMap(properties -> {
			return mapper.update(job).flatMap(rows -> {
				if (rows > 0) {
					try {
						updateSchedulerJob(job, properties.getJobGroup());
					} catch (SchedulerException | TaskException e) {
						throw new ServiceException(e.getMessage(), HttpStatusCode.ERROR);
					}
				}
				return Mono.just(rows);
			});
		});
	}

	/**
	 * 更新任务
	 *
	 * @param job      任务对象
	 * @param jobGroup 任务组名
	 */
	public void updateSchedulerJob(SysJob job, String jobGroup) throws SchedulerException, TaskException {
		Long jobId = job.getJobId();
		// 判断是否存在
		JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
		if (scheduler.checkExists(jobKey)) {
			// 防止创建时存在数据问题 先移除，然后在执行创建操作
			scheduler.deleteJob(jobKey);
		}
		ScheduleUtils.createScheduleJob(scheduler, job);
	}

	/**
	 * 校验cron表达式是否有效
	 *
	 * @param cronExpression 表达式
	 * @return 结果
	 */
	@Override
	public boolean checkCronExpressionIsValid(String cronExpression) {
		return CronUtils.isValid(cronExpression);
	}
}
