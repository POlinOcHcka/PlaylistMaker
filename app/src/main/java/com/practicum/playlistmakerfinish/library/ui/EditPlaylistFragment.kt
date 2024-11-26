package com.practicum.playlistmakerfinish.library.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmakerfinish.library.domain.model.IntentKeys.PLAYLIST_ID_KEY
import com.practicum.playlistmakerfinish.library.presentation.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : Fragment() {
    private val editPlaylistViewModel by viewModel<EditPlaylistViewModel>()

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private var isImageAdd: Boolean = false
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val value = arguments?.getString(PLAYLIST_ID_KEY)
        value?.let { editPlaylistViewModel.getPlaylist(it) }
        binding.playlistImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.playlistImage.setImageURI(uri)
                val uriLocal = editPlaylistViewModel.saveImageToLocalStorage(uri)
                editPlaylistViewModel.setUri(uriLocal)
                isImageAdd = true
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        editPlaylistViewModel.playlistLiveData.observe(viewLifecycleOwner) { playlist ->
            binding.newPlaylist.text = getString(R.string.edit_playlist)
            binding.createPlaylist.text = getString(R.string.save_button_text)
            binding.playlistName.setText(playlist.playlistName.toString())
            playlist.playlistDescription?.let { binding.playlistDescription.setText(it) }
            playlist.uri?.let { binding.playlistImage.setImageURI(it.toUri()) }

        }
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.playlistName.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylist.isEnabled = !text.isNullOrBlank()
            editPlaylistViewModel.setPlaylistName(text.toString())
        }
        binding.playlistDescription.doOnTextChanged { text, _, _, _ ->
            editPlaylistViewModel.setPlaylistDescroption(text.toString())
        }
        binding.createPlaylist.setOnClickListener {
            editPlaylistViewModel.editPlaylist()
            findNavController().navigateUp()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}