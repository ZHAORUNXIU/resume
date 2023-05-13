package com.x.resume.gateway.traffic;

import java.util.concurrent.TimeUnit;

public class TrafficControlItem {
    // 流控类型
    protected final TrafficControlType type;

    // 流控对象的值
    protected final String value;

    // 流控阈值
    protected final int threshold;

    // 流控周期单位
    protected final TimeUnit timeUnit;

    // 流控周期值
    protected final int unitValue;

    public TrafficControlItem(TrafficControlType type, String value, int threshold) {
        this(type, value, threshold, TimeUnit.MINUTES);
    }

    public TrafficControlItem(TrafficControlType type, String value, int threshold, TimeUnit timeUnit) {
        this(type, value, threshold, 1, timeUnit);
    }

    public TrafficControlItem(TrafficControlType type, String value, int threshold, int unitValue, TimeUnit timeUnit) {
        this.type = type;
        this.value = value;
        this.threshold = threshold;
        this.unitValue = unitValue;
        this.timeUnit = timeUnit;
    }

    public int timeout() {
        return (int) timeUnit.toSeconds(unitValue);
    }
}
