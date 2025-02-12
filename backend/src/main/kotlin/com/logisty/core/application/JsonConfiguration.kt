package com.logisty.core.application

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.cfg.MapperBuilder.findModules
import com.fasterxml.jackson.databind.json.JsonMapper
import org.postgis.geojson.PostGISModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

val mapper =
    JsonMapper
        .builder()
        .addModules(findModules() + PostGISModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .build()

@Configuration
class JsonConfiguration {
    @Bean
    @Primary
    fun jsonMapper() = mapper
}
