package com.rop.enums;
/**
 * @author XZF
 * @Title: LogStatus
 * @Package com.xunxintech.ruyue.coach.aop.enums
 * @Description
 * @date 2017/12/26 10:40
 * @Copyrigth 版权所有 (C) 2017 广州讯心信息科技有限公司.
 */
public enum LogStatus {
    /**
     * 请求中
     */
    reading(1),
    /**
     * 请求成功
     */
    success(2),
    /**
     * 失败
     */
    failure(3);

    int value;

    LogStatus(int v){
        value = v;
    }

    public int value(){
        return value;
    }
}
