package com.zen.alchan.ui.browse.user


import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.stfalcon.imageviewer.StfalconImageViewer

import com.zen.alchan.R
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ProfileSection
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.updateTopPadding
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.profile.ProfileViewPagerAdapter
import com.zen.alchan.ui.profile.bio.BioFragment
import com.zen.alchan.ui.profile.favorites.FavoritesFragment
import com.zen.alchan.ui.profile.reviews.UserReviewsFragment
import com.zen.alchan.ui.profile.stats.StatsFragment
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class UserFragment : BaseFragment() {

    private val viewModel by viewModel<UserViewModel>()

    private lateinit var profileSectionMap: HashMap<ProfileSection, Pair<ImageView, TextView>>
    private lateinit var profileFragmentList: ArrayList<Fragment>

    private lateinit var scaleUpAnim: Animation
    private lateinit var scaleDownAnim: Animation

    private var itemActivity: MenuItem? = null
    private var itemBestFriend: MenuItem? = null
    private var itemViewInAniList: MenuItem? = null
    private var itemCopyLink: MenuItem? = null

    companion object {
        const val USER_ID = "userId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userToolbar.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateTopPadding(windowInsets, initialPadding)
        }

        viewModel.userId = arguments?.getInt(USER_ID)

        profileSectionMap = hashMapOf(
            Pair(ProfileSection.BIO, Pair(userBioIcon, userBioText)),
            Pair(ProfileSection.FAVORITES, Pair(userFavoritesIcon, userFavoritesText)),
            Pair(ProfileSection.STATS, Pair(userStatsIcon, userStatsText)),
            Pair(ProfileSection.REVIEWS, Pair(userReviewsIcon, userReviewsText))
        )

        profileFragmentList = arrayListOf(BioFragment(), FavoritesFragment(), StatsFragment(), UserReviewsFragment())

        scaleUpAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
        scaleDownAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)

        userToolbar.setNavigationOnClickListener { activity?.finish() }
        userToolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.custom_close_icon)
        userToolbar.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.custom_more_icon)

        userToolbar.menu.apply {
            itemActivity = findItem(R.id.itemActivity)
            itemBestFriend = findItem(R.id.itemBestFriend)
            itemViewInAniList = findItem(R.id.itemViewOnAniList)
            itemCopyLink = findItem(R.id.itemShareProfile)
        }

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.currentSection.observe(viewLifecycleOwner, Observer {
            setupSection()
        })

        viewModel.userData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.currentIsFollowing = it.user?.isFollowing
                if (viewModel.isBestFriend) {
                    viewModel.handleBestFriend(true)
                }
                initLayout()
            }
        })

        viewModel.followersCount.observe(viewLifecycleOwner, Observer {
            userFollowersCountText.text = it.toString()
        })

        viewModel.followingsCount.observe(viewLifecycleOwner, Observer {
            userFollowingCountText.text = it.toString()
        })

        viewModel.userDataResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> {
                    userRefreshLayout.isRefreshing = false
                    loadingLayout.visibility = View.VISIBLE
                }
                ResponseStatus.SUCCESS -> loadingLayout.visibility = View.GONE
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.toggleFollowingResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    viewModel.refreshFollowingCount()
                    viewModel.currentIsFollowing = it.data?.toggleFollow?.isFollowing
                    handleFollowButton()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.animeScoreCollectionResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> userAnimeAffinityIcon.visibility = View.GONE
                ResponseStatus.SUCCESS -> {
                    userAnimeAffinityIcon.visibility = View.VISIBLE
                    val affinity = viewModel.calculateAffinity(it.data)
                    viewModel.animeAffinityText = if (affinity != null) {
                        getString(R.string.shared_anime_affinity, affinity.first, String.format("%.2f%%", affinity.second))
                    } else {
                        getString(R.string.not_enough_shared_anime_scores_to_compute_affinity)
                    }
                }
                ResponseStatus.ERROR -> {
                    userAnimeAffinityIcon.visibility = View.VISIBLE
                    viewModel.animeAffinityText = getString(R.string.failed_to_calculate_anime_affinity)
                }
            }
        })

        viewModel.mangaScoreCollectionResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> userMangaAffinityIcon.visibility = View.GONE
                ResponseStatus.SUCCESS -> {
                    userMangaAffinityIcon.visibility = View.VISIBLE
                    val affinity = viewModel.calculateAffinity(it.data)
                    viewModel.mangaAffinityText = if (affinity != null) {
                        getString(R.string.shared_manga_affinity, affinity.first, String.format("%.2f%%", affinity.second))
                    } else {
                        getString(R.string.not_enough_shared_manga_scores_to_compute_affinity)
                    }
                }
                ResponseStatus.ERROR -> {
                    userMangaAffinityIcon.visibility = View.VISIBLE
                    viewModel.mangaAffinityText = getString(R.string.failed_to_calculate_manga_affinity)
                }
            }
        })

        viewModel.initData()
    }

    private fun initLayout() {
        val user = viewModel.userData.value?.user

        userRefreshLayout.setOnRefreshListener {
            userRefreshLayout.isRefreshing = false
            viewModel.retrieveUserData()
            viewModel.triggerRefreshChildFragments()
        }

        GlideApp.with(this).load(user?.bannerImage).into(userBannerImage)
        if (user?.bannerImage != null) {
            userBannerImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(context, arrayOf(user.bannerImage)) { view, image ->
                    GlideApp.with(context!!).load(image).into(view)
                }.withTransitionFrom(userBannerImage).withHiddenStatusBar(false).show(true)
            }
        }

        if (viewModel.circularAvatar) {
            userAvatarImage.background = ContextCompat.getDrawable(activity!!, R.drawable.shape_oval_transparent)
            if (viewModel.whiteBackgroundAvatar) {
                userAvatarImage.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.white))
            } else {
                userAvatarImage.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.transparent))
            }
            GlideApp.with(this).load(user?.avatar?.large).apply(RequestOptions.circleCropTransform()).into(userAvatarImage)
        } else {
            userAvatarImage.background = ContextCompat.getDrawable(activity!!, R.drawable.shape_rectangle_transparent)
            userAvatarImage.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.transparent))
            GlideApp.with(this).load(user?.avatar?.large).into(userAvatarImage)
        }

        if (user?.avatar?.large != null) {
            userAvatarImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(context, arrayOf(user.avatar.large)) { view, image ->
                    GlideApp.with(context!!).load(image).into(view)
                }.withTransitionFrom(userAvatarImage).withHiddenStatusBar(false).show(true)
            }
        }

        userUsernameText.text = user?.name ?: ""

        badgeLayout.visibility = View.GONE

        if (!user?.moderatorStatus.isNullOrBlank()) {
            badgeLayout.visibility = View.VISIBLE
            modCard.visibility = View.VISIBLE
            modText.text = user?.moderatorStatus?.split(" ")?.map { it.toLowerCase().capitalize() }?.joinToString(" ")
        } else {
            modCard.visibility = View.GONE
        }

        if (user?.donatorTier != null && user.donatorTier != 0) {
            badgeLayout.visibility = View.VISIBLE
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

        userAnimeCountText.text = user?.statistics?.anime?.count?.toString() ?: "0"
        userMangaCountText.text = user?.statistics?.manga?.count?.toString() ?: "0"
        userFollowersCountText.text = viewModel.followersCount.value?.toString() ?: "0"
        userFollowingCountText.text = viewModel.followingsCount.value?.toString() ?: "0"

        userAnimeCountLayout.setOnClickListener {
            if (viewModel.userId != null) listener?.changeFragment(BrowsePage.USER_ANIME_LIST, viewModel.userId!!)
        }
        userMangaCountLayout.setOnClickListener {
            if (viewModel.userId != null) listener?.changeFragment(BrowsePage.USER_MANGA_LIST, viewModel.userId!!)
        }
        userFollowingCountLayout.setOnClickListener {
            if (viewModel.userId != null) listener?.changeFragment(BrowsePage.USER_FOLLOWING_LIST, viewModel.userId!!)
        }
        userFollowersCountLayout.setOnClickListener {
            if (viewModel.userId != null) listener?.changeFragment(BrowsePage.USER_FOLLOWERS_LIST, viewModel.userId!!)
        }

        if (viewModel.userId != viewModel.currentUserId) {
            userFollowButton.visibility = View.VISIBLE

            handleFollowButton()

            userFollowButton.setOnClickListener {
                DialogUtility.showOptionDialog(
                    requireActivity(),
                    if (viewModel.currentIsFollowing != true) R.string.follow_this_user else R.string.unfollow_this_user,
                    if (viewModel.currentIsFollowing != true) R.string.are_you_sure_you_want_to_follow_this_user else R.string.are_you_sure_you_want_to_shatter_this_friendship,
                    if (viewModel.currentIsFollowing != true) R.string.follow else R.string.unfollow,
                    {
                        viewModel.toggleFollow()
                    },
                    R.string.cancel,
                    { }
                )
            }
        } else {
            userFollowButton.visibility = View.GONE
        }

        userBioLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.BIO) }
        userFavoritesLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.FAVORITES) }
        userStatsLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.STATS) }
        userReviewsLayout.setOnClickListener { viewModel.setProfileSection(ProfileSection.REVIEWS) }

        if (userViewPager.adapter == null) {
            userViewPager.setPagingEnabled(false)
            userViewPager.offscreenPageLimit = profileSectionMap.size
            userViewPager.adapter = ProfileViewPagerAdapter(childFragmentManager, profileFragmentList, viewModel.userId)
        }

        viewModel.setProfileSection(viewModel.currentSection.value ?: ProfileSection.BIO)

        userAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            // disable refresh when toolbar is not fully expanded
            userRefreshLayout?.isEnabled = verticalOffset == 0

            // 50 is magic number gotten from trial and error
            if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                if (userNumberLayout?.isVisible == true) {
                    userNumberLayout?.startAnimation(scaleDownAnim)
                    userNumberLayout?.visibility = View.INVISIBLE
                }
            } else {
                if (userNumberLayout?.isInvisible == true) {
                    userNumberLayout?.startAnimation(scaleUpAnim)
                    userNumberLayout?.visibility = View.VISIBLE
                }
            }
        })

        itemActivity?.isVisible = viewModel.enableSocial

        itemActivity?.setOnMenuItemClickListener {
            if (user?.id != null) listener?.changeFragment(BrowsePage.ACTIVITY_LIST, user.id, user.name)
            true
        }

        itemBestFriend?.isVisible = viewModel.currentUserId != viewModel.userId

        if (viewModel.isBestFriend) {
            itemBestFriend?.title = getString(R.string.remove_from_best_friend)
        } else {
            itemBestFriend?.title = getString(R.string.add_as_best_friend)
        }

        itemBestFriend?.setOnMenuItemClickListener {
            val title: Int
            val message: Int
            val positiveButton: Int

            if (viewModel.isBestFriend) {
                title = R.string.remove_from_best_friend
                message = R.string.are_you_sure_you_want_to_remove_this_user_from_your_best_friend
                positiveButton = R.string.remove
            } else {
                title = R.string.add_as_best_friend
                message = R.string.are_you_sure_you_want_to_add_this_user_as_your_best_friend
                positiveButton = R.string.add
            }

            DialogUtility.showOptionDialog(
                requireActivity(),
                title,
                message,
                positiveButton,
                {
                    viewModel.handleBestFriend()
                    DialogUtility.showToast(activity, R.string.change_saved)

                    if (viewModel.isBestFriend) {
                        itemBestFriend?.title = getString(R.string.remove_from_best_friend)
                    } else {
                        itemBestFriend?.title = getString(R.string.add_as_best_friend)
                    }
                },
                R.string.cancel,
                { }
            )
            true
        }

        itemViewInAniList?.setOnMenuItemClickListener {
            if (user?.siteUrl == null) {
                DialogUtility.showToast(activity, R.string.some_data_has_not_been_retrieved)
            } else {
                CustomTabsIntent.Builder().build().launchUrl(activity!!, Uri.parse(user.siteUrl))
            }
            true
        }

        itemCopyLink?.setOnMenuItemClickListener {
            if (user?.siteUrl == null) {
                DialogUtility.showToast(activity, R.string.some_data_has_not_been_retrieved)
            } else {
                AndroidUtility.copyToClipboard(activity, user.siteUrl)
                DialogUtility.showToast(activity, R.string.link_copied)
            }
            true
        }

        userAnimeAffinityIcon.setOnClickListener {
            createTooltip(it, viewModel.animeAffinityText)
        }

        userMangaAffinityIcon.setOnClickListener {
            createTooltip(it, viewModel.mangaAffinityText)
        }

        if (viewModel.currentUserId != viewModel.userId) {
            userAnimeAffinityIcon.visibility = View.GONE
            userMangaAffinityIcon.visibility = View.GONE
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

        userViewPager.currentItem = when (viewModel.currentSection.value) {
            ProfileSection.BIO -> 0
            ProfileSection.FAVORITES -> 1
            ProfileSection.STATS -> 2
            ProfileSection.REVIEWS -> 3
            else -> 0
        }
    }

    private fun handleFollowButton() {
        val user = viewModel.userData.value?.user

        if (viewModel.currentIsFollowing == true && user?.isFollower == true) {
            userFollowButton.text = getString(R.string.mutual)
        } else if (viewModel.currentIsFollowing == true && user?.isFollower == false) {
            userFollowButton.text = getString(R.string.following)
        } else if (viewModel.currentIsFollowing == false && user?.isFollower == true) {
            userFollowButton.text = getString(R.string.follows_you)
        } else {
            userFollowButton.text = getString(R.string.follow)
        }
    }

    private fun createTooltip(view: View, text: String) {
        val tooltip = Tooltip.Builder(requireContext())
            .anchor(view, 0, 0, false)
            .maxWidth(AndroidUtility.getScreenWidth(requireActivity()) / 2)
            .typeface(ResourcesCompat.getFont(requireContext(), R.font.sourcesanspro_regular))
            .styleId(R.style.TooltipAltStyle)
            .text(text)
            .arrow(true)
            .showDuration(2000)
            .create()

        tooltip.show(view, Tooltip.Gravity.RIGHT, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileSectionMap.clear()
        profileFragmentList.clear()
        itemActivity = null
        itemBestFriend = null
        itemViewInAniList = null
        itemCopyLink = null
    }
}
