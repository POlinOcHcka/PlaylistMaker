package com.practicum.playlistmakerfinish.presentation.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.data.dto.TrackSearchResponse
import com.practicum.playlistmakerfinish.data.network.ItunesAPI
import com.practicum.playlistmakerfinish.domain.model.Track
import com.practicum.playlistmakerfinish.presentation.player.PlayerActivity
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

    private var searchText: String = ""

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {performSearch()}

    private lateinit var back: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var placeholderNoResults: LinearLayout
    private lateinit var placeholderServerError: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var tracksHistoryRv: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        back = findViewById(R.id.button_back_search)
        recyclerView = findViewById(R.id.rvTrack)
        adapter = TrackAdapter()
        queryInput = findViewById(R.id.search_string)
        clearButton = findViewById(R.id.clear)
        placeholderNoResults = findViewById(R.id.placeholderNoResults)
        placeholderServerError = findViewById(R.id.placeholderServerError)
        updateButton = findViewById(R.id.updateButton)
        tracksHistoryRv = findViewById(R.id.rvTrackHistory)
        historyAdapter = TrackAdapter()
        searchHistoryLayout = findViewById(R.id.searchHistory)
        clearHistoryButton = findViewById(R.id.clearTrackHistory)
        progressBar = findViewById(R.id.progressBar)

        val sharedPrefs = getSharedPreferences(SEARCH_HISTORY_KEY, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPrefs)

        adapter.onTrackClickListener = { track ->
            if (clickDebounce()) {
                searchHistory.saveTrack(track)
                val playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra(SEARCH_HISTORY_KEY, Gson().toJson(track))
                startActivity(playerIntent)
            }
        }

        historyAdapter.onTrackClickListener = { track ->
            if (clickDebounce()) {
                searchHistory.saveTrack(track)
                val playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra(SEARCH_HISTORY_KEY, Gson().toJson(track))
                startActivity(playerIntent)
            }
        }

        searchHistoryLayout.visibility = View.GONE

        queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(queryInput, InputMethodManager.SHOW_IMPLICIT)

                if (queryInput.text.isEmpty()) {
                    val historyTracks = searchHistory.readTracks().toList()
                    val mutableHistoryTracks = mutableListOf<Track>()
                    mutableHistoryTracks.addAll(historyTracks)
                    historyAdapter.setList(mutableHistoryTracks)
                    searchHistoryLayout.visibility = if (historyTracks.isNotEmpty()) View.VISIBLE else View.GONE
                } else {
                    searchHistoryLayout.visibility = View.GONE
                }
            }
        }

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else false
        }

        back.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            queryInput.text.clear() // Очистить текстовое поле
            clearButton.visibility = View.GONE // Скрыть кнопку (x)
            recyclerView.visibility = View.GONE
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(queryInput.windowToken, 0) // Скрыть клавиатуру
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearTracks()
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
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                // Ничего не делать
            }
        })

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("searchText", "")
            queryInput.setText(searchText)
        }

        recyclerView.adapter = adapter
        tracksHistoryRv.adapter = historyAdapter
    }

    private fun performSearch() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        itunes.search(queryInput.text.toString()).enqueue(object : Callback<TrackSearchResponse> {
            override fun onResponse(call: Call<TrackSearchResponse>, response: Response<TrackSearchResponse>) {
                progressBar.visibility = View.GONE
                if (response.code() == 200) {
                    if (response.body()?.results?.isNotEmpty() == true) {
                        mutableListOf(response.body()?.results!!)
                        showTrackList()
                    } else {
                        showNoResultsPlaceholder()
                    }
                } else {
                    showServerErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                showServerErrorPlaceholder()
            }
        })
    }

    private fun showTrackList() {
        recyclerView.visibility = View.VISIBLE
        placeholderNoResults.visibility = View.GONE
        placeholderServerError.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE
    }

    private fun showNoResultsPlaceholder() {
        recyclerView.visibility = View.GONE
        placeholderNoResults.visibility = View.VISIBLE
        placeholderServerError.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE
    }

    private fun showServerErrorPlaceholder() {
        placeholderServerError.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderNoResults.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE

        updateButton.setOnClickListener {
            performSearch()
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        Log.d("clickDebounce", "ProgressBar")
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchText", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("searchText", "")
    }
}