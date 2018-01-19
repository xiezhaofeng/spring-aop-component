package com.rop.marshaller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rop.core.AbstractRopRequest;
import com.rop.core.RopRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * @author xzf
 */
public class JsonMarshallerServiceImpl implements MarshallerService {
    @Override
    public String format(Object object) throws JsonProcessingException {
        return null;//JSONUtil.toJackson(object);
    }

    @Override
    public AbstractRopRequest readValue(String param, Class<? extends RopRequest> requestType) throws IOException {
        return null;//JSONUtil.objectMapper.readValue(param, (Class<T>) requestType);
    }

    @Override
    public <T> T readRequest(String param, TypeReference<T> type) throws IOException {
        return null;
    }
}
