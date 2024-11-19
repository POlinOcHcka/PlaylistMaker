package com.practicum.playlistmakerfinish.search.ui


import android.content.Context
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
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.databinding.FragmentSearchBinding
import com.practicum.playlistmakerfinish.search.domain.model.IntentKeys.SEARCH_HISTORY_KEY
import com.practicum.playlistmakerfinish.search.domain.model.Track
import com.practicum.playlistmakerfinish.search.presentation.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val gson: Gson by inject()

    private var searchJob: Job? = null
    private val debouncePeriod: Long = 2000L

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
                navigateToPlayerFragment(track)
            }
        }

        historyAdapter.onTrackClickListener = { track ->
            clickDebounce {
                viewModel.saveTrackToHistory(track)
                navigateToPlayerFragment(track)
            }
        }

        binding.searchString.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchString.text.isEmpty()) {
                viewModel.loadSearchHistory()
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
                // Показать/скрыть кнопку очистки сразу при изменении текста
                binding.clear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                // Поиск треков с дебаунсом
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(debouncePeriod)
                    if (s.isNullOrEmpty()) {
                        // Если строка поиска пуста, скрываем заглушку и загружаем историю поиска
                        hidePlaceholders()
                        viewModel.loadSearchHistory()
                    } else {
                        // Если есть текст, ищем треки
                        viewModel.searchTracks(s.toString())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Кнопка "Очистить поиск"
        binding.clear.setOnClickListener {
            binding.searchString.text.clear()
            binding.clear.visibility = View.GONE
            hideKeyboard()

            viewModel.loadSearchHistory()

            binding.rvTrack.visibility = View.GONE
            binding.searchHistory.visibility = View.VISIBLE
        }

        // Кнопка "Очистить историю"
        binding.clearTrackHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        observeViewModel()
    }

    private fun navigateToPlayerFragment(track: Track) {
        val bundle = Bundle().apply {
            putString(SEARCH_HISTORY_KEY, gson.toJson(track))
        }
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment, bundle)
    }

    private fun observeViewModel() {
        // Наблюдаем за результатами поиска
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { tracks ->
                if (tracks.isEmpty() && binding.searchString.text.isNotEmpty()) {
                    showNoResultsPlaceholder() // Показать заглушку, если результатов нет и есть текст в строке поиска
                } else {
                    adapter.setList(tracks)
                    showTrackList()
                }
            }
        }

        // Наблюдаем за состоянием загрузки
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    binding.searchHistory.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                    if (binding.searchString.text.isEmpty() && binding.searchString.hasFocus()) {
                        viewModel.loadSearchHistory()
                    }
                }
            }
        }

        // Наблюдаем за ошибками
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showError.collect { showError ->
                if (showError) showServerErrorPlaceholder() else showTrackList()
            }
        }

        // Наблюдаем за заглушкой "Нет результатов"
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showNoResults.collect { showNoResults ->
                if (showNoResults) {
                    showNoResultsPlaceholder() // Показываем плейсхолдер "Нет результатов"
                }
            }
        }

        // Наблюдаем за историей поиска
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchHistoryTracks.collect { tracks ->
                historyAdapter.setList(tracks)
                if (tracks.isNotEmpty() && binding.searchString.text.isEmpty()) {
                    binding.searchHistory.visibility = View.VISIBLE
                    binding.clearTrackHistory.visibility = View.VISIBLE
                    binding.textViewSearchHistory.visibility = View.VISIBLE
                } else {
                    binding.searchHistory.visibility = View.GONE
                    binding.clearTrackHistory.visibility = View.GONE
                    binding.textViewSearchHistory.visibility = View.GONE
                }
            }
        }
    }

    private fun hidePlaceholders() {
        binding.placeholderNoResults.visibility = View.GONE
        binding.placeholderServerError.visibility = View.GONE
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

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchString.windowToken, 0)
    }

    private fun clickDebounce(action: () -> Unit) {
        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            action()
            delay(debouncePeriod)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchJob?.cancel()
    }
}