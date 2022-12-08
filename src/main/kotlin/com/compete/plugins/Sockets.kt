package com.compete.plugins

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set


data class UserInfo(
    var token: String?,
    var user: String? = null,
    var pass: String? = null,
) {
    override fun toString(): String {
        return "id is $token"
    }

    fun isNon(): Boolean {
        return token.isNullOrBlank() and (user.isNullOrBlank() or pass.isNullOrBlank())
    }
}

class ChatServer {
    val userList = ConcurrentHashMap<WebSocketSession, UserInfo>()
    val roomList = ConcurrentHashMap<Int, MutableList<UserInfo>>()
    val lastMessage = LinkedList<String>()


}


// 在Application中注册模块注入依赖
val chatServer = module {
    single {
        ChatServer()
    }
}

inline val WebSocketSession.ctServer: ChatServer
    get() {
        val ct: ChatServer by inject(ChatServer::class.java)
        return ct
    }

suspend inline fun <T> WebSocketSession.sendJson(data: T) = outgoing.send(Frame.Text(Gson().toJson(data)))


open class BaseMsgBody {
    var code: Int = -1
}


open class GenericBody<T> : BaseMsgBody() {
    var msg: T? = null
}

data class BingImageList(
    val url: String,
    val title: String,
)


data class BingImage(
    val images: List<BingImageList>,
)

object OPCODE {
    const val ERROR_DATA = 8001
    const val INIT_CONNECT = 1000
    const val USER_REG = 1001
    const val USER_LOGIN = 1002

    /**
     * 客户端需要上传自己的信息
     */
    const val USER_NEED_UPLOAD_STATE = 1003

    /**
     * 客户端回报用户注册成功 并返回token
     */
    const val OP_REG_USER_SUCCESS = 1004

    /**
     * 客户端登录成功
     */
    const val OP_LOGIN_USER_SUCCESS = 1005

    /**
     * 强制客户端跳转登录页面
     */
    const val FORCE_LOGIN = 1006

    //用户不存在
    const val USER_NOT_EXISTS = 1007
    const val USER_PASSWORD_ERROR = 1008
    const val USER_ADMIN_ACCESS = 1009

    const val GET_LOGIN_PARAMS = 1010
    const val TOKEN_VALID = 1011
    const val TOKEN_ERROR = 1012
}

suspend fun WebSocketSession.parseIncomingMsg(user: UserInfo, text: String) {
    val gs = Gson()
    val msg = try {
        gs.fromJson(text, GenericBody::class.java)
    } catch (e: Exception) {
        null
    } ?: return sendJson(GenericBody<String>().apply {
        code = OPCODE.ERROR_DATA
        msg = "数据错误。"
    })

    when (msg.code) {
        OPCODE.INIT_CONNECT -> {
            val data = (msg.msg!! as LinkedTreeMap<*, *>)
            val tk = data["token"].toString()
            if (tk.isNotEmpty() or (tk == "null")) {
                if ((tk == "null") or ctServer.userList.filter {
                        it.value.token == tk
                    }.toList().isEmpty()) {
                    return sendJson(GenericBody<String>().apply {
                        code = OPCODE.FORCE_LOGIN
                        this.msg = "用户token失效 重新登录"
                    })
                }
            }

            // 询问是否注册了数据
            sendJson(GenericBody<String>().apply {
                code = OPCODE.USER_NEED_UPLOAD_STATE
                this.msg = "上传客户端信息"
            })
            return
        }

        OPCODE.USER_REG -> {
            sendJson(GenericBody<UserInfo>().apply {
                code = OPCODE.OP_REG_USER_SUCCESS
                this.msg = user
            })
            ctServer.userList[this] = user
            return
        }

        OPCODE.USER_LOGIN -> {
            val data = (msg.msg!! as LinkedTreeMap<*, *>)
            user.user = data["user"]?.toString()
            user.pass = data["pass"]?.toString()
            user.token = data["token"]?.toString() ?: user.token


            if (user.isNon()) {
                return sendJson(GenericBody<String>().apply {
                    code = OPCODE.ERROR_DATA
                    this.msg = "数据错误。"
                })
            } else if (!user.user.isNullOrEmpty()) {
                //管理员登录
                return sendJson(GenericBody<String>().apply {
                    code = OPCODE.USER_NOT_EXISTS
                    this.msg = "USER_NOT_EXISTS。"
                })
            }

            val has = ctServer.userList.filter {
//                println("${it.value.token} ${user.token}")
                if (user.user != null) it.value.user == user.user else it.value.token == user.token
            }.toList()
            if (has.isNotEmpty()) {
                val cli = has[0].first
                ctServer.userList[cli]?.token = null
                cli.close(CloseReason(code = 1, message = "掉线啦"))
            }
            ctServer.userList[this] = user
            sendJson(GenericBody<String>().apply {
                code = OPCODE.OP_LOGIN_USER_SUCCESS
                this.msg = user.token
            })
            return
        }

        else -> {
            sendJson(GenericBody<String>().apply {
                code = OPCODE.ERROR_DATA
                this.msg = "数据错误"
            })
            return
        }
    }
}


fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    runBlocking {
        //初始化数据库数据
    }

    routing {
        webSocket("/ws") {
            val user = UserInfo(UUID.randomUUID().toString().replace("-", ""))
            println("user join $user")
            // app 连接后首先标识注册 如果没有token则是新设备 下发token并注册在案
            try {
                incoming.consumeEach { frame ->
                    when (frame) {
                        is Frame.Close -> {
                            println("user disconnect!")
                        }

                        is Frame.Binary, is Frame.Text -> {
                            val text = if (frame is Frame.Text) frame.readText() else frame.readBytes()
                                .toString(Charsets.UTF_8)
                            println(text)
                            if (text == "iamlive") {
                                outgoing.send(Frame.Ping("pong".toByteArray()))
                                return@consumeEach
                            }
                            parseIncomingMsg(user, text)
                        }

                        is Frame.Ping -> {
                            println("rev ping,send pong")
                            outgoing.send(Frame.Pong("pong".toByteArray()))
                        }

                        else -> {
                            println(frame.frameType)
                        }
                    }
                }
            } finally {
                ctServer.userList.remove(this)
                println("member left")
            }
        }
    }
}



