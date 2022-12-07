package com.compete

import com.compete.DataBase.SqliteServer
import com.compete.common.CommonModule
import com.compete.plugins.chatServer
import com.compete.plugins.configureHTTP
import com.compete.plugins.configureRouting
import com.compete.plugins.configureSockets
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import java.text.DateFormat
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as clientNegotiation

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module,
    ).start(wait = true)
}

val httpClientModule = module {
    single {
        // 数据库对象
        SqliteServer()
    }

    single {
        HttpClient(OkHttp) {
            install(clientNegotiation) {
                gson {
                    setPrettyPrinting()
                    setDateFormat(DateFormat.LONG)
                }
            }
        }
    }
}

fun Application.module() {
    startKoin {
        slf4jLogger()
        modules(httpClientModule)
        modules(chatServer)
        modules(CommonModule.getAllModules())
    }

    //提前初始化数据库
    val sqliteServer by inject<SqliteServer>()
    sqliteServer.init()



    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    configureHTTP()
    configureSockets()
    configureRouting()
}


