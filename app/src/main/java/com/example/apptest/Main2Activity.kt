package com.example.apptest

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.button
import kotlinx.android.synthetic.main.activity_main.changeActivity
import kotlinx.android.synthetic.main.activity_main_2.*


class Main2Activity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2)


        button.setOnClickListener { changeVisibility() }

        changeActivity.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun getGenericView(): View? = image_map
}