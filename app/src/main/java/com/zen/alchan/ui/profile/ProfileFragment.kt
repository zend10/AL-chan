package com.zen.alchan.ui.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.textview.MaterialTextView
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.getAttrValue
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.activity.ActivityFragment
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.bio.BioFragment
import com.zen.alchan.ui.favorite.FavoriteFragment
import com.zen.alchan.ui.review.ReviewFragment
import com.zen.alchan.ui.stats.StatsFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_not_logged_in.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private val viewModel by viewModel<ProfileViewModel>()
    private val sharedViewModel by sharedViewModel<SharedProfileViewModel>()

    private var fragments: List<Fragment>? = null
    private var cardMenu: List<Pair<AppCompatImageView, MaterialTextView>>? = null

    private var menuItemActivities: MenuItem? = null
    private var menuItemNotifications: MenuItem? = null
    private var menuItemAddAsBestFriend: MenuItem? = null
    private var menuItemSettings: MenuItem? = null
    private var menuItemViewOnAniList: MenuItem? = null
    private var menuItemShareProfile: MenuItem? = null
    private var menuItemCopyLink: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sharedViewModel.userId = it.getInt(USER_ID)
        }
    }

    override fun setUpLayout() {
        profileToolbar.menu.apply {
            menuItemActivities = findItem(R.id.itemActivities)
            menuItemNotifications = findItem(R.id.itemNotifications)
            menuItemAddAsBestFriend = findItem(R.id.itemAddAsBestFriend)
            menuItemSettings = findItem(R.id.itemSettings)
            menuItemViewOnAniList = findItem(R.id.itemViewOnAniList)
            menuItemShareProfile = findItem(R.id.itemShareProfile)
            menuItemCopyLink = findItem(R.id.itemCopyLink)
        }

        fragments = listOf(
            BioFragment.newInstance(),
            FavoriteFragment.newInstance(),
            StatsFragment.newInstance(),
            ReviewFragment.newInstance(true)
        )

        cardMenu = listOf(
            profileBioIcon to profileBioText,
            profileFavoritesIcon to profileFavoritesText,
            profileStatsIcon to profileStatsText,
            profileReviewsIcon to profileReviewsText
        )

        profileViewPager.adapter = ProfileViewPagerAdapter(this, fragments ?: listOf())

        profileViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val selectedColor = ColorStateList.valueOf(requireContext().getAttrValue(R.attr.themeSecondaryColor))
                val unselectedColor = ColorStateList.valueOf(requireContext().getAttrValue(R.attr.themeContentColor))

                cardMenu?.toList()?.forEachIndexed { index, cardItem ->
                    val cardIcon = cardItem.first
                    val cardText = cardItem.second

                    if (index == position) {
                        cardIcon.imageTintList = selectedColor
                        cardText.setTextColor(selectedColor)
                    } else {
                        cardIcon.imageTintList = unselectedColor
                        cardText.setTextColor(unselectedColor)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    viewModel.setCurrentPage(SharedProfileViewModel.Page.values()[profileViewPager.currentItem])
                }
            }
        })

        profileBioLayout.setOnClickListener {
            viewModel.setCurrentPage(SharedProfileViewModel.Page.BIO)
        }

        profileFavoritesLayout.setOnClickListener {
            viewModel.setCurrentPage(SharedProfileViewModel.Page.FAVORITE)
        }

        profileStatsLayout.setOnClickListener {
            viewModel.setCurrentPage(SharedProfileViewModel.Page.STATS)
        }

        profileReviewsLayout.setOnClickListener {
            viewModel.setCurrentPage(SharedProfileViewModel.Page.REVIEW)
        }

        goToLoginButton.setOnClickListener {
            viewModel.logoutAsGuest()
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
    }

    override fun setUpInsets() {
        profileHeaderGap.applyTopPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.isAuthenticated.subscribe {
                notLoggedInLayout.show(!it)
            }
        )

        disposables.add(
            viewModel.currentPage.subscribe {
                profileViewPager.setCurrentItem(it.ordinal, true)
            }
        )

        disposables.add(
            sharedViewModel.isViewerProfile.subscribe {
                showToolbarMenu(it)
            }
        )

        disposables.add(
            sharedViewModel.user.subscribe {
                profileUsernameText.text = it.name
                ImageUtil.loadImage(requireContext(), it.avatar.large, profileAvatarImage)
                ImageUtil.loadImage(requireContext(), it.bannerImage, profileBannerImage)
            }
        )

        disposables.add(
            sharedViewModel.animeCount.subscribe {
                profileAnimeCountText.text = it.toString()
            }
        )

        disposables.add(
            sharedViewModel.mangaCount.subscribe {
                profileMangaCountText.text = it.toString()
            }
        )

        disposables.add(
            sharedViewModel.followingCount.subscribe {
                profileFollowingCountText.text = it.toString()
            }
        )

        disposables.add(
            sharedViewModel.followersCount.subscribe {
                profileFollowersCountText.text = it.toString()
            }
        )

        sharedViewModel.loadData()
    }

    private fun showToolbarMenu(isViewerProfile: Boolean) {
        menuItemNotifications?.isVisible = isViewerProfile
        menuItemAddAsBestFriend?.isVisible = !isViewerProfile
        menuItemSettings?.isVisible = isViewerProfile
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragments = null
        cardMenu = null
        menuItemActivities = null
        menuItemNotifications = null
        menuItemAddAsBestFriend = null
        menuItemSettings = null
        menuItemViewOnAniList = null
        menuItemShareProfile = null
        menuItemCopyLink = null
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