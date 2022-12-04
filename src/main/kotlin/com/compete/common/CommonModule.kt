package com.compete.common

import org.koin.core.module.Module

object CommonModule {

    fun getAllModules(): List<Module> {
        val lst: List<Module> = arrayListOf(
            userService
        )
        return lst
    }

    val jwtAudience = "http://0.0.0.0:8080/hello"
    val jwtIssuer = "http://0.0.0.0:8080/"
    val jwtSecret = "secret"
}