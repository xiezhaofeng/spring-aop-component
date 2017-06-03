package com.aop.marshaller;

import com.aop.core.RopRequest;
import com.aop.enums.MessageFormat;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * Created by jers on 2017/6/4.
 */
public interface MarshallerService {

    String format(Object object) throws JsonProcessingException;

    <T> T readvalue(String param, Class<? extends RopRequest> requestType) throws IOException;
}
