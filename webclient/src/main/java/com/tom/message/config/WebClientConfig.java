package com.tom.message.config;

import com.tom.message.interceptor.TraceIdExchangeFilterFunction;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // 创建连接池配置
        ConnectionProvider provider = ConnectionProvider.builder("message-notification-pool")
                .maxConnections(500)                // 最大连接数
                .pendingAcquireTimeout(Duration.ofSeconds(60)) // 等待获取连接的超时时间
                .pendingAcquireMaxCount(100)         // 最大等待获取连接数
                .maxIdleTime(Duration.ofSeconds(20))  // 连接最大空闲时间
                .maxLifeTime(Duration.ofMinutes(30))  // 连接最大生命周期
                .build();

        // 配置HttpClient
        HttpClient httpClient = HttpClient.create(provider)
                // 连接超时
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                // 响应超时
                .responseTimeout(Duration.ofSeconds(30))
                // 读写超时
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)))
                // 开启压缩
                .compress(true)
                // 开启keep-alive
                .keepAlive(true);

        // 配置ExchangeStrategies（用于限制内存大小）
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .filter(new TraceIdExchangeFilterFunction())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
