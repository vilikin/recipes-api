package `in`.vilik

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(Compression) {
        gzip()
    }

    install(CallLogging) {
        level = Level.INFO
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Head)
        method(HttpMethod.Get)
        anyHost()
    }

    install(ContentNegotiation) {
        gson()
    }

    install(StatusPages) {
        exception<Exception> { cause ->
            log.error("wut", cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/hello") {
            val contents = RecipesRepository.getContentsFrom("/metadata")
            call.respond(contents)
        }
    }
}
