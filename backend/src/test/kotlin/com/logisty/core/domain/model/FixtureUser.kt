package com.logisty.core.domain.model

import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.LastName

data class FixtureUser(
    val userId: UserId,
    val firstName: FirstName,
    val lastName: LastName,
    val email: UserEmail,
    val password: UserPassword,
)
