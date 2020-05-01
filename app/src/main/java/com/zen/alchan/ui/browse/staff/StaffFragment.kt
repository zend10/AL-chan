package com.zen.alchan.ui.browse.staff


import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.textview.MaterialTextView
import com.stfalcon.imageviewer.StfalconImageViewer

import com.zen.alchan.R
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.enums.StaffPage
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.StaffCharacter
import com.zen.alchan.helper.pojo.StaffMedia
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.character.CharacterFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.browse.staff.anime.StaffAnimeFragment
import com.zen.alchan.ui.browse.staff.bio.StaffBioFragment
import com.zen.alchan.ui.browse.staff.manga.StaffMangaFragment
import com.zen.alchan.ui.browse.staff.voice.StaffVoiceFragment
import kotlinx.android.synthetic.main.fragment_staff.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType
import kotlin.math.abs

/**
 * A simple [Fragment] subclass.
 */
class StaffFragment : BaseFragment() {

    private val viewModel by viewModel<StaffViewModel>()

    private lateinit var staffSectionMap: HashMap<StaffPage, Pair<ImageView, MaterialTextView>>
    private lateinit var staffFragmentList: List<Fragment>

    private lateinit var scaleUpAnim: Animation
    private lateinit var scaleDownAnim: Animation

    private lateinit var itemOpenAniList: MenuItem
    private lateinit var itemCopyLink: MenuItem

    companion object {
        const val STAFF_ID = "staffId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.staffId = arguments?.getInt(STAFF_ID)

        staffSectionMap = hashMapOf(
            Pair(StaffPage.BIO, Pair(staffBioIcon, staffBioText)),
            Pair(StaffPage.VOICE, Pair(staffVoiceIcon, staffVoiceText)),
            Pair(StaffPage.ANIME, Pair(staffAnimeIcon, staffAnimeText)),
            Pair(StaffPage.MANGA, Pair(staffMangaIcon, staffMangaText))
        )

        staffFragmentList = arrayListOf(StaffBioFragment(), StaffVoiceFragment(), StaffAnimeFragment(), StaffMangaFragment())

        scaleUpAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
        scaleDownAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)

        staffToolbar.setNavigationOnClickListener { activity?.finish() }
        staffToolbar.navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_delete)
        staffToolbar.inflateMenu(R.menu.menu_anilist_link)
        itemOpenAniList = staffToolbar.menu.findItem(R.id.itemOpenAnilist)
        itemCopyLink = staffToolbar.menu.findItem(R.id.itemCopyLink)

        initLayout()
        setupObserver()
        setupSection()
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkStaffIsFavorite()
    }

    private fun setupObserver() {
        viewModel.currentSection.observe(viewLifecycleOwner, Observer {
            setupSection()
        })

        viewModel.staffData.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (it.data?.Staff() != null) {
                        viewModel.currentStaffData = it.data.Staff()
                        setupHeader()
                    }
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.staffIsFavoriteData.observe(viewLifecycleOwner, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                if (it.data?.Staff()?.isFavourite == true) {
                    staffFavoriteButton.text = getString(R.string.favorited)
                    staffFavoriteButton.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    staffFavoriteButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.transparent))
                    staffFavoriteButton.strokeColor = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    staffFavoriteButton.strokeWidth = 2
                } else {
                    staffFavoriteButton.text = getString(R.string.set_as_favorite)
                    staffFavoriteButton.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeBackgroundColor))
                    staffFavoriteButton.backgroundTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    staffFavoriteButton.strokeWidth = 0
                }

                staffFavoriteButton.isEnabled = true
            }
        })

        viewModel.toggleFavouriteResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    viewModel.checkStaffIsFavorite()
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, R.string.change_saved)
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.initData()
    }

    private fun initLayout() {
        staffRefreshLayout.setOnRefreshListener {
            staffRefreshLayout.isRefreshing = false
            viewModel.refreshData()
        }

        staffAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            // disable refresh when toolbar is not fully expanded
            staffRefreshLayout.isEnabled = verticalOffset == 0

            // 50 is magic number gotten from trial and error
            if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                if (staffBannerContentLayout.isVisible) {
                    staffBannerContentLayout.startAnimation(scaleDownAnim)
                    staffBannerContentLayout.visibility = View.INVISIBLE
                }
            } else {
                if (staffBannerContentLayout.isInvisible) {
                    staffBannerContentLayout.startAnimation(scaleUpAnim)
                    staffBannerContentLayout.visibility = View.VISIBLE
                }
            }
        })

        staffFavoriteButton.setOnClickListener {
            viewModel.updateFavorite()
        }
    }

    private fun setupHeader() {
        GlideApp.with(this).load(viewModel.currentStaffData?.image()?.large()).apply(RequestOptions.circleCropTransform()).into(leftImage)

        if (viewModel.currentStaffData?.image()?.large() != null) {
            leftImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(context, arrayOf(viewModel.currentStaffData?.image()?.large())) { view, image ->
                    GlideApp.with(context!!).load(image).into(view)
                }.withTransitionFrom(leftImage).show(true)
            }
        }

        leftText.text = viewModel.currentStaffData?.name()?.full()
        staffNativeNameText.text = viewModel.currentStaffData?.name()?.native_()

        staffFavoriteCountText.text = viewModel.currentStaffData?.favourites()?.toString()

        itemOpenAniList.isVisible = true
        itemCopyLink.isVisible = true

        itemOpenAniList.setOnMenuItemClickListener {
            CustomTabsIntent.Builder()
                .build()
                .launchUrl(activity!!, Uri.parse(viewModel.currentStaffData?.siteUrl()))
            true
        }

        itemCopyLink.setOnMenuItemClickListener {
            AndroidUtility.copyToClipboard(activity, viewModel.currentStaffData?.siteUrl()!!)
            DialogUtility.showToast(activity, R.string.link_copied)
            true
        }

        staffBioLayout.setOnClickListener { viewModel.setStaffSection(StaffPage.BIO) }
        staffVoiceLayout.setOnClickListener { viewModel.setStaffSection(StaffPage.VOICE) }
        staffAnimeLayout.setOnClickListener { viewModel.setStaffSection(StaffPage.ANIME) }
        staffMangaLayout.setOnClickListener { viewModel.setStaffSection(StaffPage.MANGA) }

        if (staffViewPager.adapter == null) {
            staffViewPager.setPagingEnabled(false)
            staffViewPager.offscreenPageLimit = staffSectionMap.size
            staffViewPager.adapter = StaffViewPagerAdapter(childFragmentManager, viewModel.staffId!!, staffFragmentList)
        }

        viewModel.setStaffSection(viewModel.currentSection.value ?: StaffPage.BIO)
    }

    private fun setupSection() {
        staffSectionMap.forEach {
            if (it.key == viewModel.currentSection.value) {
                it.value.first.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeSecondaryColor))
                it.value.second.setTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeSecondaryColor))
            } else {
                it.value.first.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
                it.value.second.setTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
            }
        }

        // maybe should not use magic number
        staffViewPager.currentItem = when (viewModel.currentSection.value) {
            StaffPage.BIO -> 0
            StaffPage.VOICE -> 1
            StaffPage.ANIME -> 2
            StaffPage.MANGA -> 3
            else -> 0
        }
    }
}
