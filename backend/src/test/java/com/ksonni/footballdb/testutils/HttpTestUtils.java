package com.ksonni.footballdb.testutils;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.when;

public class HttpTestUtils {

    public static void mockQuery(HttpServletRequest request, String query) {
        when(request.getQueryString()).thenReturn(query);
    }

}
