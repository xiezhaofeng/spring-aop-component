package com.rop.marshaller;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rop.core.AbstractRopRequest;
import com.rop.core.RopRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author XZF
 */
public interface MarshallerService {
    /**
     * converter object to string
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    String format(Object object) throws JsonProcessingException;

    /**
     * Conversion for the type of custom
     * @param param
     * @param requestType
     * @return
     * @throws IOException
     */
    AbstractRopRequest readValue(String param, Class<? extends RopRequest> requestType) throws IOException;

    /**
     * 泛型转换器
     * @param param
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T readRequest(String param, TypeReference<T> type) throws IOException;
}
