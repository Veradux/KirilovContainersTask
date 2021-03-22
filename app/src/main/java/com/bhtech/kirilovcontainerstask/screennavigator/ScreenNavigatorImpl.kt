package com.bhtech.kirilovcontainerstask.screennavigator

import androidx.fragment.app.FragmentActivity
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.ui.login.LoginFragment
import javax.inject.Inject

class ScreenNavigatorImpl @Inject constructor(private val activity: FragmentActivity) : ScreenNavigator {

    override fun navigateTo(screen: Screen) {
        val requestedFragment = when (screen) {
            Screen.LOGIN -> LoginFragment()
        }

        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.container, requestedFragment)
            .addToBackStack(requestedFragment::class.java.canonicalName)
            .commit()
    }
}