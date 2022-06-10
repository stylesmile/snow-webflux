package com.snow.web.controller.common;

import com.snow.common.config.RuoYiConfig;
import com.snow.common.constant.Constants;
import com.snow.common.core.controller.BaseController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.StringUtils;
import com.snow.common.utils.WebServerUtils;
import com.snow.common.utils.file.FileUploadUtils;
import com.snow.common.utils.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用请求处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/common")
public class CommonController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(CommonController.class);

	private static final String FILE_DELIMETER = ",";

	/**
	 * 通用下载请求
	 *
	 * @param fileName 文件名称
	 * @param delete   是否删除
	 */
	@GetMapping("/download")
	public Mono<ResponseEntity<InputStreamResource>> fileDownload(ServerWebExchange exchange, final String fileName, Boolean delete) {
		return startMono(() -> {
			if (!FileUtils.checkAllowDownload(fileName)) {
				return Mono.error(new ServiceException(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName)));
			}
			String downloadName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
			String filePath = RuoYiConfig.getDownloadPath() + fileName;
			return WebServerUtils.downloadFile(exchange.getRequest(),
				filePath,
				downloadName,
				delete);
		});
	}

	/**
	 * 通用上传请求（单个）
	 */
	@PostMapping("/upload")
	public Mono<AjaxResult> uploadFile(ServerWebExchange exchange, MultipartFile file) {
		return startMono(() -> {

			try {
				// 上传文件路径
				String filePath = RuoYiConfig.getUploadPath();
				// 上传并返回新文件名称
				String fileName = FileUploadUtils.upload(filePath, file);
				String url = WebServerUtils.getContextPath(exchange.getRequest()) + fileName;
				AjaxResult ajax = AjaxResult.success();
				ajax.put("url", url);
				ajax.put("fileName", fileName);
				ajax.put("newFileName", FileUtils.getName(fileName));
				ajax.put("originalFilename", file.getOriginalFilename());
				return Mono.just(ajax);
			} catch (Exception e) {
				return AjaxResult.errorMono(e.getMessage());
			}
		});
	}

	/**
	 * 通用上传请求（多个）
	 */
	@PostMapping("/uploads")
	public Mono<AjaxResult> uploadFiles(ServerWebExchange exchange, List<MultipartFile> files) {
		return startMono(() -> {
			try {
				// 上传文件路径
				String filePath = RuoYiConfig.getUploadPath();
				List<String> urls = new ArrayList<>();
				List<String> fileNames = new ArrayList<>();
				List<String> newFileNames = new ArrayList<>();
				List<String> originalFilenames = new ArrayList<>();
				for (MultipartFile file : files) {
					// 上传并返回新文件名称
					String fileName = FileUploadUtils.upload(filePath, file);
					String url = WebServerUtils.getContextPath(exchange.getRequest()) + fileName;
					urls.add(url);
					fileNames.add(fileName);
					newFileNames.add(FileUtils.getName(fileName));
					originalFilenames.add(file.getOriginalFilename());
				}
				AjaxResult ajax = AjaxResult.success();
				ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
				ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
				ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
				ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
				return Mono.just(ajax);
			} catch (Exception e) {
				return AjaxResult.errorMono(e.getMessage());
			}
		});
	}

	/**
	 * 本地资源通用下载
	 */
	@GetMapping("/download/resource")
	public Mono<ResponseEntity<InputStreamResource>> resourceDownload(ServerWebExchange exchange, final String resource) {
		return startMono(() -> {

			if (!FileUtils.checkAllowDownload(resource)) {
				return Mono.error(new ServiceException(StringUtils.format("资源文件({})非法，不允许下载。 ", resource)));
			}
			// 本地资源路径
			String localPath = RuoYiConfig.getProfile();
			// 数据库资源地址
			String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
			// 下载名称
			String downloadName = StringUtils.substringAfterLast(downloadPath, "/");

			return WebServerUtils.downloadFile(exchange.getRequest(),
				downloadPath,
				downloadName,
				false);
		});
	}
}
