package com.zen.alchan.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.databinding.FragmentProfileBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs


class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModel()

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

            profileToolbar.menu.apply {
                menuItemActivities = findItem(R.id.itemActivities)
                menuItemNotifications = findItem(R.id.itemNotifications)
                menuItemAddAsBestFriend = findItem(R.id.itemAddAsBestFriend)
                menuItemSettings = findItem(R.id.itemSettings)
                menuItemViewOnAniList = findItem(R.id.itemViewOnAniList)
                menuItemShareProfile = findItem(R.id.itemShareProfile)
                menuItemCopyLink = findItem(R.id.itemCopyLink)
            }

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

            profileAnimeCountLayout.clicks {

            }

            profileMangaCountLayout.clicks {

            }

            profileFollowingCountLayout.clicks {

            }

            profileFollowersCountLayout.clicks {

            }
        }
    }

    override fun setUpInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.profileCollapsingToolbar, null)
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.profileSwipeRefresh.isRefreshing = it
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
                profileAdapter?.updateData(it)
            }
        )

        viewModel.loadData(arguments?.getInt(USER_ID) ?: 0)
    }

    private fun assignAdapter(appSetting: AppSetting) {
        profileAdapter = ProfileRvAdapter(requireContext(), listOf(), appSetting, screenWidth, getProfileListener())
        binding.profileRecyclerView.adapter = profileAdapter
        binding.profileRecyclerView.addItemDecoration(SpaceItemDecoration(top = resources.getDimensionPixelSize(R.dimen.marginFar)))
    }

    private fun getProfileListener(): ProfileListener {
        return object : ProfileListener {
            override val favoriteCharacterListener: ProfileListener.FavoriteCharacterListener = getFavoriteCharacterListener()
        }
    }

    private fun getFavoriteCharacterListener(): ProfileListener.FavoriteCharacterListener {
        return object : ProfileListener.FavoriteCharacterListener {
            override fun navigateToCharacterFavorite() {

            }

            override fun navigateToCharacter(character: Character) {

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