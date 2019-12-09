package com.study.bindr;

/**
 * Used to get results from database
 * @param <T>
 */
public interface DatabaseCallBack<T> {
    void onCallback(T items);
}
