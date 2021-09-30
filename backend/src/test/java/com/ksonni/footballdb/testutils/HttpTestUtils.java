package com.ksonni.footballdb.testutils;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.when;

public class HttpTestUtils {

    private static final String TEST_SITE = "https://ksonni.com";

    public static void mockQuery(HttpServletRequest request, String query) {
        when(request.getRequestURI()).thenReturn(TEST_SITE);
        when(request.getQueryString()).thenReturn(query);
    }

}
