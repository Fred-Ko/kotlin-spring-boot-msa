package com.restaurant.common.presentation.config

import com.restaurant.common.presentation.filter.CorrelationIdFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

/**
 * 웹 관련 설정을 담당하는 설정 클래스
 */
@Configuration
class WebConfig {
    /**
     * CorrelationIdFilter를 FilterRegistrationBean으로 등록
     * 필터 체인의 가장 앞부분에 위치하도록 높은 우선순위 부여
     */
    @Bean
    fun correlationIdFilter(): FilterRegistrationBean<CorrelationIdFilter> {
        val registrationBean = FilterRegistrationBean<CorrelationIdFilter>()
        registrationBean.filter = CorrelationIdFilter()
        registrationBean.addUrlPatterns("/*")
        registrationBean.order = Ordered.HIGHEST_PRECEDENCE
        return registrationBean
    }
}
