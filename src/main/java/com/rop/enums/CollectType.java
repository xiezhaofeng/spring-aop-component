package com.rop.enums;/**
 * Created by admin on 2017/12/19.
 */

/**
 * @author XZF
 * @Title: CollectType
 * @Description
 * @date 2017/12/19 15:09
 */
public enum CollectType {
    /**
     * 默认，不记录
     */
    DEFAULT,
    /**
     * 采集日志
     */
    YES,
    /**
     * 不采集日志
     */
    NO;

    CollectType(){

    }

    /**
     * 日志级别: debug, 采集: DEFAULT,YES
     * 日志级别: info, 采集: YES
     * @param isDebugEnabled
     * @return
     */
    public boolean isCollect(boolean isDebugEnabled){
        if(CollectType.YES.equals(this)){
            return true;
        }
        if(isDebugEnabled && CollectType.DEFAULT.equals(this)){
            return true;
        }
        return false;
    }
}
