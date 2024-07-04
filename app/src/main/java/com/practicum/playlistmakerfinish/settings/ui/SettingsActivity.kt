package com.practicum.playlistmakerfinish.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.settings.data.SettingsRepository
import com.practicum.playlistmakerfinish.settings.domain.SwitchThemeUseCase
import com.practicum.playlistmakerfinish.settings.presentation.SettingsViewModel
import com.practicum.playlistmakerfinish.settings.presentation.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val repository = SettingsRepository(applicationContext)
        val switchThemeUseCase = SwitchThemeUseCase(repository)
        val viewModelFactory = SettingsViewModelFactory(switchThemeUseCase)
        viewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]

        val back = findViewById<ImageButton>(R.id.button_back_settings)
        back.setOnClickListener { finish() }

        val themeSwitcher = findViewById<SwitchCompat>(R.id.themeSwitcher)
        viewModel.isDarkTheme.observe(this, Observer { isDark ->
            themeSwitcher.isChecked = isDark
        })

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        val share = findViewById<LinearLayout>(R.id.share_the_app)
        share.setOnClickListener { shareApp() }

        val shareString = findViewById<TextView>(R.id.share_string)
        shareString.setOnClickListener { shareApp() }

        val shareButton = findViewById<ImageButton>(R.id.share_button)
        shareButton.setOnClickListener { shareApp() }

        val support = findViewById<LinearLayout>(R.id.write_to_support)
        support.setOnClickListener { writeToSupport() }

        val supportString = findViewById<TextView>(R.id.write_string)
        supportString.setOnClickListener { writeToSupport() }

        val supportButton = findViewById<ImageButton>(R.id.write_button)
        supportButton.setOnClickListener { writeToSupport() }

        val agreement = findViewById<LinearLayout>(R.id.user_agreement)
        agreement.setOnClickListener { openUserAgreement() }

        val agreementString = findViewById<TextView>(R.id.agreement_string)
        agreementString.setOnClickListener { openUserAgreement() }

        val agreementButton = findViewById<ImageButton>(R.id.agreement_button)
        agreementButton.setOnClickListener { openUserAgreement() }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        val settingsLinkCourse = getString(R.string.settings_link_course)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, settingsLinkCourse)
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
}