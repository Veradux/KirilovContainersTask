package com.bhtech.kirilovcontainerstask

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bhtech.kirilovcontainerstask.databinding.MainActivityBinding
import com.bhtech.kirilovcontainerstask.ui.main.login.LoginFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
        }
    }
}