package me.alexk

import dev.evo.prometheus.ktor.metricsModule

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.TextContent
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
)

@Serializable
data class Hello(
    val message: String = "Hello",
    val user: User,
)

fun main() {
    val logger = mu.KotlinLogging.logger {}

    val host = "localhost"
    val port = "8080"

    logger.info("Starting ktor application at http://$host:$port")

    embeddedServer(Netty, host = "localhost", port = 8080) {
        install(StatusPages) {
            exception<Throwable> { cause ->
                call.respond(
                    HttpStatusCode.InternalServerError,
                    TextContent(cause.stackTraceToString(), ContentType.Text.Plain)
                )
            }
        }

        install(ContentNegotiation) {
            json()
        }

        metricsModule()

        routing {
            ourRoutes()
        }
    }.start(wait = true)
}

fun Route.ourRoutes() {
    get("/") {
        val hello = Hello(
            user = User("world")
        )
        call.respond(hello)
    }
}