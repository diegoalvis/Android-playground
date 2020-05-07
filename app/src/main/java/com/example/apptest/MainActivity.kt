package com.example.apptest

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener { changeVisibility() }

        changeActivity.setOnClickListener {
            startActivity(Intent(this, Main2Activity::class.java))
            finish()
        }
    }

    override fun getGenericView(): View? = progress_circular
}