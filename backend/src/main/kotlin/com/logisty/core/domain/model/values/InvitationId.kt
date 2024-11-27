package com.logisty.core.domain.model.values

import java.util.UUID

@JvmInline
value class InvitationId(
    val value: UUID,
) {
    companion object {
        fun generate(): InvitationId = InvitationId(UUID.randomUUID())
    }
}
