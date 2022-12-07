package com.compete.routing

import com.compete.Type.BaseResponse
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.fileUploadRoute() {
    authenticate("jwt-auth") {
        post("/common/upload") {
            var fileName = ""
            var fileDescription = ""
            val fileSize = call.request.headers[HttpHeaders.ContentLength]?.toInt() ?: 0
            val multipart = call.receiveMultipart()
            multipart.forEachPart {
                when (it) {
                    is PartData.FormItem -> {
                        fileDescription = it.value
                    }

                    is PartData.FileItem -> {
                        fileName = it.originalFileName.toString()
                        val byte = it.streamProvider().readBytes()
                        File("uploads/$fileName").writeBytes(byte)
                    }

                    else -> {

                    }
                }
                it.dispose()
            }

            //TODO 这里没有处理文件重名的冲突 反正是测试系统 这里就不处理了
            call.respond(
                hashMapOf(
                    "code" to 200,
                    "msg" to "上传成功,文件大小: ${fileSize / 1024}KB.",
                    "file" to "/prod-api/common/file/$fileName"
                )
            )
        }

        get("/common/file/{fileName}") {
            val fileName = call.parameters["fileName"] ?: ""
            val exists = File("uploads/$fileName")
            val file = if (exists.exists()) exists else null
            if (file == null) call.respond(
                BaseResponse(
                    201,
                    "文件不存在。"
                )
            ) else call.respondFile(file)
        }
    }
}