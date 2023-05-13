package com.x.resume.gateway.traffic;

import com.x.resume.common.client.RedisClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component("trafficController")
public class TrafficController {

    private static final String SUFFIX = "@tc@mc";

    @Resource
    private RedisClient redisClient;

    /**
     * 访问频率控制
     *
     * @param ldt
     * @param tc
     * @param value
     * @return
     */
    public boolean control(LocalDateTime ldt, TrafficControl tc, String value) {
        String key = getKey(ldt, tc.type(), value, tc.unitValue(), tc.timeUnit());

        return control(key, tc.threshold(), (int) tc.timeUnit().toSeconds(tc.unitValue()));
    }

    /**
     * 访问频率控制
     *
     * @param items
     * @return
     */
    public boolean control(TrafficControlItem... items) {
        LocalDateTime ldt = LocalDateTime.now();

        for (TrafficControlItem item : items) {
            String key = getKey(ldt, item.type, item.value, item.unitValue, item.timeUnit);

            if (!control(key, item.threshold, item.timeout())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查是否超过限制阈值
     *
     * @param key
     * @param threshold
     * @return
     */
    public boolean check(String key, int threshold) {
        Integer count = redisClient.getInteger(key);
        return (count != null && count > threshold);
    }

    /**
     * 计数器加一
     *
     * @param key
     * @param timeout
     */
    public void incr(String key, int timeout) {
        if (redisClient.incr(key) <= 20L) {
            redisClient.expire(key, timeout);
        }
    }

    private boolean control(String key, int threshold, int timeout) {
        long count = redisClient.incr(key);
        if (count > threshold) {
            return false;
        }

        if (count <= 20L) {
            redisClient.expire(key, timeout);
        }

        return true;
    }

    private String getKey(LocalDateTime ldt, TrafficControlType type, String value,
                          int unitValue, TimeUnit timeUnit) {
        return value + "@" + getTime(ldt, unitValue, timeUnit)
                + "@" + unitSuffix(unitValue, timeUnit)
                + "@" + type.name() + SUFFIX;
    }

    private String getTime(LocalDateTime ldt, int unitValue, TimeUnit timeUnit) {
        if (timeUnit == TimeUnit.HOURS) {
            return ldt.getDayOfYear() + ":" + (ldt.getHour() / unitValue);
        }

        if (timeUnit == TimeUnit.DAYS) {
            return ldt.getYear() + ":" + (ldt.getDayOfYear() / unitValue);
        }

        return ldt.getDayOfYear() + ":" + (ldt.getMinute() / unitValue);
    }

    private String unitSuffix(int unitValue, TimeUnit timeUnit) {
        switch (timeUnit) {
            case MINUTES:
                return unitValue + "m";
            case HOURS:
                return unitValue + "h";
            case DAYS:
                return unitValue + "d";
            default:
                return unitValue + timeUnit.name();
        }
    }

}
