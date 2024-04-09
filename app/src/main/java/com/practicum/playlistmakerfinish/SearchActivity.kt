package com.practicum.playlistmakerfinish

import android.annotation.SuppressLint
import android.content.Intent
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
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmakerfinish.SharedPreferences.SEARCH_HISTORY_KEY
import com.practicum.playlistmakerfinish.SharedPreferences.SearchHistory
import com.practicum.playlistmakerfinish.adapter.TrackAdapter
import com.practicum.playlistmakerfinish.api.ItunesAPI
import com.practicum.playlistmakerfinish.model.TrackModel
import com.practicum.playlistmakerfinish.response.TrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunes = retrofit.create(ItunesAPI::class.java)

    private var searchText: String = "" // Глобальная переменная для хранения текста поискового запроса

    private var back: ImageButton = findViewById(R.id.button_back)

    private var recyclerView: RecyclerView = findViewById(R.id.rvTrack)
    private var adapter: TrackAdapter = TrackAdapter()
    private var queryInput: EditText = findViewById(R.id.search_string)
    private var clearButton: ImageButton = findViewById(R.id.clear)

    private var placeholderNoResults: LinearLayout = findViewById(R.id.placeholderNoResults)
    private var placeholderServerError: LinearLayout = findViewById(R.id.placeholderServerError)
    private var updateButton: Button = findViewById(R.id.updateButton)

    private var tracksHistoryRv: RecyclerView = findViewById(R.id.rvTrackHistory)
    private var historyAdapter: TrackAdapter = TrackAdapter()

    private var searchHistoryLayout: LinearLayout = findViewById(R.id.searchHistory)
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyTracks: MutableList<TrackModel>
    private var clearHistoryButton: Button = findViewById(R.id.clearTrackHistory)


    fun showTrackList() {
        recyclerView.visibility = View.VISIBLE
        placeholderNoResults.visibility = View.GONE
        placeholderServerError.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE
    }

    // Обработка успешного запроса без результатов
    fun showNoResultsPlaceholder() {
        recyclerView.visibility = View.GONE
        placeholderNoResults.visibility = View.VISIBLE
        placeholderServerError.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE
    }

    // Обработка ошибки сервера
    fun showServerErrorPlaceholder() {
        placeholderServerError.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderNoResults.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE

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
        recyclerView.visibility = View.VISIBLE
        placeholderNoResults.visibility = View.GONE
        placeholderServerError.visibility = View.GONE
    }

    fun performSearch() {
        // Скрыть плейсхолдер и отобразить прогресс-бар или другие элементы загрузки
        hidePlaceholder()
        // Выполнить поисковый запрос
        searchTrack()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistoryLayout.visibility = View.GONE

        queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && queryInput.text.isEmpty()) {
                val historyTracks = searchHistory.readTracks().toList()
                historyAdapter.setList(historyTracks as MutableList<TrackModel>)
                searchHistoryLayout.visibility = if (historyTracks.isNotEmpty()) View.VISIBLE else View.GONE
            } else {
                searchHistoryLayout.visibility = View.GONE
            }
        }

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            } else false
        }

        back.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }


        queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(queryInput, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        clearButton.setOnClickListener {
            queryInput.text.clear() // Очистить текстовое поле
            clearButton.visibility = View.GONE // Скрыть кнопку (x)
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(queryInput.windowToken, 0) // Скрыть клавиатуру
        }

        clearHistoryButton.setOnClickListener {
            historyTracks.clear()
            searchHistory.clearTracks()
            searchHistory.saveTrack(historyTracks)
            searchHistoryLayout.visibility = View.GONE
        }

        queryInput.addTextChangedListener(object : TextWatcher {
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

        // Проверяем, есть ли сохраненное состояние (Bundle savedInstanceState)
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("searchText", "")
            queryInput.setText(searchText)
        }

        // Добавляем TextWatcher для отслеживания изменений текста в EditText
        queryInput.addTextChangedListener(object : TextWatcher {
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
        initialHistory()

    }

    private fun initial() {recyclerView.adapter = adapter}

    private fun initialHistory() {tracksHistoryRv.adapter = adapter}

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