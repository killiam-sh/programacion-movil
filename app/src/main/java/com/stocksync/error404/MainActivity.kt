package com.stocksync.error404

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), LeftFragment.OnOptionSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val leftFragment = supportFragmentManager.findFragmentById(R.id.leftFragmentContainer) as? LeftFragment
        leftFragment?.setOnOptionSelectedListener(this)

        val rightFragment = supportFragmentManager.findFragmentById(R.id.rightFragmentContainer) as? RightFragment
        rightFragment?.showOption("Perfil")
    }

    override fun onOptionSelected(option: String) {
        val rightFragment = supportFragmentManager.findFragmentById(R.id.rightFragmentContainer) as? RightFragment
        rightFragment?.showOption(option)
    }
}