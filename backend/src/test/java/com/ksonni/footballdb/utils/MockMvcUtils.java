package com.ksonni.footballdb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class MockMvcUtils {

    private ObjectMapper mapper = new ObjectMapper();

    public MockHttpServletRequestBuilder get(String path) {
        return MockMvcRequestBuilders.get(path).secure(true);
    }

    public MockHttpServletRequestBuilder post(String path) {
        return MockMvcRequestBuilders.post(path).secure(true);
    }

    public MockHttpServletRequestBuilder delete(String path) {
        return MockMvcRequestBuilders.delete(path).secure(true);
    }

    public MockHttpServletRequestBuilder put(String path) {
        return MockMvcRequestBuilders.put(path).secure(true);
    }

    public MockHttpServletRequestBuilder patch(String path) {
        return MockMvcRequestBuilders.patch(path).secure(true);
    }

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
