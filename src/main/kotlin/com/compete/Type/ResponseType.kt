package com.compete.Type

import kotlinx.serialization.Serializable

class ResponseType {
    @Serializable
    open class BaseResponse(
        var code: Int? = null,
        var msg: String? = null
    )

    @Serializable
    class ResponseImage : BaseResponse() {
        val image: String = ""
    }

    @Serializable
    class LoginResponse(
        var token: String? = null
    ) : BaseResponse()
}

class RequestType {
    @Serializable
    data class LoginRequest(
        val username: String,
        val password: String,
    )
}