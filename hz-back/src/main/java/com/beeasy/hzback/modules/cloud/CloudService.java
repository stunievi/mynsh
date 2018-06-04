package com.beeasy.hzback.modules.cloud;

import org.springframework.stereotype.Service;

@Service
public class CloudService {
    public CloudApi cloudApi;

//    public CloudService(Decoder decoder, Encoder encoder, Client client){
//        this.cloudApi = Feign.builder().client(client)
//                .encoder(encoder)
//                .decoder(new Decoder() {
//                    @Override
//                    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
//                        return null;
//                    }
//                })
////                .requestInterceptor(new BasicAuthRequestInterceptor("user","password"))
//                .target(CloudApi.class,"http://192.168.31.55");
//    }

}
