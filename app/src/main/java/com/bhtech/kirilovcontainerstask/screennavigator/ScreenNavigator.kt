package com.bhtech.kirilovcontainerstask.screennavigator

/**
 * Contains all available screen fragments.
 *
 * Used by ScreenNavigator as a way to tell which fragment to place in the main activity.
 */
enum class Screen {
    LOGIN
}

interface ScreenNavigator {

    fun navigateTo(screen: Screen)
}
