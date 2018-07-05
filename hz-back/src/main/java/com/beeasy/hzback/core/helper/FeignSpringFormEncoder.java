//package com.beeasy.hzback.core.helper;
//
//import feign.RequestTemplate;
//import feign.codec.EncodeException;
//import org.springframework.beans.factory.ObjectFactory;
//import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
//import org.springframework.cloud.netflix.feign.support.SpringEncoder;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpOutputMessage;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.lang.reflect.Type;
//import java.nio.charset.Charset;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author elvis.xu
// * @since 2017-04-11 15:33
// */
//public class FeignSpringFormEncoder extends SpringEncoder {
//
//    protected ObjectFactory<HttpMessageConverters> messageConverters;
//    protected HttpHeaders multipartHeaders = new HttpHeaders();
//    public static final Charset UTF_8 = Charset.forName("UTF-8");
//
//    public FeignSpringFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
//        super(messageConverters);
//        this.messageConverters = messageConverters;
//        multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
//    }
//
//    protected static boolean isFormRequest(Type type) {
//        return MAP_STRING_WILDCARD.equals(type);
//    }
//
//    protected static boolean isMultipart(Object body, Type bodyType) {
//        if (isFormRequest(bodyType)) {
//            Map<String, ?> map = (Map<String, ?>) body;
//            for (Map.Entry<String, ?> entry : map.entrySet()) {
//                Object value = entry.getValue();
//                if (isMultipartFile(value) || isMultipartFileArray(value)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    protected static boolean isMultipartFile(Object obj) {
//        return obj instanceof MultipartFile;
//    }
//
//    protected static boolean isMultipartFileArray(Object o) {
//        return o != null && o.getClass().isArray() && MultipartFile.class.isAssignableFrom(o.getClass().getComponentType());
//    }
//
//    @Override
//    public void encode(Object requestBody, Type bodyType, RequestTemplate request) throws EncodeException {
//        if (isMultipart(requestBody, bodyType)) {
//            encodeMultipartFormRequest((Map<String, ?>) requestBody, request);
//        } else {
//            super.encode(requestBody, bodyType, request);
//        }
//    }
//
//    /**
//     * Encodes the request as a multipart form. It can detect a single {@link MultipartFile}, an
//     * array of {@link MultipartFile}s, or POJOs (that are converted to JSON).
//     *
//     * @param formMap
//     * @param template
//     * @throws EncodeException
//     */
//    private void encodeMultipartFormRequest(Map<String, ?> formMap, RequestTemplate template) throws EncodeException {
//        if (formMap == null) {
//            throw new EncodeException("Cannot encode request with null form.");
//        }
//        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
//        for (Map.Entry<String, ?> entry : formMap.entrySet()) {
//            Object value = entry.getValue();
//            if (isMultipartFile(value)) {
//                map.add(entry.getKey(), encodeMultipartFile((MultipartFile) value));
//            } else if (isMultipartFileArray(value)) {
//                encodeMultipartFiles(map, entry.getKey(), Arrays.asList((MultipartFile[]) value));
//            } else {
//                map.add(entry.getKey(), encodeJsonObject(value));
//            }
//        }
//        encodeRequest(map, multipartHeaders, template);
//    }
//
//    /**
//     * Wraps a single {@link MultipartFile} into a {@link HttpEntity} and sets the
//     * {@code Content-type} header to {@code application/octet-stream}
//     *
//     * @param file
//     * @return
//     */
//    private HttpEntity<?> encodeMultipartFile(MultipartFile file) {
//        HttpHeaders filePartHeaders = new HttpHeaders();
//        filePartHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        try {
//            Resource multipartFileResource = new MultipartFileResource(file.getOriginalFilename(), file.getSize(), file.getInputStream());
//            return new HttpEntity<>(multipartFileResource, filePartHeaders);
//        } catch (IOException ex) {
//            throw new EncodeException("Cannot encode request.", ex);
//        }
//    }
//
//    /**
//     * Fills the request map with {@link HttpEntity}s containing the given {@link MultipartFile}s.
//     * Sets the {@code Content-type} header to {@code application/octet-stream} for each file.
//     *
//     * @param map the current request map.
//     * @param name the name of the array field in the multipart form.
//     * @param files
//     */
//    private void encodeMultipartFiles(LinkedMultiValueMap<String, Object> map, String name, List<? extends MultipartFile> files) {
//        HttpHeaders filePartHeaders = new HttpHeaders();
//        filePartHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        try {
//            for (MultipartFile file : files) {
//                Resource multipartFileResource = new MultipartFileResource(file.getOriginalFilename(), file.getSize(), file.getInputStream());
//                map.add(name, new HttpEntity<>(multipartFileResource, filePartHeaders));
//            }
//        } catch (IOException ex) {
//            throw new EncodeException("Cannot encode request.", ex);
//        }
//    }
//
//    /**
//     * Wraps an object into a {@link HttpEntity} and sets the {@code Content-type} header to
//     * {@code application/json}
//     *
//     * @param o
//     * @return
//     */
//    private HttpEntity<?> encodeJsonObject(Object o) {
//        HttpHeaders jsonPartHeaders = new HttpHeaders();
//        jsonPartHeaders.setContentType(MediaType.APPLICATION_JSON);
//        return new HttpEntity<>(o, jsonPartHeaders);
//    }
//
//    /**
//     * Calls the conversion chain actually used by
//     * {@link org.springframework.web.client.RestTemplate}, filling the body of the request
//     * template.
//     *
//     * @param value
//     * @param requestHeaders
//     * @param template
//     * @throws EncodeException
//     */
//    private void encodeRequest(Object value, HttpHeaders requestHeaders, RequestTemplate template) throws EncodeException {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        HttpOutputMessage dummyRequest = new HttpOutputMessageImpl(outputStream, requestHeaders);
//        try {
//            Class<?> requestType = value.getClass();
//            MediaType requestContentType = requestHeaders.getContentType();
//            for (HttpMessageConverter<?> messageConverter : messageConverters.getObject().getConverters()) {
//                if (messageConverter.canWrite(requestType, requestContentType)) {
//                    ((HttpMessageConverter<Object>) messageConverter).write(value, requestContentType, dummyRequest);
//                    break;
//                }
//            }
//        } catch (IOException ex) {
//            throw new EncodeException("Cannot encode request.", ex);
//        }
//        HttpHeaders headers = dummyRequest.getHeaders();
//        if (headers != null) {
//            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
//                template.header(entry.getKey(), entry.getValue());
//            }
//        }
//        /*
//        we should use a template output stream... this will cause issues if files are too big,
//        since the whole request will be in memory.
//         */
//        template.body(outputStream.toByteArray(), UTF_8);
//    }
//
//    /**
//     * Dummy resource class. Wraps file content and its original name.
//     */
//    static class MultipartFileResource extends InputStreamResource {
//
//        private final String filename;
//        private final long size;
//
//        public MultipartFileResource(String filename, long size, InputStream inputStream) {
//            super(inputStream);
//            this.size = size;
//            this.filename = filename;
//        }
//
//        @Override
//        public String getFilename() {
//            return this.filename;
//        }
//
//        @Override
//        public InputStream getInputStream() throws IOException, IllegalStateException {
//            return super.getInputStream(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public long contentLength() throws IOException {
//            return size;
//        }
//
//    }
//
//    /**
//     * Minimal implementation of {@link org.springframework.http.HttpOutputMessage}. It's needed to
//     * provide the request body output stream to
//     * {@link org.springframework.http.converter.HttpMessageConverter}s
//     */
//    private class HttpOutputMessageImpl implements HttpOutputMessage {
//
//        private final OutputStream body;
//        private final HttpHeaders headers;
//
//        public HttpOutputMessageImpl(OutputStream body, HttpHeaders headers) {
//            this.body = body;
//            this.headers = headers;
//        }
//
//        @Override
//        public OutputStream getBody() throws IOException {
//            return body;
//        }
//
//        @Override
//        public HttpHeaders getHeaders() {
//            return headers;
//        }
//
//    }
//}
//
