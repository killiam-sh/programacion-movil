package com.stocksync.error404

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), LeftFragment.OnOptionSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun setSidebarVisible(visible: Boolean) {
        val leftContainer = findViewById<View>(R.id.leftFragmentContainer)
        leftContainer?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onOptionSelected(option: String) {
        val rightFragment =
            supportFragmentManager.findFragmentById(R.id.rightFragmentContainer) as? RightFragment
        rightFragment?.showOption(option)
    }
}
