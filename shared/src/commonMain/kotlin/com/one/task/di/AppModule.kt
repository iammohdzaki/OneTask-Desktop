package com.one.task.di

import com.one.task.data.DriverFactory
import com.one.task.data.TaskRepository
import com.one.task.data.createDatabase
import com.one.task.data.createDataStore
import com.one.task.presentation.mvi.AppViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.one.task.data.SettingsRepository
import com.one.task.data.DefaultSettingsRepository
import com.one.task.presentation.mvi.SettingsViewModel

val sharedModule = module {
    single { createDataStore() }
    single<SettingsRepository> { DefaultSettingsRepository(get()) }
    single(createdAtStart = true) { createDatabase(get()) }
    single(createdAtStart = true) { TaskRepository(get()) }
    factory { AppViewModel(get(), get()) }
    factory { SettingsViewModel(get(), get()) }
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
