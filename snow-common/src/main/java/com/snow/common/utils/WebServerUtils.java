package com.snow.common.utils;

import com.snow.common.constant.HttpStatusCode;
import com.snow.common.core.text.Convert;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.file.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * 客户端工具类
 *
 * @author ruoyi
 */
public class WebServerUtils {
	/**
	 * 获取String参数
	 */
	public static String getParameter(ServerHttpRequest request, final String name) {
		return request.getQueryParams().getFirst(name);
	}

	/**
	 * 获取String参数
	 */
	public static String getParameter(ServerHttpRequest request, final String name, final String defaultValue) {
		return Convert.toStr(getParameter(request, name), defaultValue);
	}

	/**
	 * 获取Integer参数
	 */
	public static Integer getParameterToInt(ServerHttpRequest request, final String name) {
		return Convert.toInt(getParameter(request, name));
	}

	/**
	 * 获取Integer参数
	 */
	public static Integer getParameterToInt(ServerHttpRequest request, final String name, Integer defaultValue) {
		return Convert.toInt(getParameter(request, name), defaultValue);
	}

	/**
	 * 获取Boolean参数
	 */
	public static Boolean getParameterToBool(ServerHttpRequest request, final String name) {
		return Convert.toBool(getParameter(request, name));
	}

	/**
	 * 获取Boolean参数
	 */
	public static Boolean getParameterToBool(ServerHttpRequest request, final String name, Boolean defaultValue) {
		return Convert.toBool(getParameter(request, name), defaultValue);
	}

	public static String getHeader(ServerHttpRequest request, final String headerName) {
		return request.getHeaders().getFirst(headerName);
	}

	public static void addHeader(ServerHttpResponse response, final String headerName, final String headerValue) {
		response.getHeaders().add(headerName, headerValue);
	}

	public static void setHeader(ServerHttpResponse response, final String headerName, final String headerValue) {
		response.getHeaders().set(headerName, headerValue);
	}

	public static String getRemoteAddress(ServerHttpRequest request) {
		return request.getRemoteAddress().getAddress().toString();
	}

	/**
	 * 将字符串渲染到客户端
	 *
	 * @param response 渲染对象
	 * @param string   待渲染的字符串
	 */
	public static Mono<Void> renderString(ServerHttpResponse response, final String string) {
		response.setStatusCode(HttpStatus.OK);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		if (string == null) {
			return Mono.empty();
		}

		byte[] body = string.getBytes(StandardCharsets.UTF_8);
		DataBuffer buffer = new DefaultDataBufferFactory().wrap(body);
		return response.writeWith(Mono.just(buffer));
	}

	/**
	 * 是否是Ajax异步请求
	 *
	 * @param request 请求对象
	 */
	public static boolean isAjaxRequest(ServerHttpRequest request) {
		String accept = getHeader(request, "accept");
		if (accept != null && accept.contains("application/json")) {
			return true;
		}

		String xRequestedWith = getHeader(request, "X-Requested-With");
		if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest")) {
			return true;
		}

		String uri = request.getURI().toString();
		if (StringUtils.inStringIgnoreCase(uri, ".json", ".xml")) {
			return true;
		}

		String ajax = getParameter(request, "__ajax");
		return StringUtils.inStringIgnoreCase(ajax, "json", "xml");
	}

	/**
	 * 获取完整的请求路径
	 *
	 * @return 服务地址
	 */
	public static String getURI(ServerHttpRequest request) {
		return request.getURI().toString();
	}

	/**
	 * 获取完整的请求路径，包括：域名，端口，上下文访问路径
	 *
	 * @return 服务地址
	 */
	public static String getContextPath(ServerHttpRequest request) {
		return request.getPath().contextPath().value();
	}

	/**
	 * 下载文件
	 */
	public static Mono<ResponseEntity<InputStreamResource>> downloadFile(
		ServerHttpRequest request,
		final String filePath,
		final String downloadName,
		Boolean delete) {
		String headerFilename = downloadName;
		try {
			headerFilename = FileUtils.setFileDownloadHeader(request, downloadName);
		} catch (UnsupportedEncodingException e) {
		}
		String headerFilename2 = headerFilename;

		try {
			File file = new File(filePath);
			InputStream s = Files.newInputStream(file.toPath());
			return Mono.just(s).map(it -> {
				return ResponseEntity.ok()
					.header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + headerFilename2 + "\"")
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(it));
			}).doOnError(t -> {
			}).doOnSuccess((o) -> {
				if (delete) {
					// TODO delete file after download
//					FileUtils.deleteFile(filePath);
				}
			});
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), HttpStatusCode.ERROR);
		}
	}

	public static Mono<ResponseEntity<InputStreamResource>> downloadFile(
		ServerHttpRequest request,
		byte[] data,
		final String downloadName) {

		String headerFilename = downloadName;
		try {
			headerFilename = FileUtils.setFileDownloadHeader(request, downloadName);
		} catch (UnsupportedEncodingException e) {
		}
		String headerFilename2 = headerFilename;

		ByteArrayInputStream s = new ByteArrayInputStream(data);
		return Mono.just(s).map(it -> {
			return ResponseEntity.ok()
				.header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
				.header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + headerFilename2 + "\"")
				.header(HttpHeaders.CONTENT_LENGTH, "" + data.length)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(new InputStreamResource(it));
		});

	}

}
