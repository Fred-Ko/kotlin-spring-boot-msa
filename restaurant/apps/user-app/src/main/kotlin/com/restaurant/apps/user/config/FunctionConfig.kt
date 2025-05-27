package com.restaurant.apps.user.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import java.util.function.Function

/**
 * 기본 Spring Cloud Stream 및 Kafka 설정
 * StreamBridge를 사용하기 위한 최소한의 구성입니다.
 */
@Configuration
class FunctionConfig {

    /**
     * Kafka 메시지 변환기 빈 설정
     * StreamBridge와 함께 사용할 때 도움이 됩니다.
     */
    @Bean
    fun messageConverter(): RecordMessageConverter {
        return StringJsonMessageConverter()
    }

    /**
     * This function bean is required by the spring.cloud.function.definition
     * in application.yml. It helps with Spring Cloud Function initialization.
     */
    @Bean
    fun functionRouter(): Function<Any, Any> {
        return Function { it }
    }
}
