package com.bhtech.kirilovcontainerstask.screennavigator

import androidx.fragment.app.FragmentActivity
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator.Screen
import com.bhtech.kirilovcontainerstask.ui.bluetoothdetection.BluetoothDetectionFragment
import com.bhtech.kirilovcontainerstask.ui.cartography.CartographyFragment
import com.bhtech.kirilovcontainerstask.ui.containersmenu.ContainersMenuFragment
import com.bhtech.kirilovcontainerstask.ui.editcontainer.EditContainerFragment
import com.bhtech.kirilovcontainerstask.ui.login.LoginFragment
import com.bhtech.kirilovcontainerstask.ui.mainmenu.MainMenuFragment
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
        Screen.BLUETOOTH -> BluetoothDetectionFragment()
        Screen.CONTAINERS -> ContainersMenuFragment()
        Screen.MAP -> CartographyFragment()
        Screen.EDIT_CONTAINER -> EditContainerFragment()
    }
}