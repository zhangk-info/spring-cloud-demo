package com.zk.importbigdata.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期工具类
 * 
 * @author
 */
@Slf4j
public class DateUtils {

	public static final String Y_M_D = "yyyy-MM-dd";
	public static final String Y_M_D_HM = "yyyy-MM-dd HH:mm";
	public static final String Y_M_D_HMS = "yyyy-MM-dd HH:mm:ss";
	public static final String YMD = "yyyyMMdd";
	public static final String YMDHM = "yyyyMMddHHmm";
	public static final String YMDHMS = "yyyyMMddHHmmss";
	public static final String ymd = "yyyy/MM/dd";
	public static final String ymd_HM = "yyyy/MM/dd HH:mm";
	public static final String ymd_HMS = "yyyy/MM/dd HH:mm:ss";

	/**
	 * 智能转换日期
	 *
	 * @param date
	 * @return
	 */
	public static String smartFormat(Date date) {
		String dateStr = null;
		if (date == null) {
			dateStr = "";
		} else {
			try {
				dateStr = formatDate(date, Y_M_D_HMS);
				// 时分秒
				if (dateStr.endsWith(" 00:00:00")) {
					dateStr = dateStr.substring(0, 10);
				}
				// 时分
				else if (dateStr.endsWith("00:00")) {
					dateStr = dateStr.substring(0, 16);
				}
				// 秒
				else if (dateStr.endsWith(":00")) {
					dateStr = dateStr.substring(0, 16);
				}
			} catch (Exception ex) {
				throw new IllegalArgumentException("转换日期失败: " + ex.getMessage(), ex);
			}
		}
		return dateStr;
	}

	/**
	 * 智能转换日期
	 *
	 * @param text
	 * @return
	 */
	public static Date smartFormat(String text) {
		Date date = null;
		try {
			if (text == null || text.length() == 0) {
				date = null;
			} else {
				int length = text.length();
				if (text.indexOf("-") > 0) {
					if (length == 10) {
						date = formatStringToDate(text, Y_M_D);
					} else if (length == 16) {
						date = formatStringToDate(text, Y_M_D_HM);
					} else if (length == 19) {
						date = formatStringToDate(text, Y_M_D_HMS);
					} else {
						throw new IllegalArgumentException("日期长度不符合要求!");
					}
				} else if (text.indexOf("/") > 0) {
					if (length == 10) {
						date = formatStringToDate(text, ymd);
					} else if (length == 16) {
						date = formatStringToDate(text, ymd_HM);
					} else if (length == 19) {
						date = formatStringToDate(text, ymd_HMS);
					} else {
						throw new IllegalArgumentException("日期长度不符合要求!");
					}
				} else {
					if (length == 10) {
						date = formatStringToDate(text, YMD);
					} else if (length == 13) {
						date = new Date(Long.parseLong(text));
					} else if (length == 16) {
						date = formatStringToDate(text, YMDHM);
					} else if (length == 19) {
						date = formatStringToDate(text, YMDHMS);
					} else {
						throw new IllegalArgumentException("日期长度不符合要求!");
					}
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("日期格式错误!");
		}
		return date;
	}

	/**
	 * 获取当前日期
	 *
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public static String getNow(String format) throws Exception {
		return formatDate(new Date(), format);
	}

	/**
	 * 格式化日期格式
	 *
	 * @param argDate
	 * @param argFormat
	 * @return 格式化后的日期字符串
	 */
	public static String formatDate(Date argDate, String argFormat) throws Exception {
		if (argDate == null) {
			throw new Exception("参数[日期]不能为空!");
		}
		if (argFormat == null || argFormat.length() == 0) {
			argFormat = Y_M_D;
		}
		SimpleDateFormat sdfFrom = new SimpleDateFormat(argFormat);
		return sdfFrom.format(argDate).toString();
	}

	public static String formatDate(Long time, String argFormat) throws Exception {
		Date date = new Date();
		date.setTime(time);
		if (argFormat == null || argFormat.length() == 0) {
			argFormat = Y_M_D;
		}
		SimpleDateFormat sdfFrom = new SimpleDateFormat(argFormat);
		return sdfFrom.format(date).toString();
	}

	/**
	 * 把字符串格式化成日期
	 *
	 * @param argDateStr
	 * @param argFormat
	 * @return
	 */
	public static Date formatStringToDate(String argDateStr, String argFormat) throws Exception {
		if (argDateStr == null || argDateStr.trim().length() < 1) {
			throw new Exception("参数[日期]不能为空!");
		}
		String strFormat = argFormat;
		if (strFormat == null || strFormat.length() == 0) {
			strFormat = Y_M_D;
			if (argDateStr.length() > 16) {
				strFormat = Y_M_D_HMS;
			} else if (argDateStr.length() > 10) {
				strFormat = Y_M_D_HM;
			}
		}
		SimpleDateFormat sdfFormat = new SimpleDateFormat(strFormat);
		// 严格模式
		sdfFormat.setLenient(false);
		try {
			return sdfFormat.parse(argDateStr);
		} catch (ParseException e) {
			throw new Exception(e);
		}
	}

	public static long getTime(Calendar c, int hour, int minute) {
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		return c.getTimeInMillis();
	}

	public static boolean isDate(String date) {
		/**
		 * 判断日期格式和范围
		 */
		String rexp = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
		Pattern pat = Pattern.compile(rexp);
		Matcher mat = pat.matcher(date);
		boolean dateType = mat.matches();
		return dateType;
	}

	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	public static int getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH) + 1;
	}

	public static int getDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public static String[] weeks = { "", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };

	public static String getWeek(Date date) {
		if (date == null) {
			return "";
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return weeks[c.get(Calendar.DAY_OF_WEEK)];
	}

	// 获取两个日期之间相隔多少天
	public static long getDiffDays(String startDate, String endDate) throws Exception {
		Date start = DateUtils.formatStringToDate(startDate, DateUtils.Y_M_D);
		Date end = DateUtils.formatStringToDate(endDate, DateUtils.Y_M_D);
		return Math.abs((end.getTime() - start.getTime()) / 1000 / 60 / 60 / 24);
	}

	public static long getDiffDays(Date start, Date end) throws Exception {
		return Math.abs((end.getTime() - start.getTime()) / 1000 / 60 / 60 / 24);
	}

	public static long getDiffDays(long start, long end) throws Exception {
		return Math.abs((start - end) / 1000 / 60 / 60 / 24);
	}

	/**
	 * @return
	 */
	public static String getCurrentStringDate(String frm) {
		try {
			return formatDate(new Date(), frm);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取多少天之前的日期 day 指定天
	 * 
	 * @return
	 */
	public static String getBeforeStringDate(int day) {
		try {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_YEAR, -day);
			return formatDate(c.getTime(), Y_M_D);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date getBeforeDate(int day) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -day);
		return c.getTime();
	}
	
	
	public static String getAfterStringDate(int day) {
		try {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_YEAR, day);
			return formatDate(c.getTime(), Y_M_D);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date getAfeterDate(int day) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, day);
		return c.getTime();
	}

	 //获取指定年中指定的周的开始日期
	public static String getStartDayOfWeekNo(int year, int weekNo) {
		Calendar cal = getCalendarFormYear(year);
		cal.set(Calendar.WEEK_OF_YEAR, weekNo);
		String month = intToString(cal.get(Calendar.MONTH) + 1);
		String day = intToString(cal.get(Calendar.DAY_OF_MONTH));
		return cal.get(Calendar.YEAR) + "-" + month + "-" + day;

	}
	
    //获取指定年中指定的周的结束日期
	public static String getEndDayOfWeekNo(int year, int weekNo) {
		Calendar cal = getCalendarFormYear(year);
		cal.set(Calendar.WEEK_OF_YEAR, weekNo);
		cal.add(Calendar.DAY_OF_WEEK, 6);
		String month = intToString(cal.get(Calendar.MONTH) + 1);
		String day = intToString(cal.get(Calendar.DAY_OF_MONTH));
		return cal.get(Calendar.YEAR) + "-" + month + "-" + day;
	}
	
    //获取当前年
	public static Calendar getCalendarFormYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.YEAR, year);
		return cal;
	}
	
	private static String intToString(int i){
		String str = "";
		if(i < 10){
			str = "0"+i;
		}else{
			str = i+"";
		}
		return str;
	}

	// 获得本周一0点时间
	public static Date getTimesWeekmorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return  cal.getTime();
	}

	// 获得本周日24点时间
	public  static Date getTimesWeeknight() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getTimesWeekmorning());
		cal.add(Calendar.DAY_OF_WEEK, 7);
		return cal.getTime();
	}

	/**
	 * 当前季度的开始时间
	 *
	 * @return
	 */
	public static Date getCurrentQuarterStartTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		try {
			if (currentMonth >= 1 && currentMonth <= 3)
				c.set(Calendar.MONTH, 0);
			else if (currentMonth >= 4 && currentMonth <= 6)
				c.set(Calendar.MONTH, 3);
			else if (currentMonth >= 7 && currentMonth <= 9)
				c.set(Calendar.MONTH, 6);
			else if (currentMonth >= 10 && currentMonth <= 12)
				c.set(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return c.getTime();
	}

	/**
	 * 当前季度的结束时间
	 *
	 * @return
	 */
	public static Date getCurrentQuarterEndTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		try {
			if (currentMonth >= 1 && currentMonth <= 3) {
				c.set(Calendar.MONTH, 2);
				c.set(Calendar.DATE, 31);
			} else if (currentMonth >= 4 && currentMonth <= 6) {
				c.set(Calendar.MONTH, 5);
				c.set(Calendar.DATE, 30);
			} else if (currentMonth >= 7 && currentMonth <= 9) {
				c.set(Calendar.MONTH, 8);
				c.set(Calendar.DATE, 30);
			} else if (currentMonth >= 10 && currentMonth <= 12) {
				c.set(Calendar.MONTH, 11);
				c.set(Calendar.DATE, 31);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return c.getTime();
	}
}
