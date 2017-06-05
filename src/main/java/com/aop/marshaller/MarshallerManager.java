package com.aop.marshaller;

import com.aop.core.AbstractRopRequest;
import com.aop.core.RopRequest;
import com.aop.enums.MessageFormat;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jers on 2017/6/4.
 */
public class MarshallerManager {

    private Map<MessageFormat, MarshallerService> marshallerServiceMap = new ConcurrentHashMap<MessageFormat, MarshallerService>();

    public void addMarshaller(MessageFormat format, MarshallerService marshallerService){
        marshallerServiceMap.put(format, marshallerService);
    }

    public MarshallerService getMarshaller(MessageFormat format){
        return marshallerServiceMap.get(format );
    }

    public String messageFormat(MessageFormat format, Object object) throws JsonProcessingException {
        return getMarshaller(format).format(object );
    }

    public AbstractRopRequest readvalue(String param, Class<? extends RopRequest> requestType, MessageFormat format) throws IOException {
        return getMarshaller(format).readvalue(param,requestType);
    }
}
