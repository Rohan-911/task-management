package com.frontend.category.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.frontend.category.client.CategoryClient;
import com.frontend.category.client.NotificationClient;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    public CategoryClient categoryClient(RestClient restClient) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(CategoryClient.class);
    }

    @Bean
    public NotificationClient notificationClient(RestClient restClient) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(NotificationClient.class);
    }

    @Bean
    public com.frontend.category.client.AuthClient authClient(RestClient restClient) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(com.frontend.category.client.AuthClient.class);
    }
}