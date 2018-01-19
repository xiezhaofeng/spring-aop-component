package com.rop.marshaller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rop.core.AbstractRopRequest;
import com.rop.core.RopRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * @author XZF
 */
public class XmlMarshallerServiceImpl implements MarshallerService {
    @Override
    public String format(Object object) throws JsonProcessingException {
        return null;//XMLParser.toXmlString(object);
    }

    @Override
    public AbstractRopRequest readValue(String param, Class<? extends RopRequest> requestType) throws IOException {
        return null;//XMLParser.xmlMapper.readValue(param, (Class<T>) requestType);
    }

    @Override
    public <T> T readRequest(String param, TypeReference<T> type) throws IOException {
        return null;
    }
}
