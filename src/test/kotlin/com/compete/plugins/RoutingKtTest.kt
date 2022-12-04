package com.compete.plugins

import com.compete.module
import io.ktor.server.testing.*
import kotlin.test.Test

class RoutingKtTest {

    @Test
    fun testGetGetimg() = testApplication {
        application {
            module()
        }
//        client.get("/getImg").body<ResponseType.BaseResponse<ResponseType.ResponseImage>>().apply {
//            println(this)
//        }
    }

    @Test
    fun testPostUserLogin() = testApplication {
        application {
            configureRouting()
        }
    }
}