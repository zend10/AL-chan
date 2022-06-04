package com.zen.alchan.ui.staff

import android.content.res.ColorStateList
import android.graphics.Color
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
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.FragmentStaffBinding
import com.zen.alchan.helper.extensions.applyBottomPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.getAttrValue
import com.zen.alchan.helper.extensions.getNumberFormatting
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class StaffFragment : BaseFragment<FragmentStaffBinding, StaffViewModel>() {

    override val viewModel: StaffViewModel by viewModel()

    private var scaleUpAnimation: Animation? = null
    private var scaleDownAnimation: Animation? = null

    private var staffAdapter: StaffRvAdapter? = null

    private var menuViewOnAniList: MenuItem? = null
    private var menuCopyLink: MenuItem? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStaffBinding {
        return FragmentStaffBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            scaleUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
            scaleDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)

            setUpToolbar(staffToolbar, "", R.drawable.ic_delete) {
                navigation.closeBrowseScreen()
            }
            staffToolbar.inflateMenu(R.menu.menu_view_on_anilist)

            menuViewOnAniList = staffToolbar.menu.findItem(R.id.itemViewOnAniList)
            menuCopyLink = staffToolbar.menu.findItem(R.id.itemCopyLink)

            menuViewOnAniList?.setOnMenuItemClickListener {
                viewModel.loadStaffLink()
                true
            }

            menuCopyLink?.setOnMenuItemClickListener {
                viewModel.copyStaffLink()
                true
            }

            staffRecyclerView.addItemDecoration(SpaceItemDecoration(top = resources.getDimensionPixelSize(R.dimen.marginFar)))
            assignAdapter(AppSetting())

            staffAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                staffSwipeRefresh.isEnabled = verticalOffset == 0

                if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                    if (staffBannerContentLayout.isVisible) {
                        staffBannerContentLayout.startAnimation(scaleDownAnimation)
                        staffBannerContentLayout.visibility = View.INVISIBLE
                    }
                } else {
                    if (staffBannerContentLayout.isInvisible) {
                        staffBannerContentLayout.startAnimation(scaleUpAnimation)
                        staffBannerContentLayout.visibility = View.VISIBLE
                    }
                }
            })

            staffImage.clicks {
                viewModel.loadStaffImage()
            }

            staffSetAsFavoriteButton.clicks {
                viewModel.toggleFavorite()
            }
        }
    }

    override fun setUpInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.staffCollapsingToolbar, null)
        binding.staffRecyclerView.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.staffSwipeRefresh.isRefreshing = it
            },
            viewModel.isAuthenticated.subscribe {
                binding.staffSetAsFavoriteButton.isEnabled = it

                if (!it) {
                    binding.staffSetAsFavoriteButton.apply {
                        text = getString(R.string.please_login)
                        strokeWidth = 0
                        strokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
                        backgroundTintList = ColorStateList.valueOf(context.getAttrValue(R.attr.themeContentTransparentColor))
                        setTextColor(context.getAttrValue(R.attr.themeContentColor))
                    }
                }
            },
            viewModel.success.subscribe {
                dialog.showToast(it)
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.staffAdapterComponent.subscribe {
                assignAdapter(it)
            },
            viewModel.staffImage.subscribe {
                ImageUtil.loadImage(requireContext(), it, binding.staffImage)
            },
            viewModel.staffName.subscribe {
                binding.staffNameText.text = it
            },
            viewModel.favoritesCount.subscribe {
                binding.staffFavoritesText.text = it.getNumberFormatting()
            },
            viewModel.isFavorite.subscribe {
                if (it) {
                    binding.staffSetAsFavoriteButton.apply {
                        text = context.getString(R.string.your_favorite)
                        strokeWidth = context.resources.getDimensionPixelSize(R.dimen.lineWidth)
                        strokeColor = ColorStateList.valueOf(context.getAttrValue(R.attr.themePrimaryColor))
                        backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                        setTextColor(context.getAttrValue(R.attr.themePrimaryColor))
                    }
                } else {
                    binding.staffSetAsFavoriteButton.apply {
                        text = context.getString(R.string.set_as_favorite)
                        strokeWidth = 0
                        strokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
                        backgroundTintList = ColorStateList.valueOf(context.getAttrValue(R.attr.themePrimaryColor))
                        setTextColor(context.getAttrValue(R.attr.themeBackgroundColor))
                    }
                }
            },
            viewModel.staffItemList.subscribe {
                staffAdapter?.updateData(it)
            },
            viewModel.staffLink.subscribe {
                navigation.openWebView(it)
            },
            viewModel.staffImageForPreview.subscribe {
                ImageUtil.showFullScreenImage(requireContext(), it, binding.staffImage)
            }
        )

        arguments?.getInt(STAFF_ID)?.let {
            viewModel.loadData(it)
        }
    }

    private fun assignAdapter(appSetting: AppSetting) {
        staffAdapter = StaffRvAdapter(requireContext(), listOf(), appSetting, screenWidth, getStaffListener())
        binding.staffRecyclerView.adapter = staffAdapter
    }

    private fun getStaffListener(): StaffListener {
        return object : StaffListener {
            override fun navigateToStaffCharacter() {

            }

            override fun navigateToStaffMedia() {

            }

            override val staffCharacterListener: StaffListener.StaffCharacterListener = object : StaffListener.StaffCharacterListener {
                override fun navigateToCharacter(character: Character) {
                    navigation.navigateToCharacter(character.id)
                }
            }

            override val staffMediaListener: StaffListener.StaffMediaListener = object : StaffListener.StaffMediaListener {
                override fun navigateToMedia(media: Media) {
                    navigation.navigateToMedia(media.getId())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        staffAdapter = null
    }

    companion object {
        private const val STAFF_ID = "staffId"

        @JvmStatic
        fun newInstance(staffId: Int) =
            StaffFragment().apply {
                arguments = Bundle().apply {
                    putInt(STAFF_ID, staffId)
                }
            }
    }
}