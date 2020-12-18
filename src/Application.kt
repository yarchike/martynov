package com.martynov

import com.martynov.route.RoutingV1
import com.martynov.service.FileService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.with
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import javax.naming.ConfigurationException

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }
    install(KodeinFeature) {
        constant(tag = "foto-dir") with (
                environment.config.propertyOrNull("ph.upload.dir")?.getString()
                    ?: throw ConfigurationException("Upload dir is not specified")
                )
        bind<FileService>() with eagerSingleton {
            FileService(
                instance(tag = "foto-dir")
            )
        }

        bind<RoutingV1>() with eagerSingleton {
            RoutingV1(instance(tag = "foto-dir"),
            instance())
        }


    }
    install(Routing) {
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }


}

