package com.nicolasmesa.springboot.testcommon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.AbstractMockHttpServletRequestBuilder;

import java.net.URI;

public class RequestBuilder extends AbstractMockHttpServletRequestBuilder<RequestBuilder> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    RequestBuilder(HttpMethod httpMethod) {
        super(httpMethod);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static RequestBuilder get(String uriTemplate, Object... uriVariables) {
        return (new RequestBuilder(HttpMethod.GET)).uri(uriTemplate, uriVariables);
    }

    public static RequestBuilder get(URI uri) {
        return (new RequestBuilder(HttpMethod.GET)).uri(uri);
    }

    public static RequestBuilder post(String uriTemplate, Object... uriVariables) {
        return (new RequestBuilder(HttpMethod.POST)).uri(uriTemplate, uriVariables);
    }

    public static RequestBuilder put(String uriTemplate, Object... uriVariables) {
        return (new RequestBuilder(HttpMethod.PUT)).uri(uriTemplate, uriVariables);
    }

    public static RequestBuilder put(URI uri) {
        return (new RequestBuilder(HttpMethod.PUT)).uri(uri);
    }

    public static RequestBuilder patch(String uriTemplate, Object... uriVariables) {
        return (new RequestBuilder(HttpMethod.PATCH)).uri(uriTemplate, uriVariables);
    }

    public static RequestBuilder patch(URI uri) {
        return (new RequestBuilder(HttpMethod.PATCH)).uri(uri);
    }

    public static RequestBuilder delete(String uriTemplate, Object... uriVariables) {
        return (new RequestBuilder(HttpMethod.DELETE)).uri(uriTemplate, uriVariables);
    }

    public static RequestBuilder delete(URI uri) {
        return (new RequestBuilder(HttpMethod.DELETE)).uri(uri);
    }

    public RequestBuilder uri(String uriTemplate, Object... uriVariables) {
        return (RequestBuilder) super.uri(uriTemplate, uriVariables);
    }

    public RequestBuilder body(Object body) throws JsonProcessingException {
        super.contentType(MediaType.APPLICATION_JSON);
        String json = objectMapper.writeValueAsString(body);
        return (RequestBuilder) super.content(json);
    }
}
