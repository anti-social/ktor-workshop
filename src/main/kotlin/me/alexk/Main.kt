package me.alexk

import dev.evo.prometheus.ktor.metricsModule

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.TextContent
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.lang.IllegalArgumentException

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

        metricsModule()

        routing {
            get("/") {
                throw IllegalArgumentException("no argument")
                call.respondText("Hello world!")
            }
        }
    }.start(wait = true)
}
