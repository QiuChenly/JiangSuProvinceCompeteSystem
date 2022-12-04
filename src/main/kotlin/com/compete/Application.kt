package com.compete

import com.compete.DataBase.SqliteServer
import com.compete.common.CommonModule
import com.compete.plugins.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger

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
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true //解析json的时候忽略掉未映射的值
                })
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


    configureHTTP()
    configureSerialization()
    configureSockets()
    configureRouting()
}


