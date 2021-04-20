package com.zen.alchan.ui.login

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.applyTopBottomPaddingInsets
import com.zen.alchan.helper.libs.ImageUtil
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.NavigationManager
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val viewModel by viewModel<LoginViewModel>()

    override fun setupLayout() {
        ImageUtil.loadImage(requireContext(), R.drawable.landing_wallpaper, loginBackgroundImage)

        loginBackIcon.setOnClickListener { requireActivity().onBackPressed() }

        registerButton.setOnClickListener { openWebView(NavigationManager.Url.ANILIST_REGISTER) }
        loginButton.setOnClickListener { openWebView(NavigationManager.Url.ANILIST_LOGIN) }

        enterWithoutLoginButton.setOnClickListener {
            viewModel.loginAsGuest()
            requireActivity().onBackPressed()
            navigate(NavigationManager.Page.MAIN)
        }

        val aniListText = "anilist.co"
        val noticeText= SpannableString(getString(R.string.you_ll_be_redirected_to_anilist_co_to_login_register_make_sure_the_url_is_anilist_co_before_entering_your_email_and_password))
        val startIndex = noticeText.indexOf(aniListText)
        val endIndex = startIndex + aniListText.length
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                openWebView(NavigationManager.Url.ANILIST_WEBSITE)
            }
        }
        noticeText.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        loginNoticeText.movementMethod = LinkMovementMethod.getInstance()
        loginNoticeText.text = noticeText
    }

    override fun setupInsets() {
        loginContentRoot.applyTopBottomPaddingInsets()
    }

    override fun setupObserver() {
        disposables.add(
            viewModel.loading.subscribe {
                loadingLayout.visibility = if (it) View.VISIBLE else View.GONE
            }
        )

        arguments?.let {
            it.getString(BEARER_TOKEN)?.let { bearerToken ->
                viewModel.login(bearerToken)
            }
        }
    }

    companion object {
        private const val BEARER_TOKEN = "bearerToken"

        @JvmStatic
        fun newInstance(bearerToken: String? = null) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(BEARER_TOKEN, bearerToken)
                }
            }
    }
}