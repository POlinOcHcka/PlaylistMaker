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
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmakerfinish.adapter.TrackAdapter
import com.practicum.playlistmakerfinish.model.TrackModel

class SearchActivity : AppCompatActivity() {

    private var searchText: String = "" // Глобальная переменная для хранения текста поискового запроса


    lateinit var adapter: TrackAdapter
    lateinit var recyclerView: RecyclerView
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

        initial()

    }

    private fun initial() {
        recyclerView = findViewById(R.id.rv_track)
        adapter = TrackAdapter()
        recyclerView.adapter = adapter
        adapter.setList(myTrack())
    }

    fun myTrack(): ArrayList<TrackModel> {

        val trackList = ArrayList<TrackModel>()

        val track1 = TrackModel("Smells Like Teen Spirit", "Nirvana", "5:01","https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg")
        trackList.add(track1)

        val track2 = TrackModel("Billie Jean", "Michael Jackson", "4:35","https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg")
        trackList.add(track2)

        val track3 = TrackModel("Stayin' Alive", "Bee Gees", "4:10","https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg")
        trackList.add(track3)

        val track4 = TrackModel("Whole Lotta Love", "Led Zeppelin", "5:33","https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg")
        trackList.add(track4)

        val track5 = TrackModel("Sweet Child O'Mine", "Guns N' Roses", "5:03","https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
        trackList.add(track5)

        return trackList
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