package com.x.resume.gateway.traffic;


import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(TrafficControls.class)
public @interface TrafficControl {

    /**
     * 流量控制类型
     *
     * @return
     */
    TrafficControlType type() default TrafficControlType.IP;

    /**
     * 阈值
     *
     * @return
     */
    int threshold();

    /**
     * 时间值
     *
     * @return
     */
    int unitValue() default 1;

    /**
     * 时间单位,支持分、小时、天三种
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
