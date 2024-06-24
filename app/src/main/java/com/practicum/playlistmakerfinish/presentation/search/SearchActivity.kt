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
import com.practicum.playlistmakerfinish.ServiceLocator
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.data.SharedPreferencesSearchHistoryRepository
import com.practicum.playlistmakerfinish.domain.api.TrackInteractor
import com.practicum.playlistmakerfinish.domain.model.Track
import com.practicum.playlistmakerfinish.presentation.player.PlayerActivity

class SearchActivity : AppCompatActivity() {

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
    private lateinit var trackInteractor: TrackInteractor

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
        trackInteractor = ServiceLocator.provideTrackInteractor()

        val sharedPrefs = getSharedPreferences(SEARCH_HISTORY_KEY, MODE_PRIVATE)
        val searchHistory = SharedPreferencesSearchHistoryRepository(sharedPrefs)

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
        val query = queryInput.text.toString()

        trackInteractor.searchTracks(query, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    if (foundTracks.isNotEmpty()) {
                        adapter.setList(foundTracks)
                        showTrackList()
                    } else {
                        showNoResultsPlaceholder()
                    }
                }
            }

            override fun onFailure() {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    showServerErrorPlaceholder()
                }
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