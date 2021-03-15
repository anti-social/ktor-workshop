package me.alexk

import dev.evo.elasticmagic.Document
import dev.evo.elasticmagic.ElasticsearchCluster
import dev.evo.elasticmagic.ElasticsearchIndex
import dev.evo.elasticmagic.ElasticsearchVersion
import dev.evo.elasticmagic.SearchQuery
import dev.evo.elasticmagic.compile.CompilerProvider
import dev.evo.elasticmagic.serde.json.JsonDeserializer
import dev.evo.elasticmagic.serde.json.JsonSerializer
import dev.evo.elasticmagic.transport.ElasticsearchKtorTransport

import dev.evo.prometheus.ktor.metricsModule

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.engine.cio.CIO
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

object ProductDoc : Document() {
    val rank by float()
}

fun Route.ourRoutes() {
    val logger = mu.KotlinLogging.logger {}

    val esTransport = ElasticsearchKtorTransport(
        "http://es6-stg-prom-lb.prom.dev-cloud.evo.:9200",
        CIO.create {}
    )
    val compilers = CompilerProvider(
        ElasticsearchVersion(6, 0, 0),
        JsonSerializer,
        JsonDeserializer
    )
    val cluster = ElasticsearchCluster(esTransport, compilers)
    val index = cluster["ua_trunk_catalog"]

    get("/") {
        val hello = Hello(
            user = User("world")
        )
        call.respond(hello)
    }

    get("/es") {
        val query = SearchQuery {
            functionScore(
                query = null,
                functions = listOf(
                    fieldValueFactor(
                        ProductDoc.rank,
                    )
                )
            )
        }
        logger.info("${compilers.searchQuery.compile(compilers.serializer, query).body}")
    }
}