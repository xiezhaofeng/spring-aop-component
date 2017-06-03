package com.aop.marshaller;

import com.aop.core.RopRequest;
import com.aop.enums.MessageFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.xunxintech.ruyue.coach.io.json.JSONUtil;

import java.io.IOException;

/**
 * Created by jers on 2017/6/4.
 */
public class JsonMarshallerService implements MarshallerService {
    @Override
    public String format(Object object) throws JsonProcessingException {
        return JSONUtil.toJackson(object);
    }

    @Override
    public <T> T readvalue(String param, Class<? extends RopRequest> requestType) throws IOException {
        return JSONUtil.objectMapper.readValue(param, (Class<T>) requestType);
    }
}
