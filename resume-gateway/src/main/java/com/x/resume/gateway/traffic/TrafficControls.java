package com.x.resume.gateway.traffic;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface TrafficControls {

    TrafficControl[] value();
}
