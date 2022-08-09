package me.blessedsibanda.license.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "example")
@Getter
@Setter
@NoArgsConstructor
public class ServiceConfig {
    private String property;
}
