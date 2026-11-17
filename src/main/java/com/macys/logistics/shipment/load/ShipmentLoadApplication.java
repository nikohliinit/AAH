package com.macys.logistics.shipment.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.config.LogInterceptor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Configuration
@EnableRetry
@Log4j2
public class ShipmentLoadApplication {

    @Bean
    public ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(apiInfo());
    }

    @Value("${tms.service.connectionTimeOut}")
    private int connectionTimeOut;

    @Value("${tms.service.readTimeOut}")
    private int readTimeOut;


    private Info apiInfo() {
		return new Info().title("TMS Shipment Load Integration API ");
	}

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = null;
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(getClientHttpRequestFactory());
        restTemplate = new RestTemplate(factory);
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new LogInterceptor());
        restTemplate.setInterceptors(interceptors);

        return  restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectionRequestTimeout(connectionTimeOut);
        clientHttpRequestFactory.setConnectTimeout(readTimeOut);
        return clientHttpRequestFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(ShipmentLoadApplication.class, args);
    }

}
