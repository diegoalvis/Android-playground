package com.example.apptest

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val codeReceiver by lazy { CodeScannerBroadcastReceiver(this::onCodeRead) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text.text = Build.BRAND


        codeReceiver.registerReceiver(this)


        modalProductExpirationDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                var clearString = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                modalProductExpirationDate.removeTextChangedListener(this)
                var clearString = s.toString()
                if (before == 1 && clearString.isNotBlank() && clearString.elementAt(clearString.length - 1) == '/') {
                    clearString = clearString.substring(0, clearString.length - 1)
                    modalProductExpirationDate.setText(clearString)
                    modalProductExpirationDate.setSelection(clearString.length)
                }
                if (before == 0 && (clearString.length == 3 || clearString.length == 6)) {
                    clearString = clearString.substring(0, clearString.length - 1) + "/" + clearString.last()
                    modalProductExpirationDate.setText(clearString)
                    modalProductExpirationDate.setSelection(clearString.length)
                }
                modalProductExpirationDate.addTextChangedListener(this)
            }
        })


    }

    override fun onDestroy() {
        super.onDestroy()
        codeReceiver.unregister(this)
    }

    @SuppressLint("SetTextI18n")
    private fun onCodeRead(code: String) {
        text.text = "${Build.BRAND}\n$code"
    }

}