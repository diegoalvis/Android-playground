package com.example.apptest

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {


    private var visibility: Boolean = false

    fun changeVisibility() {
        visibility = !visibility
        getGenericView()?.visibility = if (visibility) VISIBLE else INVISIBLE
    }


    abstract fun getGenericView(): View?
}




