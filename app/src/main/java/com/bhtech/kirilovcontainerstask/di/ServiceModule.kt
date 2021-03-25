package com.bhtech.kirilovcontainerstask.di

import android.content.Context
import com.bhtech.kirilovcontainerstask.service.containers.ContainersService
import com.bhtech.kirilovcontainerstask.service.containers.ContainersServiceMockImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Provides
    @Singleton
    fun getContainersService(@ApplicationContext appContext: Context): ContainersService =
        ContainersServiceMockImpl(appContext)
}