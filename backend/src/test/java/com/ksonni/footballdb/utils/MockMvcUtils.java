package com.ksonni.footballdb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class MockMvcUtils {

    private ObjectMapper mapper = new ObjectMapper();

    public MockHttpServletRequestBuilder postJSON(String path, Object object) throws JsonProcessingException {
        return setJSONContent(post(path), object);
    }

    public MockHttpServletRequestBuilder putJSON(String path, Object object) throws JsonProcessingException {
        return setJSONContent(put(path), object);
    }

    public MockHttpServletRequestBuilder patchJSON(String path, Object object) throws JsonProcessingException {
        return setJSONContent(patch(path), object);
    }

    private MockHttpServletRequestBuilder setJSONContent(MockHttpServletRequestBuilder builder, Object object) throws JsonProcessingException {
        return builder.accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(object))
                .contentType(MediaType.APPLICATION_JSON);
    }

}
