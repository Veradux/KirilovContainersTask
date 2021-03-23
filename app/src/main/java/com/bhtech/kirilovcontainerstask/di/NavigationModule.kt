package com.bhtech.kirilovcontainerstask.di

import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class NavigationModule {

    @Binds
    abstract fun bindNavigator(impl: ScreenNavigatorImpl): ScreenNavigator
}
