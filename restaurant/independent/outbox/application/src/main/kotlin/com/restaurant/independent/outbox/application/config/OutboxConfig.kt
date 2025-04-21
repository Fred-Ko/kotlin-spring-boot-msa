package com.restaurant.independent.outbox.application.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Outbox 모듈의 설정을 제공하는 설정 클래스.
 */
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "outbox.polling")
class OutboxConfig {
    /**
     * 메시지 처리 배치 크기
     */
    var batchSize: Int = 100

    /**
     * 최대 재시도 횟수
     */
    var maxRetries: Int = 3

    /**
     * 대기 중인 메시지 폴링 간격 (밀리초)
     */
    var pendingMessagesInterval: Long = 5000

    /**
     * 실패한 메시지 폴링 간격 (밀리초)
     */
    var failedMessagesInterval: Long = 60000
}
