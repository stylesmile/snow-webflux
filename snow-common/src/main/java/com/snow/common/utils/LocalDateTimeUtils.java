package com.snow.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具类
 */
public class LocalDateTimeUtils {

	/**
	 * 获取当前Date型日期
	 *
	 * @return Date() 当前日期
	 */
	public static LocalDateTime getNowDate() {
		return LocalDateTime.now();
	}

	/**
	 * 日期到 LocalDateTime
	 */
	public static LocalDateTime toLocalDateTime(Date date) {
		if (date == null) {
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * @param localDateTime
	 * @return
	 */
	public static Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * Format to 'yyyy-mm-dd'
	 */
	public static String format(final LocalDateTime date) {
		return date.format(DateTimeFormatter.ISO_DATE);
	}

	/**
	 * 日期型字符串转化为日期 格式
	 */
	public static LocalDateTime parseDate(Object str) {
		return toLocalDateTime(DateUtils.parseDate(str));
	}

	public static LocalDateTime dateTime(final String format, final String ts) {
		return toLocalDateTime(DateUtils.dateTime(format, ts));
	}

	/**
	 * 毫米差(after - before)
	 */
	public static long diffMillis(final LocalDateTime after, final LocalDateTime before) {
		return Duration.between(before, after).toMillis();
	}

	/**
	 * 毫米差(to - from)
	 */
	public static long durationToMillis(final LocalDateTime from, final LocalDateTime to) {
		return Duration.between(from, to).toMillis();
	}

	public static LocalDateTime plusDays(LocalDateTime date, long i) {
		return date.plusDays(i);
	}
}
