package com.efficiency.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author : Vincent.jiao
 * @Date : 2021/8/3 21:47
 * @Version : 1.0
 */
@Configuration
public class SpringMVCConfig implements WebMvcConfigurer{
        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
            converters.add(new MappingJackson2HttpMessageConverter());
        }

}