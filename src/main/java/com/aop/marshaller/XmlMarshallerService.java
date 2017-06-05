package com.aop.marshaller;

import com.aop.core.AbstractRopRequest;
import com.aop.core.RopRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * Created by jers on 2017/6/4.
 */
public class XmlMarshallerService implements MarshallerService {
    @Override
    public String format(Object object) throws JsonProcessingException {
        return null;//XMLParser.toXmlString(object);
    }

    @Override
    public AbstractRopRequest readvalue(String param, Class<? extends RopRequest> requestType) throws IOException {
        return null;//XMLParser.xmlMapper.readValue(param, (Class<T>) requestType);
    }
}
