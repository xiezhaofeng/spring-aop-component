package com.aop.marshaller;

import com.aop.enums.MessageFormat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jers on 2017/6/4.
 */
public class MessageFormatMarshaller {

    private Map<MessageFormat, MarshallerService> marshallerServiceMap = new ConcurrentHashMap<MessageFormat, MarshallerService>();

    public void addMarshaller(MessageFormat format, MarshallerService marshallerService){
        marshallerServiceMap.put(format, marshallerService);
    }

    public MarshallerService getMarshaller(MessageFormat format){
        return marshallerServiceMap.get(format );
    }

    public String messageFormat(MessageFormat format, Object object){
        return getMarshaller(format).format(object );
    }
}
