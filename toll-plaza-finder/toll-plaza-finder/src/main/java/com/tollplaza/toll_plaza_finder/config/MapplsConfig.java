
package com.tollplaza.toll_plaza_finder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MapplsConfig {

    @Bean
    public RestClient mapplsRestClient() {
        return RestClient.builder().build();
    }
}
