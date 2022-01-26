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
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.data.response.anilist.Studio
import com.zen.alchan.databinding.FragmentProfileBinding
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

    private var menuItemActivities: MenuItem? = null
    private var menuItemNotifications: MenuItem? = null
    private var menuItemAddAsBestFriend: MenuItem? = null
    private var menuItemSettings: MenuItem? = null
    private var menuItemViewOnAniList: MenuItem? = null
    private var menuItemShareProfile: MenuItem? = null
    private var menuItemCopyLink: MenuItem? = null

    private var scaleUpAnimation: Animation? = null
    private var scaleDownAnimation: Animation? = null

    private var profileAdapter: ProfileRvAdapter? = null

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

            if (arguments?.getInt(USER_ID) != 0) {
                setUpToolbar(profileToolbar, "", R.drawable.ic_delete) {
                    navigation.closeBrowseScreen()
                }
            }

            profileToolbar.menu.apply {
                menuItemActivities = findItem(R.id.itemActivities)
                menuItemNotifications = findItem(R.id.itemNotifications)
                menuItemAddAsBestFriend = findItem(R.id.itemAddAsBestFriend)
                menuItemSettings = findItem(R.id.itemSettings)
                menuItemViewOnAniList = findItem(R.id.itemViewOnAniList)
                menuItemShareProfile = findItem(R.id.itemShareProfile)
                menuItemCopyLink = findItem(R.id.itemCopyLink)
            }

            profileToolbar.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_custom_more)

            profileRecyclerView.addItemDecoration(SpaceItemDecoration(top = resources.getDimensionPixelSize(R.dimen.marginFar)))
            assignAdapter(AppSetting())

            notLoggedInLayout.goToLoginButton.setOnClickListener {
                viewModel.logout()
                navigation.navigateToLanding()
            }

            menuItemActivities?.setOnMenuItemClickListener {
                navigation.navigateToActivities()
                true
            }

            menuItemNotifications?.setOnMenuItemClickListener {
                navigation.navigateToNotifications()
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

            profileAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                profileSwipeRefresh.isEnabled = verticalOffset == 0

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
                val userId = arguments?.getInt(USER_ID) ?: 0
                if (userId == 0)
                    sharedViewModel.navigateTo(SharedMainViewModel.Page.ANIME)
                else
                    navigation.navigateToAnimeMediaList(userId)
            }

            profileMangaCountLayout.clicks {
                val userId = arguments?.getInt(USER_ID) ?: 0
                if (userId == 0)
                    sharedViewModel.navigateTo(SharedMainViewModel.Page.MANGA)
                else
                    navigation.navigateToMangaMediaList(userId)
            }

            profileFollowingCountLayout.clicks {
                navigation.navigateToFollowing(arguments?.getInt(USER_ID) ?: 0)
            }

            profileFollowersCountLayout.clicks {
                navigation.navigateToFollowers(arguments?.getInt(USER_ID) ?: 0)
            }
        }
    }

    override fun setUpInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.profileCollapsingToolbar, null)
        if (arguments?.getInt(USER_ID) != 0) {
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
                assignAdapter(it)
            },
            viewModel.notLoggedInLayoutVisibility.subscribe {
                binding.notLoggedInLayout.notLoggedInLayout.show(it)
                binding.profileSwipeRefresh.show(!it)
            },
            viewModel.viewerMenuItemVisibility.subscribe {
                menuItemNotifications?.isVisible = it
                menuItemSettings?.isVisible = it
            },
            viewModel.bestFriendVisibility.subscribe {
                menuItemAddAsBestFriend?.isVisible = it
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
            }
        )

        viewModel.loadData(arguments?.getInt(USER_ID) ?: 0)
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
                navigation.navigateToUserStats(arguments?.getInt(USER_ID) ?: 0)
            }
        }
    }

    private fun getFavoriteMediaListener(): ProfileListener.FavoriteMediaListener {
        return object : ProfileListener.FavoriteMediaListener {
            override fun navigateToFavoriteMedia(mediaType: MediaType) {

            }

            override fun navigateToMedia(media: Media, mediaType: MediaType) {
                navigation.navigateToMedia(media.getId())
            }
        }
    }

    private fun getFavoriteCharacterListener(): ProfileListener.FavoriteCharacterListener {
        return object : ProfileListener.FavoriteCharacterListener {
            override fun navigateToFavoriteCharacter() {

            }

            override fun navigateToCharacter(character: Character) {
                navigation.navigateToCharacter(character.id)
            }
        }
    }

    private fun getFavoriteStaffListener(): ProfileListener.FavoriteStaffListener {
        return object : ProfileListener.FavoriteStaffListener {
            override fun navigateToFavoriteStaff() {

            }

            override fun navigateToStaff(staff: Staff) {
                navigation.navigateToStaff(staff.id)
            }
        }
    }

    private fun getFavoriteStudioListener(): ProfileListener.FavoriteStudioListener {
        return object : ProfileListener.FavoriteStudioListener {
            override fun navigateToFavoriteStudio() {

            }

            override fun navigateToStudio(studio: Studio) {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        menuItemActivities = null
        menuItemNotifications = null
        menuItemAddAsBestFriend = null
        menuItemSettings = null
        menuItemViewOnAniList = null
        menuItemShareProfile = null
        menuItemCopyLink = null
        profileAdapter = null
    }

    companion object {
        private const val USER_ID = "userId"

        @JvmStatic
        fun newInstance(userId: Int = 0) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                }
            }
    }
}