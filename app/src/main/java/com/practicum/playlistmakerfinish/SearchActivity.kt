package com.practicum.playlistmakerfinish

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmakerfinish.adapter.TrackAdapter
import com.practicum.playlistmakerfinish.api.ItunesAPI
import com.practicum.playlistmakerfinish.model.TrackModel
import com.practicum.playlistmakerfinish.response.TrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunes = retrofit.create(ItunesAPI::class.java)

    private var searchText: String = "" // Глобальная переменная для хранения текста поискового запроса

    lateinit var adapter: TrackAdapter
    lateinit var recyclerView: RecyclerView

    lateinit var queryInput: EditText

    lateinit var placeholder: LinearLayout
    lateinit var placeholderImageNoResults: ImageView
    lateinit var placeholderTextNoResults: TextView
    lateinit var placeholderImageServerError: ImageView
    lateinit var placeholderTextServerError: TextView
    lateinit var updateButton: Button

    fun showTrackList() {
        recyclerView.visibility = View.VISIBLE
        placeholderImageNoResults.visibility = View.GONE
        placeholderTextNoResults.visibility = View.GONE
        placeholderImageServerError.visibility = View.GONE
        placeholderTextServerError.visibility = View.GONE
        placeholder.visibility = View.GONE
        updateButton.visibility = View.GONE
    }

    // Обработка успешного запроса без результатов
    fun showNoResultsPlaceholder() {
        placeholderImageNoResults.visibility = View.VISIBLE
        placeholderTextNoResults.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderImageServerError.visibility = View.GONE
        placeholderTextServerError.visibility = View.GONE
        updateButton.visibility = View.GONE
    }

    // Обработка ошибки сервера
    fun showServerErrorPlaceholder() {
        placeholderImageNoResults.visibility = View.GONE
        placeholderTextNoResults.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholderImageServerError.visibility = View.VISIBLE
        placeholderTextServerError.visibility = View.VISIBLE
        updateButton.visibility = View.VISIBLE

        // Добавить слушатель на кнопку "Обновить"
        updateButton.setOnClickListener {
            // Вызвать функцию для повторного выполнения поискового запроса
            performSearch()
        }
    }

    fun searchTrack() {
        itunes.search(queryInput.text.toString()).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.results?.isNotEmpty() == true) {
                        adapter.setList(response.body()?.results!!)
                        adapter.notifyDataSetChanged()
                        showTrackList()
                    } else {
                        showNoResultsPlaceholder()
                    }
                } else {
                    showServerErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showServerErrorPlaceholder()
            }
        })
    }

    // Дополнительная функция для скрытия плейсхолдера и отображения элементов загрузки
    fun hidePlaceholder() {
//        placeholder.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    fun performSearch() {
        // Скрыть плейсхолдер и отобразить прогресс-бар или другие элементы загрузки
        hidePlaceholder()
        // Выполнить поисковый запрос
        searchTrack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val back = findViewById<ImageButton>(R.id.button_back)

        queryInput = findViewById(R.id.search_string)

        placeholder = findViewById(R.id.placeholder)
        placeholderImageNoResults = findViewById(R.id.placeholderImageNoResults)
        placeholderTextNoResults = findViewById(R.id.placeholderTextNoResults)
        placeholderImageServerError = findViewById(R.id.placeholderImageServerError)
        placeholderTextServerError = findViewById(R.id.placeholderTextServerError)
        updateButton = findViewById(R.id.updateButton)

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                adapter.notifyDataSetChanged()
                true
            } else false
        }

        back.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val search = findViewById<EditText>(R.id.search_string)

        search.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT)
            }
        }


        val searchString = findViewById<EditText>(R.id.search_string)
        val clearButton = findViewById<ImageButton>(R.id.clear)

        clearButton.setOnClickListener {
            searchString.text.clear() // Очистить текстовое поле
            clearButton.visibility = View.GONE // Скрыть кнопку (x)
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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