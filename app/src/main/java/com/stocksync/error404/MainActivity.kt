package com.stocksync.error404

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), LeftFragment.OnOptionSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // LeftFragment se engancha solo en onAttach() porque esta Activity
        // implementa OnOptionSelectedListener; RightFragment muestra "Perfil"
        // por defecto en su propio onViewCreated().
    }

    override fun onOptionSelected(option: String) {
        val rightFragment =
            supportFragmentManager.findFragmentById(R.id.rightFragmentContainer) as? RightFragment
        rightFragment?.showOption(option)
    }
}
