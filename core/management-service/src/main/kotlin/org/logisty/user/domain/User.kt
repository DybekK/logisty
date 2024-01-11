package org.logisty.user.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    var id: UUID,
    var name: String,
    var email: String,
    var password: String
)
