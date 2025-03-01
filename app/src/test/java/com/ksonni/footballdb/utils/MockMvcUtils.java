package com.ksonni.footballdb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * Helpers to make using Mock MVC easier.
 */
public class MockMvcUtils {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Mock GET request with https.
     *
     * @param path path to send request
     * @return mock
     */
    public MockHttpServletRequestBuilder get(final String path) {
        return MockMvcRequestBuilders.get(path).secure(true);
    }

    /**
     * Mock POST request with https.
     *
     * @param path path to send request
     * @return mock
     */
    public MockHttpServletRequestBuilder post(final String path) {
        return MockMvcRequestBuilders.post(path).secure(true);
    }

    /**
     * Mock DELETE request with https.
     *
     * @param path path to send request
     * @return mock
     */
    public MockHttpServletRequestBuilder delete(final String path) {
        return MockMvcRequestBuilders.delete(path).secure(true);
    }

    /**
     * Mock PUT request with https.
     *
     * @param path path to send request
     * @return mock
     */
    public MockHttpServletRequestBuilder put(final String path) {
        return MockMvcRequestBuilders.put(path).secure(true);
    }

    /**
     * Mock PATCH request with https.
     *
     * @param path path to send request
     * @return mock
     */
    public MockHttpServletRequestBuilder patch(final String path) {
        return MockMvcRequestBuilders.patch(path).secure(true);
    }

    /**
     * Mock POST request with https.
     *
     * @param path   path to send request
     * @param object any JSON serializable object to send with the request
     * @return mock
     */
    public MockHttpServletRequestBuilder postJSON(final String path, final Object object)
            throws JsonProcessingException {
        return setJSONContent(post(path), object);
    }

    /**
     * Mock PUT request with https.
     *
     * @param path   path to send request
     * @param object any JSON serializable object to send with the request
     * @return mock
     */
    public MockHttpServletRequestBuilder putJSON(final String path, final Object object)
            throws JsonProcessingException {
        return setJSONContent(put(path), object);
    }

    /**
     * Mock PATCH request with https.
     *
     * @param path   path to send request
     * @param object any JSON serializable object to send with the request
     * @return mock
     */
    public MockHttpServletRequestBuilder patchJSON(final String path, final Object object)
            throws JsonProcessingException {
        return setJSONContent(patch(path), object);
    }

    private MockHttpServletRequestBuilder setJSONContent(final MockHttpServletRequestBuilder builder,
                                                         final Object object)
            throws JsonProcessingException {
        return builder.accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(object))
                .contentType(MediaType.APPLICATION_JSON);
    }

}
