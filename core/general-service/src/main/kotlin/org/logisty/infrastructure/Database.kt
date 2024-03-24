package org.logisty.infrastructure

import io.ktor.server.application.*
import org.flywaydb.core.Flyway

object Database {
    data class PostgresConfig(val url: String, val user: String, val password: String) {
        companion object {
            fun fromEnv(environment: ApplicationEnvironment): PostgresConfig {
                val url = environment.config.property("postgres.url").getString()
                val user = environment.config.property("postgres.user").getString()
                val password = environment.config.property("postgres.password").getString()
                return PostgresConfig(url, user, password)
            }
        }
    }

    fun migrate(environment: ApplicationEnvironment) {
        val (url, user, password) = PostgresConfig.fromEnv(environment)

        val flyway = Flyway.configure().dataSource(url, user, password).load()
        flyway.migrate()
    }
}