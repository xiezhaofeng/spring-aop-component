package com.rop.core.impl;


import com.rop.core.LogService;
import com.rop.enums.LogStatus;

import java.time.LocalDateTime;

/**
 * @author XZF
 * @Title: LogServiceImpl
 * @Package com.xunxintech.ruyue.coach.aop.impl
 * @Description
 * @date 2017/12/26 10:46
 * @Copyrigth 版权所有 (C) 2017 广州讯心信息科技有限公司.
 */
public class LogServiceImpl implements LogService {

    @Override
    public void logRecord(String logSn, String request, String response, LogStatus logStatus, LocalDateTime requestTime, String methodName) {

    }
}
