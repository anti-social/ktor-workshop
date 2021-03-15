package me.alexk

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val logger = mu.KotlinLogging.logger {}

    val host = "localhost"
    val port = "8080"

    logger.info("Starting ktor application at http://$host:$port")

    embeddedServer(Netty, host = "localhost", port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello world!")
            }
        }
    }.start(wait = true)
}
