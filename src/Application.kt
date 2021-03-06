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
import io.ktor.response.respondText
import io.ktor.routing.delete
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
        exception<Throwable> { cause ->
            log.error("wut", cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/recipes") {
            call.respond(Recipes.findAll())
        }

        get("/recipes/{id}") {
            val id = call.parameters["id"]!!
            call.respond(Recipes.findById(id))
        }

        delete("/cache") {
            Cache.clear()
            call.respondText("Cache cleared")
        }
    }
}
