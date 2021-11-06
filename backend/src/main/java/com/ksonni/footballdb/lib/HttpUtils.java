package com.ksonni.footballdb.lib;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpUtils {

    public static URI getRequestURI(HttpServletRequest request) throws URISyntaxException {
        String s = request.getRequestURI() + "?" + request.getQueryString();
        return new URI(s);
    }

}
