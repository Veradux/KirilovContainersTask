package com.bhtech.kirilovcontainerstask.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.screennavigator.Screen
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var navigator: ScreenNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null) {
            navigator.navigateTo(Screen.LOGIN)
        }
    }
}