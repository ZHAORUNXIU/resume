package com.x.resume.common.manager.cache;

public interface CacheCallBack<T> {

    T execute(Object... key);

}
