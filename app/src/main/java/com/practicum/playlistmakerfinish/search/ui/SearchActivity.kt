package com.practicum.playlistmakerfinish.search.ui


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.ServiceLocator.ServiceLocator
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.player.ui.PlayerActivity
import com.practicum.playlistmakerfinish.search.domain.model.IntentKeys
import com.practicum.playlistmakerfinish.search.domain.model.IntentKeys.SEARCH_HISTORY_KEY
import com.practicum.playlistmakerfinish.search.presentation.SearchViewModel
import com.practicum.playlistmakerfinish.search.presentation.SearchViewModelFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel

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

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

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

        recyclerView.adapter = adapter
        tracksHistoryRv.adapter = historyAdapter

        val trackInteractor = ServiceLocator.provideTrackInteractor()
        val searchHistory = ServiceLocator.provideSearchHistoryRepository()

        viewModel = ViewModelProvider(this, SearchViewModelFactory(trackInteractor, searchHistory))
            .get(SearchViewModel::class.java)

        adapter.onTrackClickListener = { track ->
            if (clickDebounce()) {
                viewModel.saveTrackToHistory(track)
                val playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra(SEARCH_HISTORY_KEY, Gson().toJson(track))
                startActivity(playerIntent)
            }
        }

        historyAdapter.onTrackClickListener = { track ->
            if (clickDebounce()) {
                viewModel.saveTrackToHistory(track)
                val playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra(SEARCH_HISTORY_KEY, Gson().toJson(track))
                startActivity(playerIntent)
            }
        }

        queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(queryInput, InputMethodManager.SHOW_IMPLICIT)

                if (queryInput.text.isEmpty()) {
                    viewModel.loadSearchHistory()
                }
            } else {
                searchHistoryLayout.visibility = View.GONE
            }
        }

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchTracks(queryInput.text.toString())
                true
            } else false
        }

        queryInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.GONE
                } else {
                    clearButton.visibility = View.VISIBLE
                }
                viewModel.searchTracks(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearButton.setOnClickListener {
            queryInput.text.clear()
            clearButton.visibility = View.GONE
            recyclerView.visibility = View.GONE
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(queryInput.windowToken, 0)
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        back.setOnClickListener {
            finish()
        }

        observeViewModel()

        if (savedInstanceState != null) {
            val searchText = savedInstanceState.getString("searchText", "")
            queryInput.setText(searchText)
        }

        searchHistoryLayout.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { tracks ->
            adapter.setList(tracks)
            showTrackList()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.showNoResults.observe(this) { showNoResults ->
            if (showNoResults) showNoResultsPlaceholder() else showTrackList()
        }

        viewModel.showError.observe(this) { showError ->
            if (showError) showServerErrorPlaceholder() else showTrackList()
        }

        viewModel.searchHistoryTracks.observe(this) { tracks ->
            historyAdapter.setList(tracks)
            if (tracks.isNotEmpty() && queryInput.hasFocus() && queryInput.text.isEmpty()) {
                searchHistoryLayout.visibility = View.VISIBLE
            } else {
                searchHistoryLayout.visibility = View.GONE
            }
        }
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
            viewModel.searchTracks(queryInput.text.toString())
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, 1000L)
        }
        return current
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchText", queryInput.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchText = savedInstanceState.getString("searchText", "")
        queryInput.setText(searchText)
    }
}