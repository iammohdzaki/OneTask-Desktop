package com.one.task.domain

import org.slf4j.LoggerFactory

actual object Logger {
    private val logger = LoggerFactory.getLogger("OneTask")

    actual fun d(tag: String, message: String) {
        logger.info("[$tag] [DEBUG] $message")
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            logger.error("[$tag] $message", throwable)
        } else {
            logger.error("[$tag] $message")
        }
    }

    actual fun i(tag: String, message: String) {
        logger.info("[$tag] $message")
    }
}
