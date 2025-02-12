package com.logisty.core.adapter

import com.logisty.core.application.MutableClock
import com.logisty.core.application.persistence.event.Events
import com.logisty.core.application.persistence.tables.Fleets
import com.logisty.core.application.persistence.tables.Invitations
import com.logisty.core.application.persistence.tables.OrderRoutes
import com.logisty.core.application.persistence.tables.OrderSteps
import com.logisty.core.application.persistence.tables.Orders
import com.logisty.core.application.persistence.tables.Users
import com.logisty.core.domain.Fixtures
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Clock
import java.time.Instant

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
open class FunctionalTest {
    val fixtures = Fixtures()

    @Autowired
    lateinit var clock: Clock

    @Autowired
    lateinit var mockMvc: MockMvc

    lateinit var routes: FunctionalHttpTemplate

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres =
            PostgreSQLContainer(
                DockerImageName
                    .parse("postgis/postgis:15-3.5-alpine")
                    .asCompatibleSubstituteFor("postgres"),
            ).withReuse(true)
    }

    @BeforeEach
    fun prepareSystem() {
        (clock as MutableClock).resetTo(Instant.now())
        routes = FunctionalHttpTemplate(mockMvc, fixtures)
    }

    @BeforeEach
    fun prepareDatabase() {
        transaction {
            SchemaUtils.drop(Events, Fleets, Invitations, Users, Orders, OrderSteps, OrderRoutes)
            SchemaUtils.create(Events, Fleets, Invitations, Users, Orders, OrderSteps, OrderRoutes)

            fixtures.createFleet()
            fixtures.createInvitations()
            fixtures.createUsers()
        }
    }
}
