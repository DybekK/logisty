package com.logisty.core.application

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper

val mapper = JsonMapper.builder()
    .findAndAddModules()
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .build()
