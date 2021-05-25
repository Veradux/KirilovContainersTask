package com.bhtech.kirilovcontainerstask.screennavigator

interface ScreenNavigator {

    fun navigateTo(screen: Screen)

    /**
     * Contains all available screen fragments.
     * Used by ScreenNavigator as a way to tell which fragment to place in the main activity.
     */
    enum class Screen {
        LOGIN,
        MAIN,
        BLUETOOTH,
        MAP,
        CONTAINERS,
        EDIT_CONTAINER,
        DAY_NIGHT
    }
}
