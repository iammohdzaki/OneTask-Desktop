package com.one.task.di

import com.one.task.data.DriverFactory
import com.one.task.data.TaskRepository
import com.one.task.data.createDatabase
import com.one.task.data.createDataStore
import com.one.task.presentation.mvi.AppViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val sharedModule = module {
    single { createDataStore() }
    single { createDatabase(get()) }
    single { TaskRepository(get()) }
    factory { AppViewModel(get()) }
}

fun initKoin(driverFactory: DriverFactory) {
    startKoin {
        modules(
            module {
                single { driverFactory }
            },
            sharedModule
        )
    }
}
