package com.one.task.data

import app.cash.sqldelight.db.SqlDriver
import com.one.task.data.db.AppDatabase

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): AppDatabase {
    return AppDatabase(driverFactory.createDriver())
}
