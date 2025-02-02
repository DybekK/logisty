package com.logisty.core.application

import com.fasterxml.jackson.databind.json.JsonMapper

val mapper = JsonMapper.builder()
    .findAndAddModules()
    .build()
