package com.practicum.playlistmakerfinish.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.databinding.FragmentSettingsBinding
import com.practicum.playlistmakerfinish.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isDarkTheme.observe(viewLifecycleOwner, Observer { isDark ->
            binding.themeSwitcher.isChecked = isDark
        })

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        val shareClickListener = View.OnClickListener { shareApp() }
        binding.shareTheApp.setOnClickListener(shareClickListener)
        binding.shareString.setOnClickListener(shareClickListener)
        binding.shareButton.setOnClickListener(shareClickListener)

        val supportClickListener = View.OnClickListener { writeToSupport() }
        binding.writeToSupport.setOnClickListener(supportClickListener)
        binding.writeString.setOnClickListener(supportClickListener)
        binding.writeButton.setOnClickListener(supportClickListener)

        val agreementClickListener = View.OnClickListener { openUserAgreement() }
        binding.userAgreement.setOnClickListener(agreementClickListener)
        binding.agreementString.setOnClickListener(agreementClickListener)
        binding.agreementButton.setOnClickListener(agreementClickListener)
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            val settingsLinkCourse = getString(R.string.settings_link_course)
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, settingsLinkCourse)
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
    }

    private fun writeToSupport() {
        val supportEmail = getString(R.string.settings_support_email)
        val subject = getString(R.string.settings_subject)
        val message = getString(R.string.settings_message)

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(emailIntent)
    }

    private fun openUserAgreement() {
        val url = getString(R.string.settings_link_offer)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}