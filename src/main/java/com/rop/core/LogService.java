package com.rop.core;


import com.rop.enums.LogStatus;

import java.time.LocalDateTime;

/**
 * @author XZF
 * @Title: LogService
 * @Package com.xunxintech.ruyue.coach.aop
 * @Description 日志采集工具类
 * @date 2017/12/26 9:52
 * @Copyrigth 版权所有 (C) 2017 广州讯心信息科技有限公司.
 */
public interface LogService {

    /**
     * 记录日志
     * @param logSn
     * @param request
     * @param response
     * @param logStatus
     * @param requestTime
     * @param methodName
     */
    void logRecord(String logSn, String request, String response, LogStatus logStatus, LocalDateTime requestTime, String methodName);
}
