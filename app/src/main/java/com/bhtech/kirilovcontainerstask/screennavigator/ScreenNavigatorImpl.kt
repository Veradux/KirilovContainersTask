package com.bhtech.kirilovcontainerstask.screennavigator

import androidx.fragment.app.FragmentActivity
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator.Screen
import com.bhtech.kirilovcontainerstask.ui.mainmenu.MainMenuFragment
import com.bhtech.kirilovcontainerstask.ui.login.LoginFragment
import javax.inject.Inject

class ScreenNavigatorImpl @Inject constructor(private val activity: FragmentActivity) : ScreenNavigator {

    override fun navigateTo(screen: Screen) {
        val requestedFragment = getCorrespondingFragmentTo(screen)

        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.container, requestedFragment)
            .addToBackStack(requestedFragment::class.java.canonicalName)
            .commit()
    }

    private fun getCorrespondingFragmentTo(screen: Screen) = when (screen) {
        Screen.LOGIN -> LoginFragment()
        Screen.MAIN -> MainMenuFragment()
    }
}