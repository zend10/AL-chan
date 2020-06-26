package com.zen.alchan.ui.profile


import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.textview.MaterialTextView
import com.stfalcon.imageviewer.StfalconImageViewer

import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.FollowPage
import com.zen.alchan.helper.enums.ProfileSection
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseMainFragment
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.notification.NotificationActivity
import com.zen.alchan.ui.profile.bio.BioFragment
import com.zen.alchan.ui.profile.favorites.FavoritesFragment
import com.zen.alchan.ui.profile.follows.FollowsActivity
import com.zen.alchan.ui.profile.reviews.UserReviewsFragment
import com.zen.alchan.ui.settings.SettingsActivity
import com.zen.alchan.ui.profile.stats.StatsFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class ProfileFragment : BaseMainFragment() {

    private val viewModel by viewModel<ProfileViewModel>()

    private lateinit var profileSectionMap: HashMap<ProfileSection, Pair<ImageView, TextView>>
    private lateinit var profileFragmentList: List<Fragment>

    private lateinit var scaleUpAnim: Animation
    private lateinit var scaleDownAnim: Animation

    private lateinit var itemActivity: MenuItem
    private lateinit var itemNotifications: MenuItem
    private lateinit var itemSettings: MenuItem
    private lateinit var itemViewInAniList: MenuItem
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

        profileFragmentList = arrayListOf(BioFragment(), FavoritesFragment(), StatsFragment(), UserReviewsFragment())

        scaleUpAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
        scaleDownAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)

        profileToolbar.menu.apply {
            itemActivity = findItem(R.id.itemActivity)
            itemNotifications = findItem(R.id.itemNotifications)
            itemSettings = findItem(R.id.itemSettings)
            itemViewInAniList = findItem(R.id.itemViewOnAniList)
            itemShareProfile = findItem(R.id.itemShareProfile)
        }

        setupObserver()
        initLayout()
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

        viewModel.followersCount.observe(viewLifecycleOwner, Observer {
            profileFollowersCountText.text = it.toString()
        })

        viewModel.followingsCount.observe(viewLifecycleOwner, Observer {
            profileFollowingCountText.text = it.toString()
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
            viewModel.triggerRefreshChildFragments()
        }

        GlideApp.with(this).load(user?.bannerImage).into(profileBannerImage)
        if (user?.bannerImage != null) {
            profileBannerImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(context, arrayOf(user.bannerImage)) { view, image ->
                    GlideApp.with(context!!).load(image).into(view)
                }.withTransitionFrom(profileBannerImage).show(true)
            }
        }

        if (viewModel.circularAvatar) {
            profileAvatarImage.background = ContextCompat.getDrawable(activity!!, R.drawable.shape_oval_transparent)
            if (viewModel.whiteBackgroundAvatar) {
                profileAvatarImage.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.white))
            } else {
                profileAvatarImage.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.transparent))
            }
            GlideApp.with(this).load(user?.avatar?.large).apply(RequestOptions.circleCropTransform()).into(profileAvatarImage)
        } else {
            profileAvatarImage.background = ContextCompat.getDrawable(activity!!, R.drawable.shape_rectangle_transparent)
            profileAvatarImage.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.transparent))
            GlideApp.with(this).load(user?.avatar?.large).into(profileAvatarImage)
        }

        if (user?.avatar?.large != null) {
            profileAvatarImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(context, arrayOf(user.avatar.large)) { view, image ->
                    GlideApp.with(context!!).load(image).into(view)
                }.withTransitionFrom(profileAvatarImage).show(true)
            }
        }

        profileUsernameText.text = user?.name ?: ""

        if (!user?.moderatorStatus.isNullOrBlank()) {
            modCard.visibility = View.VISIBLE
            modText.text = user?.moderatorStatus?.split(" ")?.map { it.toLowerCase().capitalize() }?.joinToString(" ")
        } else {
            modCard.visibility = View.GONE
        }

        if (user?.donatorTier != null && user.donatorTier != 0) {
            donatorCard.visibility = View.VISIBLE
            donatorText.text = user.donatorBadge
        } else {
            donatorCard.visibility = View.GONE
        }

        if (!user?.moderatorStatus.isNullOrBlank() && user?.donatorTier != null && user.donatorTier != 0) {
            badgeSpace.visibility = View.VISIBLE
        } else {
            badgeSpace.visibility = View.GONE
        }

        profileAnimeCountText.text = user?.statistics?.anime?.count?.toString() ?: "0"
        profileMangaCountText.text = user?.statistics?.manga?.count?.toString() ?: "0"
        profileFollowersCountText.text = viewModel.followersCount.value?.toString() ?: "0"
        profileFollowingCountText.text = viewModel.followingsCount.value?.toString() ?: "0"

        profileAnimeCountLayout.setOnClickListener {
            listener?.changeMenu(R.id.itemAnime)
        }
        profileMangaCountLayout.setOnClickListener {
            listener?.changeMenu(R.id.itemManga)
        }
        profileFollowingCountLayout.setOnClickListener {
            val intent = Intent(activity, FollowsActivity::class.java)
            intent.putExtra(FollowsActivity.START_POSITION, FollowPage.FOLLOWING.ordinal)
            startActivity(intent)
        }
        profileFollowersCountLayout.setOnClickListener {
            val intent = Intent(activity, FollowsActivity::class.java)
            intent.putExtra(FollowsActivity.START_POSITION, FollowPage.FOLLOWERS.ordinal)
            startActivity(intent)
        }

        profileBioLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.BIO) }
        profileFavoritesLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.FAVORITES) }
        profileStatsLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.STATS) }
        profileReviewsLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.REVIEWS) }

        if (profileViewPager.adapter == null) {
            profileViewPager.setPagingEnabled(false)
            profileViewPager.offscreenPageLimit = profileSectionMap.size
            profileViewPager.adapter = ProfileViewPagerAdapter(childFragmentManager, profileFragmentList)
        }

        viewModel.setProfileSection(viewModel.currentSection.value ?: ProfileSection.BIO)

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

        itemActivity.setOnMenuItemClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.ACTIVITY_LIST.name)
            intent.putExtra(BrowseActivity.LOAD_ID, user?.id)
            startActivity(intent)
            true
        }

        val notificationActionView = itemNotifications.actionView
        val badgeCount = notificationActionView.findViewById<MaterialTextView>(R.id.notification_badge)
        if (user?.unreadNotificationCount != null && user.unreadNotificationCount != 0) {
            if (user.unreadNotificationCount!! > 99) {
                badgeCount.text = "99+"
            } else {
                badgeCount.text = user.unreadNotificationCount?.toString()
            }
            badgeCount.visibility = View.VISIBLE
        } else {
            badgeCount.visibility = View.GONE
        }

        notificationActionView.setOnClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
        }

        itemNotifications.setOnMenuItemClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
            true
        }

        itemSettings.setOnMenuItemClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
            true
        }

        itemViewInAniList.setOnMenuItemClickListener {
            if (user?.siteUrl == null) {
                DialogUtility.showToast(activity, R.string.some_data_has_not_been_retrieved)
            } else {
                CustomTabsIntent.Builder().build().launchUrl(activity!!, Uri.parse(user.siteUrl))
            }
            true
        }

        itemShareProfile.setOnMenuItemClickListener {
            if (user?.siteUrl == null) {
                DialogUtility.showToast(activity, R.string.some_data_has_not_been_retrieved)
            } else {
                AndroidUtility.copyToClipboard(activity, user.siteUrl)
                DialogUtility.showToast(activity, R.string.link_copied)
            }
            true
        }
    }

    private fun setupSection() {
        profileSectionMap.forEach {
            if (it.key == viewModel.currentSection.value) {
                it.value.first.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeSecondaryColor))
                it.value.second.setTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeSecondaryColor))
            } else {
                it.value.first.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
                it.value.second.setTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
            }
        }

        profileViewPager.currentItem = when (viewModel.currentSection.value) {
            ProfileSection.BIO -> 0
            ProfileSection.FAVORITES -> 1
            ProfileSection.STATS -> 2
            ProfileSection.REVIEWS -> 3
            else -> 0
        }
    }
}
