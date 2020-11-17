package com.zen.alchan.ui.settings.about


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.zen.alchan.BuildConfig

import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.updateBottomPadding
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.layout_toolbar.*

/**
 * A simple [Fragment] subclass.
 */
class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarLayout.apply {
            title = getString(R.string.about_al_chan)
            navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_left)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        aboutLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateBottomPadding(windowInsets, initialPadding)
        }

        versionText.text = "Version ${BuildConfig.VERSION_NAME}"
        linkGmailText.text = Constant.EMAIL_ADDRESS
        linkTwitterText.text = Constant.TWITTER_ACCOUNT

        linkAniListLayout.setOnClickListener { openLink(Constant.ALCHAN_THREAD_URL) }
        linkPlayStoreLayout.setOnClickListener { openLink(Constant.PLAY_STORE_URL) }
        linkGmailLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", Constant.EMAIL_ADDRESS, null))
            startActivity(intent)
        }
        linkTwitterLayout.setOnClickListener { openLink(Constant.TWITTER_URL) }
        linkGitHubLayout.setOnClickListener { openLink(Constant.GITHUB_URL) }

        val targetText = "here"
        val explanationText = SpannableString(getString(R.string.the_privacy_policy_of_this_app_can_be_read_fully_here))
        val startIndex = explanationText.indexOf(targetText)
        val endIndex = startIndex + targetText.length
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openLink(Constant.PRIVACY_POLICY_URL)
            }
        }

        explanationText.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        privacyPolicyText.movementMethod = LinkMovementMethod.getInstance()
        privacyPolicyText.text = explanationText
    }

    private fun openLink(url: String) {
        CustomTabsIntent.Builder().build().launchUrl(activity!!, Uri.parse(url))
    }
}
