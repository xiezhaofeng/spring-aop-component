package com.aop.marshaller;

import java.io.IOException;

import com.aop.core.AbstractRopRequest;
import com.aop.core.RopRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by jers on 2017/6/4.
 */
public interface MarshallerService {

    String format(Object object) throws JsonProcessingException;

    AbstractRopRequest readvalue(String param, Class<? extends RopRequest> requestType) throws IOException;
}
