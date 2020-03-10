package com.example.apptest

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Config values
        val initialText = "$0.00"
        val maxAmount = 200000 // 200.000


        setupEditText(maxAmount, initialText)
    }

    private fun setupEditText(maxAmount: Int, initialText: String) {
        // Set a max  number of characters
        val maxLength = NumberFormat.getCurrencyInstance(Locale.US).format(maxAmount).length
        editText.filters = arrayOf<InputFilter>(LengthFilter(maxLength))

        editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (editText.text.isNullOrEmpty()) {
                    editText.setText(initialText)
                }
            } else if (editText.text.toString() == initialText) {
                editText.setText("")
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            private var lastAmount = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // TODO review
                val currentAmount =
                    clearCurrencyToNumber(s.toString()).toDoubleOrNull()?.div(100) ?: 0.0
                errorText.text = if (currentAmount > maxAmount) "Error" else ""
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s.toString()
                if (value != lastAmount && editText.hasFocus()) {
                    editText.removeTextChangedListener(this)

                    val cleanString = clearCurrencyToNumber(value)
                    val formatted = transformToCurrency(cleanString)
                    lastAmount = formatted

                    editText.setText(formatted)
                    editText.setSelection(formatted.length)
                    editText.addTextChangedListener(this)
                }
            }


        })
    }

    fun clearCurrencyToNumber(currencyValue: String?): String {
        return currencyValue?.replace("[(a-z)|(A-Z)|($,.)]".toRegex(), "") ?: ""
    }

    fun transformToCurrency(value: String): String {
        val parsed = value.toDoubleOrNull() ?: 0.0
        return NumberFormat.getCurrencyInstance(Locale.US).format(parsed / 100.0)
    }
}
