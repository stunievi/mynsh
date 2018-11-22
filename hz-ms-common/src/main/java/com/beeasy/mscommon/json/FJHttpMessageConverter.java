package com.beeasy.mscommon.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONPObject;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.support.spring.FastJsonContainer;
import com.alibaba.fastjson.support.spring.MappingFastJsonValue;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import org.osgl.$;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class FJHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements GenericHttpMessageConverter<Object> {
    public static final MediaType APPLICATION_JAVASCRIPT = new MediaType("application", "javascript");
    private Charset charset = Charset.forName("UTF-8");
    /** @deprecated */
    @Deprecated
    protected SerializerFeature[] features = new SerializerFeature[0];
    /** @deprecated */
    @Deprecated
    protected SerializeFilter[] filters = new SerializeFilter[0];
    /** @deprecated */
    @Deprecated
    protected String dateFormat;
    private FastJsonConfig fastJsonConfig = new FastJsonConfig();

    public FastJsonConfig getFastJsonConfig() {
        return this.fastJsonConfig;
    }

    public void setFastJsonConfig(FastJsonConfig fastJsonConfig) {
        this.fastJsonConfig = fastJsonConfig;
    }

    public FJHttpMessageConverter() {
        super(MediaType.ALL);
    }

    /** @deprecated */
    @Deprecated
    public Charset getCharset() {
        return this.fastJsonConfig.getCharset();
    }

    /** @deprecated */
    @Deprecated
    public void setCharset(Charset charset) {
        this.fastJsonConfig.setCharset(charset);
    }

    /** @deprecated */
    @Deprecated
    public String getDateFormat() {
        return this.fastJsonConfig.getDateFormat();
    }

    /** @deprecated */
    @Deprecated
    public void setDateFormat(String dateFormat) {
        this.fastJsonConfig.setDateFormat(dateFormat);
    }

    /** @deprecated */
    @Deprecated
    public SerializerFeature[] getFeatures() {
        return this.fastJsonConfig.getSerializerFeatures();
    }

    /** @deprecated */
    @Deprecated
    public void setFeatures(SerializerFeature... features) {
        this.fastJsonConfig.setSerializerFeatures(features);
    }

    /** @deprecated */
    @Deprecated
    public SerializeFilter[] getFilters() {
        return this.fastJsonConfig.getSerializeFilters();
    }

    /** @deprecated */
    @Deprecated
    public void setFilters(SerializeFilter... filters) {
        this.fastJsonConfig.setSerializeFilters(filters);
    }

    /** @deprecated */
    @Deprecated
    public void addSerializeFilter(SerializeFilter filter) {
        if (filter != null) {
            int length = this.fastJsonConfig.getSerializeFilters().length;
            SerializeFilter[] filters = new SerializeFilter[length + 1];
            System.arraycopy(this.fastJsonConfig.getSerializeFilters(), 0, filters, 0, length);
            filters[filters.length - 1] = filter;
            this.fastJsonConfig.setSerializeFilters(filters);
        }
    }

    protected boolean supports(Class<?> clazz) {
        return true;
    }

    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return super.canRead(contextClass, mediaType);
    }

    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return super.canWrite(clazz, mediaType);
    }

    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return this.readType(this.getType(type, contextClass), inputMessage);
    }

    public void write(Object o, Type type, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        super.write(o, contentType, outputMessage);
    }

    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return this.readType(this.getType(clazz, (Class)null), inputMessage);
    }

    private Object readType(Type type, HttpInputMessage inputMessage) throws IOException {
        try {
            InputStream in = inputMessage.getBody();
            return JSON.parseObject(in, this.fastJsonConfig.getCharset(), type, this.fastJsonConfig.getFeatures());
        } catch (JSONException var4) {
            throw new HttpMessageNotReadableException("JSON parse error: " + var4.getMessage(), var4);
        } catch (IOException var5) {
            throw new HttpMessageNotReadableException("I/O error while reading input message", var5);
        }
    }


    public String getJsonpParam(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String[] jsonps = {"callback","jsonp"};
        String jsonpValue = null;
        for (String jsonp : jsonps) {
            jsonpValue = request.getParameter(jsonp);
            if($.isNotNull(jsonpValue)){
                break;
            }
        }
        return jsonpValue;
    }
    public boolean methodIs(String method){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getMethod().equalsIgnoreCase(method);
    }


    public void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        ByteArrayOutputStream outnew = new ByteArrayOutputStream();

        try {
            HttpHeaders headers = outputMessage.getHeaders();
            SerializeFilter[] globalFilters = this.fastJsonConfig.getSerializeFilters();
            List<SerializeFilter> allFilters = new ArrayList(Arrays.asList(globalFilters));
            boolean isJsonp = false;

            String jsonpValue = getJsonpParam();

            Object value = this.strangeCodeForJackson(object);
            if (value instanceof FastJsonContainer) {
                FastJsonContainer fastJsonContainer = (FastJsonContainer)value;
                PropertyPreFilters filters = fastJsonContainer.getFilters();
                allFilters.addAll(filters.getFilters());
                value = fastJsonContainer.getValue();
            }

            if (value instanceof MappingFastJsonValue) {
                if (!StringUtils.isEmpty(((MappingFastJsonValue)value).getJsonpFunction())) {
                    isJsonp = true;
                }
            } else if (value instanceof JSONPObject) {
                isJsonp = true;
            }


            int len = 0;
            if($.isNotNull(jsonpValue)){
                byte[] bytes = null;
                bytes = (jsonpValue + "(").getBytes();
                outnew.write(bytes);
                len += bytes.length;
            }

            if(value instanceof String){
                outnew.write(((String) value).getBytes());
                len += ((String) value).getBytes().length;
            }
            else{
                len += JSON.writeJSONString(outnew, this.fastJsonConfig.getCharset(), value, this.fastJsonConfig.getSerializeConfig(), (SerializeFilter[])allFilters.toArray(new SerializeFilter[allFilters.size()]), this.fastJsonConfig.getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE, this.fastJsonConfig.getSerializerFeatures());
            }
            if($.isNotNull(jsonpValue)){
                byte[] bytes = null;
                bytes = (");").getBytes();
                outnew.write(bytes);
                len += bytes.length;
                //set jsonp header
                headers.setContentType(MediaType.TEXT_HTML);
            }

//            if (isJsonp) {
//                headers.setContentType(APPLICATION_JAVASCRIPT);
//            }

            if (this.fastJsonConfig.isWriteContentLength()) {
                headers.setContentLength((long)len);
            }

            outnew.writeTo(outputMessage.getBody());

        } catch (JSONException var14) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + var14.getMessage(), var14);
        } finally {
            outnew.close();
        }

    }

    private Object strangeCodeForJackson(Object obj) {
        if (obj != null) {
            String className = obj.getClass().getName();
            if ("com.fasterxml.jackson.databind.node.ObjectNode".equals(className)) {
                return obj.toString();
            }
        }

        return obj;
    }

    protected Type getType(Type type, Class<?> contextClass) {
        return Spring4TypeResolvableHelper.isSupport() ? Spring4TypeResolvableHelper.getType(type, contextClass) : type;
    }

    private static class Spring4TypeResolvableHelper {
        private static boolean hasClazzResolvableType;

        private Spring4TypeResolvableHelper() {
        }

        private static boolean isSupport() {
            return hasClazzResolvableType;
        }

        private static Type getType(Type type, Class<?> contextClass) {
            if (contextClass != null) {
                ResolvableType resolvedType = ResolvableType.forType(type);
                if (type instanceof TypeVariable) {
                    ResolvableType resolvedTypeVariable = resolveVariable((TypeVariable)type, ResolvableType.forClass(contextClass));
                    if (resolvedTypeVariable != ResolvableType.NONE) {
                        return resolvedTypeVariable.resolve();
                    }
                } else if (type instanceof ParameterizedType && resolvedType.hasUnresolvableGenerics()) {
                    ParameterizedType parameterizedType = (ParameterizedType)type;
                    Class<?>[] generics = new Class[parameterizedType.getActualTypeArguments().length];
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();

                    for(int i = 0; i < typeArguments.length; ++i) {
                        Type typeArgument = typeArguments[i];
                        if (typeArgument instanceof TypeVariable) {
                            ResolvableType resolvedTypeArgument = resolveVariable((TypeVariable)typeArgument, ResolvableType.forClass(contextClass));
                            if (resolvedTypeArgument != ResolvableType.NONE) {
                                generics[i] = resolvedTypeArgument.resolve();
                            } else {
                                generics[i] = ResolvableType.forType(typeArgument).resolve();
                            }
                        } else {
                            generics[i] = ResolvableType.forType(typeArgument).resolve();
                        }
                    }

                    return ResolvableType.forClassWithGenerics(resolvedType.getRawClass(), generics).getType();
                }
            }

            return type;
        }

        private static ResolvableType resolveVariable(TypeVariable<?> typeVariable, ResolvableType contextType) {
            ResolvableType resolvedType;
            if (contextType.hasGenerics()) {
                resolvedType = ResolvableType.forType(typeVariable, contextType);
                if (resolvedType.resolve() != null) {
                    return resolvedType;
                }
            }

            ResolvableType superType = contextType.getSuperType();
            if (superType != ResolvableType.NONE) {
                resolvedType = resolveVariable(typeVariable, superType);
                if (resolvedType.resolve() != null) {
                    return resolvedType;
                }
            }

            ResolvableType[] var4 = contextType.getInterfaces();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                ResolvableType ifc = var4[var6];
                resolvedType = resolveVariable(typeVariable, ifc);
                if (resolvedType.resolve() != null) {
                    return resolvedType;
                }
            }

            return ResolvableType.NONE;
        }

        static {
            try {
                Class.forName("org.springframework.core.ResolvableType");
                hasClazzResolvableType = true;
            } catch (ClassNotFoundException var1) {
                hasClazzResolvableType = false;
            }

        }
    }
}

