package com.practicum.playlistmakerfinish.search.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.databinding.FragmentSearchBinding
import com.practicum.playlistmakerfinish.player.ui.PlayerActivity
import com.practicum.playlistmakerfinish.search.domain.model.IntentKeys
import com.practicum.playlistmakerfinish.search.presentation.SearchViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private val gson: Gson by inject()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

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
            if (clickDebounce()) {
                viewModel.saveTrackToHistory(track)
                val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
                playerIntent.putExtra(IntentKeys.SEARCH_HISTORY_KEY, gson.toJson(track))
                startActivity(playerIntent)
            }
        }

        historyAdapter.onTrackClickListener = { track ->
            if (clickDebounce()) {
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
                if (s.isNullOrEmpty()) {
                    binding.clear.visibility = View.GONE
                } else {
                    binding.clear.visibility = View.VISIBLE
                }
                viewModel.searchTracks(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.clear.setOnClickListener {
            binding.searchString.text.clear()
            binding.clear.visibility = View.GONE
            binding.rvTrack.visibility = View.GONE
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchString.windowToken, 0)
        }

        binding.clearTrackHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        observeViewModel()

        if (savedInstanceState != null) {
            val searchText = savedInstanceState.getString("searchText", "")
            binding.searchString.setText(searchText)
        }

        binding.searchHistory.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isEmpty()) {
                showNoResultsPlaceholder()
            } else {
                adapter.setList(tracks)
                showTrackList()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.showError.observe(viewLifecycleOwner) { showError ->
            if (showError) showServerErrorPlaceholder() else showTrackList()
        }

        viewModel.searchHistoryTracks.observe(viewLifecycleOwner) { tracks ->
            historyAdapter.setList(tracks)
            if (tracks.isNotEmpty() && binding.searchString.hasFocus() && binding.searchString.text.isEmpty()) {
                binding.searchHistory.visibility = View.VISIBLE
            } else {
                binding.searchHistory.visibility = View.GONE
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
        outState.putString("searchText", binding.searchString.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}