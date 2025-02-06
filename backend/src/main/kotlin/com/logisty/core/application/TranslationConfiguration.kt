package com.logisty.core.application

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource

@Configuration
class TranslationConfiguration {
    @Bean
    fun messageSource(): MessageSource =
        ResourceBundleMessageSource().apply {
            setBasenames("messages")
            setDefaultEncoding("UTF-8")
        }
}
