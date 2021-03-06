package com.bhtech.kirilovcontainerstask.ui

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator.Screen
import com.bhtech.kirilovcontainerstask.ui.bluetoothdetection.BluetoothDetectionFragment
import com.bhtech.kirilovcontainerstask.ui.cartography.CartographyFragment
import com.bhtech.kirilovcontainerstask.ui.containersmenu.ContainersMenuFragment
import com.bhtech.kirilovcontainerstask.ui.editcontainer.EditContainerFragment
import com.bhtech.kirilovcontainerstask.ui.login.LoginFragment
import com.bhtech.kirilovcontainerstask.ui.mainmenu.MainMenuFragment
import com.vmadalin.easypermissions.EasyPermissions
import dagger.hilt.android.AndroidEntryPoint

private const val REQUEST_CODE_FINE_LOCATION_PERMISSION = 1

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ScreenNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            navigateTo(Screen.LOGIN)
        }
    }

    //region Navigation
    /**
     * Used to prevent duplicate fragments from being added to the back stack.
     * If the requested fragment has already been created before, the app is navigated back to it.
     */
    override fun navigateTo(screen: Screen) {
        if (screen == Screen.MAP) {
            handleMapPermsOnNavigation()
        } else {
            openFragment(screen)
        }
    }

    private fun openFragment(screen: Screen) {
        val fragmentName = getCorrespondingFragmentTo(screen)
        val fragment = supportFragmentManager.findFragmentByTag(fragmentName)
        if (fragmentName != null && fragment != null) {
            popBackStackTo(supportFragmentManager, fragmentName)
        } else if (fragmentName != null) {
            attachNewView(supportFragmentManager, fragmentName)
        }
    }

    private fun getCorrespondingFragmentTo(screen: Screen) = when (screen) {
        Screen.LOGIN -> LoginFragment::class.java.canonicalName
        Screen.MAIN -> MainMenuFragment::class.java.canonicalName
        Screen.BLUETOOTH -> BluetoothDetectionFragment::class.java.canonicalName
        Screen.CONTAINERS -> ContainersMenuFragment::class.java.canonicalName
        Screen.MAP -> CartographyFragment::class.java.canonicalName
        Screen.EDIT_CONTAINER -> EditContainerFragment::class.java.canonicalName
    }

    private fun popBackStackTo(fragmentManager: FragmentManager, fragmentClassName: String): Fragment? {
        val fragment = fragmentManager.findFragmentByTag(fragmentClassName) ?: return null

        fragmentManager.popBackStack(fragmentClassName, 0)
        fragmentManager.executePendingTransactions()
        return fragment
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    private fun attachNewView(fragmentManager: FragmentManager, viewClassName: String): Fragment {
        val newFragment = fragmentManager.fragmentFactory.instantiate(classLoader, viewClassName)

        fragmentManager.beginTransaction()
            .replace(R.id.container, newFragment, viewClassName)
            .addToBackStack(viewClassName).commit()

        fragmentManager.executePendingTransactions()
        return newFragment
    }
    //endregion

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_FINE_LOCATION_PERMISSION && grantResults.contains(PERMISSION_GRANTED)) {
            openFragment(Screen.MAP)
        }
    }

    private fun handleMapPermsOnNavigation() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(
                host = this,
                rationale = getString(R.string.permission_fine_location_rationale_message),
                requestCode = REQUEST_CODE_FINE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            openFragment(Screen.MAP)
        }
    }
}

/**
 * This can be in a BaseFragment class, which all fragments could implement,
 * though for an application of this small size it was deemed unnecessary.
 */
fun Fragment.navigateTo(screen: Screen) {
    if (activity is MainActivity) {
        (activity as MainActivity).navigateTo(screen)
    }
}