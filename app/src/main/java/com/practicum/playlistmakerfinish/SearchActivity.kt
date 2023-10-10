package com.practicum.playlistmakerfinish

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton

class SearchActivity : AppCompatActivity() {

    private var searchText: String = "" // Глобальная переменная для хранения текста поискового запроса
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val back = findViewById<ImageButton>(R.id.button_back)

        back.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val search = findViewById<EditText>(R.id.search_string)

        search.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        val searchString = findViewById<EditText>(R.id.search_string)
        val clearButton = findViewById<ImageButton>(R.id.clear)

        clearButton.setOnClickListener {
            searchString.text.clear() // Очистить текстовое поле
            clearButton.visibility = View.GONE // Скрыть кнопку (x)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchString.windowToken, 0) // Скрыть клавиатуру
        }

        searchString.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ничего не делать
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Показать или скрыть кнопку (x) в зависимости от наличия текста
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.GONE
                } else {
                    clearButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Ничего не делать
            }
        })

        val searchEditText = findViewById<EditText>(R.id.search_string)

        // Проверяем, есть ли сохраненное состояние (Bundle savedInstanceState)
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("searchText", "")
            searchEditText.setText(searchText)
        }

        // Добавляем TextWatcher для отслеживания изменений текста в EditText
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ничего не делаем перед изменением текста
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Обновляем глобальную переменную searchText при изменении текста
                searchText = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {
                // Ничего не делаем после изменения текста
            }
        })

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняем текст поискового запроса в Bundle
        outState.putString("searchText", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Восстанавливаем текст поискового запроса из Bundle
        searchText = savedInstanceState.getString("searchText", "")
    }
}