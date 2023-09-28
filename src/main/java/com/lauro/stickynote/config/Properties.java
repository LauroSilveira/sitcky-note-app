package com.lauro.stickynote.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "resource.server")
@Data
public class Properties {

    public String url;
}
