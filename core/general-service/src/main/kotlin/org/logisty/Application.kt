package org.logisty

import io.ktor.server.application.*
import org.logisty.infrastructure.Database

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    Database.migrate(environment)
}
