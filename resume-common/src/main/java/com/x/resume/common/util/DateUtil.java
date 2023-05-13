package com.x.resume.common.util;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.x.resume.common.util.Log.format;

/**
 * create by runxiu.zhao
 */
public class DateUtil extends DateUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    public static Date parseDate(String str, FORMAT format) {
        try {
            return parseDate(str, new String[]{format.getCode()});
        } catch (ParseException ex) {
            LOG.error(Log.format("时间格式转换失败", Log.kv("value", str), Log.kv("parse", format.getCode())));
        }
        return null;
    }

    /**
     * 取得指定日期格式的字符串
     *
     * @return String
     */
    public static String formatDate(Date date, FORMAT format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format.getCode());
        return dateFormat.format(date);
    }

    /**
     * 时间格式转换
     *
     * @param value 时间
     * @param from  原始格式
     * @param to    转换后格式
     * @return
     */
    public static String formatSwitch(String value, FORMAT from, FORMAT to) {

        Date date = DateUtil.parseDate(value, from);

        return DateUtil.formatDate(date, to);
    }

    /**
     * 获取倒计时
     *
     * @param startTime 开始计算时间
     * @param minute    倒时数分钟
     * @return 剩余时间，单位秒
     */
    public static Long countdown(long startTime, long minute) {
        long countdown = TimeUnit.MINUTES.toSeconds(minute) - (System.currentTimeMillis() - startTime) / 1000;
        return countdown > 0 ? countdown : 0;
    }

    /**
     * 判断两时间点的间隔是否大于days
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param days      时间间隔
     * @return boolean
     */
    public static boolean checkTimeRang(Long startTime, Long endTime, Long days) {
        if (null == startTime || null == endTime || null == days) {
            return false;
        }

        if (startTime > endTime) {
            return false;
        }
        Long time = days * 24 * 60 * 60 * 1000L;
        Long diffTime = endTime - startTime;
        return diffTime.compareTo(time) >= 0;
    }

    /**
     * 获取本月第一天
     */
    public static Long getFirstDay4ThisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTimeInMillis();
    }

    /**
     * 获取本月最后一天
     */
    public static Long getLastDay4ThisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return calendar.getTimeInMillis();
    }

    public enum FORMAT {
        YYYY("yyyy"),
        YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
        YYYY_MM_DD("yyyy-MM-dd"),
        YYYYMMDD("yyyyMMdd"),
        YYYYMMDDHHMMSS("yyyyMMddhhmmss"),
        YYYYMMDDHHMMSS1("yyyyMMddHHmmss"),
        YYYYMMDDHHMMSS2("YYYYMMDDHHMMSS"),
        ;


        private final String code;

        FORMAT(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
