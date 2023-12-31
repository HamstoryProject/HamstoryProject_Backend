package com.codingrecipe.config;

import com.codingrecipe.jwt.BearerAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final BearerAuthInterceptor bearerAuthInterceptor;

    public WebMvcConfig(BearerAuthInterceptor bearerAuthInterceptor){
        this.bearerAuthInterceptor = bearerAuthInterceptor;
    }

    /*
    // 특정 경로로 들어오면 컨트롤러보다 인터셉터가 먼저 실행되게 함
    public void addInterceptors(InterceptorRegistry registry){
        System.out.println(">>> Interceptor added");
        //registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/**");
    }
     */

    //프론트엔드에서 백엔드로 접속할 수 있게 해줌
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**").allowedOrigins("http://localhost:3000");
    }
}
