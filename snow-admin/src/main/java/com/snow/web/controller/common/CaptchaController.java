package com.snow.web.controller.common;

import com.google.code.kaptcha.Producer;
import com.snow.common.config.RuoYiConfig;
import com.snow.common.constant.Constants;
import com.snow.common.core.controller.BaseController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.redis.RedisCache;
import com.snow.common.utils.sign.Base64;
import com.snow.common.utils.uuid.IdUtils;
import com.snow.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 *
 * @author ruoyi
 */
@RestController
public class CaptchaController extends BaseController {
	@Resource(name = "captchaProducer")
	private Producer captchaProducer;

	@Resource(name = "captchaProducerMath")
	private Producer captchaProducerMath;

	@Autowired
	private RedisCache redisCache;

	@Autowired
	private ISysConfigService configService;

	/**
	 * 生成验证码
	 */
	@GetMapping("/captchaImage")
	public Mono<AjaxResult> getCode(ServerHttpResponse response) {
		return startMono(() -> {

			return configService.selectCaptchaOnOff().map(captchaOnOff -> {
				AjaxResult ajax = AjaxResult.success();
				ajax.put("captchaOnOff", captchaOnOff);
				if (!captchaOnOff) {
					return ajax;
				}

				// 保存验证码信息
				String uuid = IdUtils.simpleUUID();
				String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

				String capStr = null, code = null;
				BufferedImage image = null;

				// 生成验证码
				String captchaType = RuoYiConfig.getCaptchaType();
				if ("math".equals(captchaType)) {
					String capText = captchaProducerMath.createText();
					capStr = capText.substring(0, capText.lastIndexOf("@"));
					code = capText.substring(capText.lastIndexOf("@") + 1);
					image = captchaProducerMath.createImage(capStr);
				} else if ("char".equals(captchaType)) {
					capStr = code = captchaProducer.createText();
					image = captchaProducer.createImage(capStr);
				}

				redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
				// 转换流信息写出
				FastByteArrayOutputStream os = new FastByteArrayOutputStream();
				try {
					ImageIO.write(image, "jpg", os);
				} catch (IOException e) {
					return AjaxResult.error(e.getMessage());
				}

				ajax.put("uuid", uuid);
				ajax.put("img", Base64.encode(os.toByteArray()));
				return ajax;
			});
		});
	}
}
