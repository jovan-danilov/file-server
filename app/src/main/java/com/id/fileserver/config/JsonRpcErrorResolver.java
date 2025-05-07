package com.id.fileserver.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.jsonrpc4j.ErrorResolver;

import java.lang.reflect.Method;
import java.util.List;

class JsonRpcErrorResolver implements ErrorResolver {

    @Override
    public JsonError resolveError(Throwable t, Method method, List<JsonNode> arguments) {
        return null;
    }

}

