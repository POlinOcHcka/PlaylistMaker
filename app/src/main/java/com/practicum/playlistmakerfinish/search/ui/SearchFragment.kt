package com.practicum.playlistmakerfinish.search.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.databinding.FragmentSearchBinding
import com.practicum.playlistmakerfinish.player.ui.PlayerActivity
import com.practicum.playlistmakerfinish.search.domain.model.IntentKeys
import com.practicum.playlistmakerfinish.search.presentation.SearchViewModel
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val gson: Gson by inject()

    private var clickJob: Job? = null
    private val debouncePeriod: Long = 500L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter()
        historyAdapter = TrackAdapter()

        binding.rvTrack.adapter = adapter
        binding.rvTrackHistory.adapter = historyAdapter

        adapter.onTrackClickListener = { track ->
            clickDebounce {
                viewModel.saveTrackToHistory(track)
                val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
                playerIntent.putExtra(IntentKeys.SEARCH_HISTORY_KEY, gson.toJson(track))
                startActivity(playerIntent)
            }
        }

        historyAdapter.onTrackClickListener = { track ->
            clickDebounce {
                viewModel.saveTrackToHistory(track)
                val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
                playerIntent.putExtra(IntentKeys.SEARCH_HISTORY_KEY, gson.toJson(track))
                startActivity(playerIntent)
            }
        }

        binding.searchString.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.searchString, InputMethodManager.SHOW_IMPLICIT)

                if (binding.searchString.text.isEmpty()) {
                    viewModel.loadSearchHistory()
                }
            } else {
                binding.searchHistory.visibility = View.GONE
            }
        }

        binding.searchString.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchTracks(binding.searchString.text.toString())
                true
            } else false
        }

        binding.searchString.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchTracks(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.clear.setOnClickListener {
            binding.searchString.text.clear()
            binding.clear.visibility = View.GONE
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchString.windowToken, 0)
        }

        binding.clearTrackHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { tracks ->
                if (tracks.isEmpty()) {
                    showNoResultsPlaceholder()
                } else {
                    adapter.setList(tracks)
                    showTrackList()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    binding.searchHistory.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                    if (binding.searchString.text.isEmpty() && binding.searchString.hasFocus()) {
                        binding.searchHistory.visibility = View.VISIBLE
                    } else {
                        binding.searchHistory.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showError.collect { showError ->
                if (showError) showServerErrorPlaceholder() else showTrackList()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchHistoryTracks.collect { tracks ->
                historyAdapter.setList(tracks)
                if (tracks.isNotEmpty() && binding.searchString.hasFocus() && binding.searchString.text.isEmpty()) {
                    binding.searchHistory.visibility = View.VISIBLE
                } else {
                    binding.searchHistory.visibility = View.GONE
                }
            }
        }
    }

    private fun showTrackList() {
        binding.rvTrack.visibility = View.VISIBLE
        binding.placeholderNoResults.visibility = View.GONE
        binding.placeholderServerError.visibility = View.GONE
        binding.searchHistory.visibility = View.GONE
    }

    private fun showNoResultsPlaceholder() {
        binding.rvTrack.visibility = View.GONE
        binding.placeholderNoResults.visibility = View.VISIBLE
        binding.placeholderServerError.visibility = View.GONE
        binding.searchHistory.visibility = View.GONE
    }

    private fun showServerErrorPlaceholder() {
        binding.placeholderServerError.visibility = View.VISIBLE
        binding.rvTrack.visibility = View.GONE
        binding.placeholderNoResults.visibility = View.GONE
        binding.searchHistory.visibility = View.GONE

        binding.updateButton.setOnClickListener {
            viewModel.searchTracks(binding.searchString.text.toString())
        }
    }

    private fun clickDebounce(action: () -> Unit) {
        clickJob?.cancel()
        clickJob = viewLifecycleOwner.lifecycleScope.launch {
            action()
            delay(debouncePeriod)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        clickJob?.cancel()
    }
}