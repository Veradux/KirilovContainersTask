package com.bhtech.kirilovcontainerstask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bhtech.kirilovcontainerstask.ui.main.login.LoginFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, LoginFragment.newInstance())
                    .commitNow()
        }
    }
}