package com.id.fileserver.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.jsonrpc4j.ErrorResolver;

import java.lang.reflect.Method;
import java.util.List;

class JsonRpcErrorResolver implements ErrorResolver {

    private static final int ACCESS_ERROR = -32099;

    @Override
    public JsonError resolveError(Throwable t, Method method, List<JsonNode> arguments) {
        if (t instanceof SecurityException se) {
            return new JsonError(ACCESS_ERROR, se.getMessage(), se.getClass().getSimpleName());
        }
        return null;
    }

}

