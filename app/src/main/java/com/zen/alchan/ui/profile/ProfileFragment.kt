package com.zen.alchan.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.data.response.anilist.Studio
import com.zen.databinding.FragmentProfileBinding
import com.zen.alchan.helper.enums.ActivityListPage
import com.zen.alchan.helper.enums.Favorite
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.applyBottomPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.main.SharedMainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs


class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private var menuItemReviews: MenuItem? = null
    private var menuItemActivities: MenuItem? = null
    private var menuItemAddAsBestFriend: MenuItem? = null
    private var menuItemSettings: MenuItem? = null
    private var menuItemViewOnAniList: MenuItem? = null
    private var menuItemShareProfile: MenuItem? = null
    private var menuItemCopyLink: MenuItem? = null
    private var menuItemReport: MenuItem? = null

    private var scaleUpAnimation: Animation? = null
    private var scaleDownAnimation: Animation? = null
    private var isToolbarExpanded = true

    private var profileAdapter: ProfileRvAdapter? = null
    private var currentUserId = 0
    private var appSetting = AppSetting()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            scaleUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
            scaleDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
            profileAppBarLayout.setExpanded(isToolbarExpanded)

            if (!isViewer()) {
                setUpToolbar(profileToolbar, "", R.drawable.ic_custom_close) {
                    navigation.closeBrowseScreen()
                }
            }

            profileToolbar.menu.apply {
                menuItemReviews = findItem(R.id.itemReviews)
                menuItemActivities = findItem(R.id.itemActivities)
                menuItemAddAsBestFriend = findItem(R.id.itemAddAsBestFriend)
                menuItemSettings = findItem(R.id.itemSettings)
                menuItemViewOnAniList = findItem(R.id.itemViewOnAniList)
                menuItemShareProfile = findItem(R.id.itemShareProfile)
                menuItemCopyLink = findItem(R.id.itemCopyLink)
                menuItemReport = findItem(R.id.itemReport)
            }

            profileToolbar.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_custom_more)

            profileRecyclerView.addItemDecoration(SpaceItemDecoration(top = resources.getDimensionPixelSize(R.dimen.marginFar)))
            assignAdapter(appSetting)

            notLoggedInLayout.goToLoginButton.setOnClickListener {
                viewModel.logout()
                navigation.navigateToLanding()
            }

            menuItemReviews?.setOnMenuItemClickListener {
                doIfUserIdIsLoaded {
                    navigation.navigateToUserReview(currentUserId)
                }
                true
            }

            menuItemActivities?.setOnMenuItemClickListener {
                doIfUserIdIsLoaded {
                    navigation.navigateToActivityList(ActivityListPage.SPECIFIC_USER, currentUserId)
                }
                true
            }

            menuItemSettings?.setOnMenuItemClickListener {
                navigation.navigateToSettings()
                true
            }

            menuItemViewOnAniList?.setOnMenuItemClickListener {
                viewModel.loadProfileUrlForWebView()
                true
            }

            menuItemShareProfile?.setOnMenuItemClickListener {
                viewModel.loadProfileUrlForShareSheet()
                true
            }

            menuItemCopyLink?.setOnMenuItemClickListener {
                viewModel.copyProfileUrl()
                true
            }

            menuItemReport?.setOnMenuItemClickListener {
                dialog.showToast(R.string.please_click_on_the_arrow_icon_on_the_top_left_and_click_report_block)
                viewModel.loadProfileUrlForWebView()
                true
            }

            profileAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                isToolbarExpanded = verticalOffset == 0
                profileSwipeRefresh.isEnabled = isToolbarExpanded

                if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                    if (profileBannerContentLayout.isVisible) {
                        profileBannerContentLayout.startAnimation(scaleDownAnimation)
                        profileBannerContentLayout.visibility = View.INVISIBLE
                    }
                } else {
                    if (profileBannerContentLayout.isInvisible) {
                        profileBannerContentLayout.startAnimation(scaleUpAnimation)
                        profileBannerContentLayout.visibility = View.VISIBLE
                    }
                }
            })

            profileSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            profileAvatarCircleImage.clicks {
                viewModel.loadAvatarUrl(true)
            }

            profileAvatarRectangleImage.clicks {
                viewModel.loadAvatarUrl(false)
            }

            profileBannerImage.clicks {
                viewModel.loadBannerUrl()
            }

            profileFollowButton.clicks {
                viewModel.toggleFollow()
            }

            profileAnimeCountLayout.clicks {
                if (isViewer())
                    sharedViewModel.navigateTo(SharedMainViewModel.Page.ANIME)
                else {
                    doIfUserIdIsLoaded {
                        navigation.navigateToAnimeMediaList(currentUserId)
                    }
                }
            }

            profileMangaCountLayout.clicks {
                if (isViewer())
                    sharedViewModel.navigateTo(SharedMainViewModel.Page.MANGA)
                else {
                    doIfUserIdIsLoaded {
                        navigation.navigateToMangaMediaList(currentUserId)
                    }
                }
            }

            profileFollowingCountLayout.clicks {
                doIfUserIdIsLoaded {
                    navigation.navigateToFollowing(currentUserId)
                }
            }

            profileFollowersCountLayout.clicks {
                doIfUserIdIsLoaded {
                    navigation.navigateToFollowers(currentUserId)
                }
            }
        }
    }

    override fun setUpInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.profileCollapsingToolbar, null)
        if (!isViewer()) {
            binding.profileRecyclerView.applyBottomPaddingInsets()
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.success.subscribe {
                dialog.showToast(it)
            },
            viewModel.loading.subscribe {
                binding.profileSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.profileAdapterComponent.subscribe {
                appSetting = it
                assignAdapter(it)
            },
            viewModel.notLoggedInLayoutVisibility.subscribe {
                binding.notLoggedInLayout.notLoggedInLayout.show(it)
                binding.profileSwipeRefresh.show(!it)
            },
            viewModel.viewerMenuItemVisibility.subscribe {
                menuItemSettings?.isVisible = it
            },
            viewModel.bestFriendVisibility.subscribe {
                menuItemAddAsBestFriend?.isVisible = it
            },
            viewModel.reportMenuItemVisibility.subscribe {
                menuItemReport?.isVisible = it
            },
            viewModel.avatarUrl.subscribe { (avatarUrl, useCircular) ->
                binding.profileAvatarCircleImage.show(useCircular)
                binding.profileAvatarRectangleImage.show(!useCircular)
                if (useCircular) {
                    ImageUtil.loadCircleImage(requireContext(), avatarUrl, binding.profileAvatarCircleImage)
                } else {
                    ImageUtil.loadRectangleImage(requireContext(), avatarUrl, binding.profileAvatarRectangleImage)
                }
            },
            viewModel.bannerUrl.subscribe {
                ImageUtil.loadImage(requireContext(), it, binding.profileBannerImage)
            },
            viewModel.username.subscribe {
                binding.profileUsernameText.text = it
            },
            viewModel.donatorAndModBadge.subscribe { (donatorBadge, modBadge) ->
                binding.profileBadgeLayout.show(donatorBadge != null || modBadge != null)
                binding.profileBadgeSpace.show(donatorBadge != null && modBadge != null)
                binding.profileDonatorCard.show(donatorBadge != null)
                binding.profileDonatorText.text = donatorBadge
                binding.profileModCard.show(modBadge != null)
                binding.profileModText.text = modBadge
            },
            viewModel.followButtonVisibility.subscribe {
                binding.profileFollowButton.show(it)
            },
            viewModel.followButtonText.subscribe {
                binding.profileFollowButton.text = getString(it)
            },
            viewModel.animeCompletedCount.subscribe {
                binding.profileAnimeCountText.text = it.toString()
            },
            viewModel.mangaCompletedCount.subscribe {
                binding.profileMangaCountText.text = it.toString()
            },
            viewModel.followingCount.subscribe {
                binding.profileFollowingCountText.text = it.toString()
            },
            viewModel.followersCount.subscribe {
                binding.profileFollowersCountText.text = it.toString()
            },
            viewModel.profileItemList.subscribe {
                profileAdapter?.updateData(it, true)
            },
            viewModel.profileUrlForWebView.subscribe {
                navigation.openWebView(it)
            },
            viewModel.profileUrlForShareSheet.subscribe {
                dialog.showShareSheet(it)
            },
            viewModel.avatarUrlForPreview.subscribe {
                ImageUtil.showFullScreenImage(
                    requireContext(),
                    it.first,
                    if (it.second) binding.profileAvatarCircleImage else binding.profileAvatarRectangleImage
                )
            },
            viewModel.bannerUrlForPreview.subscribe {
                ImageUtil.showFullScreenImage(
                    requireContext(),
                    it,
                    binding.profileBannerImage
                )
            },
            viewModel.currentUserId.subscribe {
                currentUserId = it
            }
        )

        viewModel.loadData(
            ProfileParam(
                arguments?.getInt(USER_ID)?.let { if (it == 0) null else it },
                arguments?.getString(USERNAME)
            )
        )
    }

    private fun assignAdapter(appSetting: AppSetting) {
        profileAdapter = ProfileRvAdapter(requireContext(), listOf(), appSetting, screenWidth, getProfileListener())
        binding.profileRecyclerView.adapter = profileAdapter
    }

    private fun getProfileListener(): ProfileListener {
        return object : ProfileListener {
            override val statsListener: ProfileListener.StatsListener = getStatsListener()
            override val favoriteMediaListener: ProfileListener.FavoriteMediaListener = getFavoriteMediaListener()
            override val favoriteCharacterListener: ProfileListener.FavoriteCharacterListener = getFavoriteCharacterListener()
            override val favoriteStaffListener: ProfileListener.FavoriteStaffListener = getFavoriteStaffListener()
            override val favoriteStudioListener: ProfileListener.FavoriteStudioListener = getFavoriteStudioListener()
        }
    }

    private fun getStatsListener(): ProfileListener.StatsListener {
        return object : ProfileListener.StatsListener {
            override fun navigateToStatsDetail() {
                doIfUserIdIsLoaded {
                    navigation.navigateToUserStats(currentUserId)
                }
            }
        }
    }

    private fun getFavoriteMediaListener(): ProfileListener.FavoriteMediaListener {
        return object : ProfileListener.FavoriteMediaListener {
            override fun navigateToFavoriteMedia(mediaType: MediaType) {
                val favorite = when (mediaType) {
                    MediaType.ANIME -> Favorite.ANIME
                    MediaType.MANGA -> Favorite.MANGA
                }
                doIfUserIdIsLoaded {
                    navigation.navigateToFavorite(currentUserId, favorite)
                }
            }

            override fun navigateToMedia(media: Media, mediaType: MediaType) {
                navigation.navigateToMedia(media.getId())
            }
        }
    }

    private fun getFavoriteCharacterListener(): ProfileListener.FavoriteCharacterListener {
        return object : ProfileListener.FavoriteCharacterListener {
            override fun navigateToFavoriteCharacter() {
                doIfUserIdIsLoaded {
                    navigation.navigateToFavorite(currentUserId, Favorite.CHARACTERS)
                }
            }

            override fun navigateToCharacter(character: Character) {
                navigation.navigateToCharacter(character.id)
            }
        }
    }

    private fun getFavoriteStaffListener(): ProfileListener.FavoriteStaffListener {
        return object : ProfileListener.FavoriteStaffListener {
            override fun navigateToFavoriteStaff() {
                doIfUserIdIsLoaded {
                    navigation.navigateToFavorite(currentUserId, Favorite.STAFF)
                }
            }

            override fun navigateToStaff(staff: Staff) {
                navigation.navigateToStaff(staff.id)
            }
        }
    }

    private fun getFavoriteStudioListener(): ProfileListener.FavoriteStudioListener {
        return object : ProfileListener.FavoriteStudioListener {
            override fun navigateToFavoriteStudio() {
                doIfUserIdIsLoaded {
                    navigation.navigateToFavorite(currentUserId, Favorite.STUDIOS)
                }
            }

            override fun navigateToStudio(studio: Studio) {
                navigation.navigateToStudio(studio.id)
            }
        }
    }

    private fun isViewer(): Boolean {
        return arguments?.getInt(USER_ID) == 0 && arguments?.getString(USERNAME) == null
    }

    private fun doIfUserIdIsLoaded(action: () -> Unit) {
        if (currentUserId != 0)
            action()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        menuItemReviews = null
        menuItemActivities = null
        menuItemAddAsBestFriend = null
        menuItemSettings = null
        menuItemViewOnAniList = null
        menuItemShareProfile = null
        menuItemCopyLink = null
        menuItemReport = null
        profileAdapter = null
    }

    companion object {
        private const val USER_ID = "userId"
        private const val USERNAME = "username"

        @JvmStatic
        fun newInstance(userId: Int? = null, username: String? = null) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    if (userId != null) putInt(USER_ID, userId)
                    if (username != null) putString(USERNAME, username)
                }
            }
    }
}