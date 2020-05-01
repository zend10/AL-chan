package com.zen.alchan.ui.profile


import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.*
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout

import com.zen.alchan.R
import com.zen.alchan.helper.enums.ProfileSection
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseMainFragment
import com.zen.alchan.ui.profile.bio.BioFragment
import com.zen.alchan.ui.profile.favorites.FavoritesFragment
import com.zen.alchan.ui.profile.reviews.ReviewsFragment
import com.zen.alchan.ui.settings.SettingsActivity
import com.zen.alchan.ui.profile.stats.StatsFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class ProfileFragment : BaseMainFragment() {

    private val viewModel by viewModel<ProfileViewModel>()
    private lateinit var profileSectionMap: HashMap<ProfileSection, Pair<ImageView, TextView>>

    private lateinit var scaleUpAnim: Animation
    private lateinit var scaleDownAnim: Animation

    private lateinit var itemNotifications: MenuItem
    private lateinit var itemSettings: MenuItem
    private lateinit var itemShareProfile: MenuItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        profileSectionMap = hashMapOf(
            Pair(ProfileSection.BIO, Pair(profileBioIcon, profileBioText)),
            Pair(ProfileSection.FAVORITES, Pair(profileFavoritesIcon, profileFavoritesText)),
            Pair(ProfileSection.STATS, Pair(profileStatsIcon, profileStatsText)),
            Pair(ProfileSection.REVIEWS, Pair(profileReviewsIcon, profileReviewsText))
        )

        scaleUpAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
        scaleDownAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)

        profileToolbar.menu.apply {
            itemNotifications = findItem(R.id.itemNotifications)
            itemSettings = findItem(R.id.itemSettings)
            itemShareProfile = findItem(R.id.itemShareProfile)
        }

        setupObserver()
        initLayout()
        setupSection()
    }

    private fun setupObserver() {
        viewModel.currentSection.observe(viewLifecycleOwner, Observer {
            setupSection()
        })

        viewModel.viewerData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                initLayout()
            }
        })

        viewModel.viewerDataResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> {
                    profileRefreshLayout.isRefreshing = false
                    loadingLayout.visibility = View.VISIBLE
                }
                ResponseStatus.SUCCESS -> loadingLayout.visibility = View.GONE
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.initData()
    }

    private fun initLayout() {
        val user = viewModel.viewerData.value

        profileRefreshLayout.setOnRefreshListener {
            profileRefreshLayout.isRefreshing = false
            viewModel.retrieveViewerData()
            // TODO: reload data reviews, stats, favs
        }

        GlideApp.with(this).load(user?.bannerImage).into(profileBannerImage)
        GlideApp.with(this).load(user?.avatar?.large).apply(RequestOptions.circleCropTransform()).into(profileAvatarImage)
        profileUsernameText.text = user?.name
        profileAnimeCountText.text = user?.statistics?.anime?.count.toString()
        profileMangaCountText.text = user?.statistics?.manga?.count.toString()
        // TODO: set following and followers text

        profileAnimeCountLayout.setOnClickListener {
            listener?.changeMenu(R.id.itemAnime)
        }
        profileMangaCountLayout.setOnClickListener {
            listener?.changeMenu(R.id.itemManga)
        }
        profileFollowingCountLayout.setOnClickListener {
            // TODO: open following list
        }
        profileFollowersCountLayout.setOnClickListener {
            // TODO: open followers list
        }

        profileBioLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.BIO) }
        profileFavoritesLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.FAVORITES) }
        profileStatsLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.STATS) }
        profileReviewsLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.REVIEWS) }

        profileAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            // disable refresh when toolbar is not fully expanded
            profileRefreshLayout.isEnabled = verticalOffset == 0

            // 50 is magic number gotten from trial and error
            if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                if (profileNumberLayout.isVisible) {
                    profileNumberLayout.startAnimation(scaleDownAnim)
                    profileNumberLayout.visibility = View.INVISIBLE
                }
            } else {
                if (profileNumberLayout.isInvisible) {
                    profileNumberLayout.startAnimation(scaleUpAnim)
                    profileNumberLayout.visibility = View.VISIBLE
                }
            }
        })

        itemNotifications.setOnMenuItemClickListener {
            // TODO: open notification page
            true
        }

        itemSettings.setOnMenuItemClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
            true
        }

        itemShareProfile.setOnMenuItemClickListener {
            // TODO: share profile
            true
        }
    }

    private fun setupSection() {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

        when (viewModel.currentSection.value) {
            ProfileSection.BIO -> fragmentTransaction.replace(profileFrameLayout.id, BioFragment())
            ProfileSection.FAVORITES -> fragmentTransaction.replace(profileFrameLayout.id, FavoritesFragment())
            ProfileSection.STATS -> fragmentTransaction.replace(profileFrameLayout.id, StatsFragment())
            ProfileSection.REVIEWS -> fragmentTransaction.replace(profileFrameLayout.id, ReviewsFragment())
        }

        profileSectionMap.forEach {
            if (it.key == viewModel.currentSection.value) {
                it.value.first.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeSecondaryColor))
                it.value.second.setTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeSecondaryColor))
            } else {
                it.value.first.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
                it.value.second.setTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
            }
        }

        fragmentTransaction.commit()
    }
}
