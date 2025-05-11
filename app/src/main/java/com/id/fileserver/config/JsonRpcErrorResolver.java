package com.id.fileserver.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.jsonrpc4j.ErrorResolver;

import java.lang.reflect.Method;
import java.util.List;

class JsonRpcErrorResolver implements ErrorResolver {

    private static final int ACCESS_ERROR = -32099;
    private static final int PARAM_ERROR = -32098;

    @Override
    public JsonError resolveError(Throwable t, Method method, List<JsonNode> arguments) {
        if (t instanceof SecurityException se) {
            return new JsonError(ACCESS_ERROR, se.getMessage(), se.getClass().getSimpleName());
        }
        if (t instanceof IllegalArgumentException iae) {
            return new JsonError(PARAM_ERROR, iae.getMessage(), iae.getClass().getSimpleName());
        }
        return null;
    }

}

