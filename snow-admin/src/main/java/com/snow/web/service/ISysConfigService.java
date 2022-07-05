package com.snow.web.service;

import com.snow.common.biz.IService;
import com.snow.web.domain.SysConfig;
import reactor.core.publisher.Mono;

/**
 * 参数配置 服务层
 *
 * @author ruoyi
 */
public interface ISysConfigService extends IService<SysConfig, Integer> {

	/**
	 * 根据键名查询参数配置信息
	 *
	 * @param configKey 参数键名
	 * @return 参数键值
	 */
	public Mono<String> selectConfigByKey(String configKey);

	/**
	 * 获取验证码开关
	 *
	 * @return true开启，false关闭
	 */
	public Mono<Boolean> selectCaptchaOnOff();

	/**
	 * 加载参数缓存数据
	 *
	 * @return
	 */
	public Mono<Long> loadingConfigCache();

	/**
	 * 清空参数缓存数据
	 */
	public void clearConfigCache();

	/**
	 * 重置参数缓存数据
	 *
	 * @return
	 */
	public Mono<Long> resetConfigCache();

	/**
	 * 校验参数键名是否唯一
	 *
	 * @param config 参数信息
	 * @return 结果
	 */
	public Mono<String> checkConfigKeyUnique(SysConfig config);
}
