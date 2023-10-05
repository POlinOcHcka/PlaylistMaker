package com.practicum.playlistmakerfinish

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout

class SettingsActivity() : AppCompatActivity() {

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back = findViewById<ImageButton>(R.id.button_back)

        back.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val share = findViewById<LinearLayout>(R.id.share_the_app)

        share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            val settingsLinkCourse = getString(R.string.settings_link_course)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, settingsLinkCourse)
            startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
        }

        val support = findViewById<LinearLayout>(R.id.write_to_support)

        support.setOnClickListener {
            val supportEmail = getString(R.string.settings_support_email)
            val subject = getString(R.string.settings_subject)
            val message = getString(R.string.settings_message)

            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, supportEmail)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            emailIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(emailIntent)
        }

        val agreement = findViewById<LinearLayout>(R.id.user_agreement)

        agreement.setOnClickListener {
            val url = getString(R.string.settings_link_offer)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }

}