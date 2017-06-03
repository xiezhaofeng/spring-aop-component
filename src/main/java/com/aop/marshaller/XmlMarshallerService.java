package com.aop.marshaller;

import com.aop.core.RopRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.xunxintech.ruyue.coach.io.json.JSONUtil;
import com.xunxintech.ruyue.coach.io.xml.XMLParser;

import java.io.IOException;

/**
 * Created by jers on 2017/6/4.
 */
public class XmlMarshallerService implements MarshallerService {
    @Override
    public String format(Object object) throws JsonProcessingException {
        return XMLParser.toXmlString(object);
    }

    @Override
    public <T> T readvalue(String param, Class<? extends RopRequest> requestType) throws IOException {
        return XMLParser.xmlMapper.readValue(param, (Class<T>) requestType);
    }
}
