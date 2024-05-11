package org.logisty.module.order.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val street: String,
    val city: String,
    val postCode: String
)