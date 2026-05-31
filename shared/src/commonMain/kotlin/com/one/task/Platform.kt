package com.one.task

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform