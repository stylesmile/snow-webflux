package com.snow.system.service.impl;

import com.snow.common.biz.BaseServiceImpl;
import com.snow.common.constant.Constants;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.redis.RedisCache;
import com.snow.common.core.text.Convert;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.StringUtils;
import com.snow.system.domain.SysConfig;
import com.snow.system.mapper.SysConfigMapper;
import com.snow.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * 参数配置 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysConfigServiceImpl
	extends BaseServiceImpl<SysConfig, Integer, SysConfigMapper>
	implements ISysConfigService {

	@Autowired
	private RedisCache redisCache;

	/**
	 * 项目启动时，初始化参数到缓存
	 */
	@PostConstruct
	public void init() {
		loadingConfigCache().subscribe();
	}

	/**
	 * 查询参数配置信息
	 *
	 * @param configId 参数配置ID
	 * @return 参数配置信息
	 */
	@Override
//	@DataSource(DataSourceType.MASTER)
	public Mono<SysConfig> selectById(Integer configId) {
		SysConfig config = new SysConfig();
		config.setConfigId(configId);
		return mapper.selectConfig(config);
	}

	/**
	 * 根据键名查询参数配置信息
	 *
	 * @param configKey 参数key
	 * @return 参数键值
	 */
	@Override
	public Mono<String> selectConfigByKey(final String configKey) {
		String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
		if (StringUtils.isNotEmpty(configValue)) {
			return Mono.just(configValue);
		}
		SysConfig config = new SysConfig();
		config.setConfigKey(configKey);
		return mapper.selectConfig(config).map(retConfig -> {
			if (StringUtils.isNotNull(retConfig)) {
				redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
				return retConfig.getConfigValue();
			}
			return StringUtils.EMPTY;
		});
	}

	/**
	 * 获取验证码开关
	 *
	 * @return true开启，false关闭
	 */
	@Override
	public Mono<Boolean> selectCaptchaOnOff() {
		return selectConfigByKey("sys.account.captchaOnOff").map(captchaOnOff -> {
			if (StringUtils.isEmpty(captchaOnOff)) {
				return true;
			}
			return Convert.toBool(captchaOnOff);
		});
	}

	/**
	 * 新增参数配置
	 *
	 * @param config 参数配置信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> insert(SysConfig config) {
		return mapper.insert(config).doOnSuccess(row -> {
			if (row > 0) {
				redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
			}
		});
	}

	/**
	 * 修改参数配置
	 *
	 * @param config 参数配置信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> update(SysConfig config) {
		return mapper.update(config).doOnSuccess(row -> {
			if (row > 0) {
				redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
			}
		});
	}

	/**
	 * 批量删除参数信息
	 *
	 * @param configIds 需要删除的参数ID
	 * @return
	 */
	@Override
	public Mono<Long> deleteByIds(Integer[] configIds) {
		return Flux.fromArray(configIds).flatMap(configId -> {
			return selectById(configId).flatMap(config -> {
				if (StringUtils.equals(UserConstants.YES, config.getConfigType())) {
					throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
				}
				return mapper.deleteById(configId).doOnSuccess(o -> {
					redisCache.deleteObject(getCacheKey(config.getConfigKey()));
				});
			});
		}).count();
	}

	/**
	 * 加载参数缓存数据
	 *
	 * @return
	 */
	@Override
	public Mono<Long> loadingConfigCache() {
		// TODO: flux redis
		return mapper.selectList(new SysConfig()).map(config -> {
			redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
			return 1;
		}).count();
	}

	/**
	 * 清空参数缓存数据
	 */
	@Override
	public void clearConfigCache() {
		Collection<String> keys = redisCache.keys(Constants.SYS_CONFIG_KEY + "*");
		redisCache.deleteObject(keys);
	}

	/**
	 * 重置参数缓存数据
	 *
	 * @return
	 */
	@Override
	public Mono<Long> resetConfigCache() {
		clearConfigCache();
		return loadingConfigCache();
	}

	/**
	 * 校验参数键名是否唯一
	 *
	 * @param config 参数配置信息
	 * @return 结果
	 */
	@Override
	public Mono<String> checkConfigKeyUnique(SysConfig config) {
		Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
		return mapper.checkConfigKeyUnique(config.getConfigKey()).map(info -> {
			if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue()) {
				return UserConstants.NOT_UNIQUE;
			}
			return UserConstants.UNIQUE;
		});

	}

	/**
	 * 设置cache key
	 *
	 * @param configKey 参数键
	 * @return 缓存键key
	 */
	private String getCacheKey(String configKey) {
		return Constants.SYS_CONFIG_KEY + configKey;
	}
}
