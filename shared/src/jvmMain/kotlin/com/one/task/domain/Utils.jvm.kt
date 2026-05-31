package com.one.task.domain

import java.util.UUID

actual fun generateUuid(): String = UUID.randomUUID().toString()
actual fun currentTimeMillis(): Long = System.currentTimeMillis()
