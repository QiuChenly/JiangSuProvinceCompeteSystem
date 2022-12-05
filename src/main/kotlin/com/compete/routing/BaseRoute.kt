package com.compete.routing

import com.compete.Type.BaseResponse
import com.compete.plugins.BingImage
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async

/**
 * 基本非授权第三方路由
 */
fun Route.baseRoute() {
    get(
        "/getImg"
    ) {
        val bing = async {
            http.get("https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1").body<BingImage>()
        }.await()
        val img = "https://cn.bing.com${bing.images[0].url}"
        call.respond(BaseResponse().apply {
            code = 0
            msg = img
        })
    }
}
