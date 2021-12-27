package com.ksonni.footballdb.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public final class HttpUtils {

    private HttpUtils() {
    }

    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

}
