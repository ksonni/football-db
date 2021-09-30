package com.ksonni.footballdb.lib;

import com.ksonni.footballdb.queryapi.InvalidQueryKeyException;
import com.ksonni.footballdb.queryapi.InvalidQueryValueException;
import com.ksonni.footballdb.queryapi.Query;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpUtils {

    public static class QueryParseException extends Exception {
        public QueryParseException(String message) { super(message); }
    }

    public static URI getRequestURI(HttpServletRequest request) throws URISyntaxException {
        String s = request.getRequestURI() + "?" + request.getQueryString();
        return new URI(s);
    }

    public static <T> Query<T> parseRequestQuery(HttpServletRequest request, Class<T> objectType) throws QueryParseException {
        try {
            URI uri = getRequestURI(request);
            return new Query<>(uri, objectType);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new QueryParseException("Unable to parse query due to invalid request URL");
        } catch (InvalidQueryValueException e) {
            throw new QueryParseException(e.getMessage());
        } catch (InvalidQueryKeyException e) {
            throw new QueryParseException(e.getMessage());
        }
    }

}
