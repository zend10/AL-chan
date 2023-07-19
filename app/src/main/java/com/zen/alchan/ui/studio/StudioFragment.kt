package com.zen.alchan.ui.studio

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
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.FragmentStudioBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class StudioFragment : BaseFragment<FragmentStudioBinding, StudioViewModel>() {

    override val viewModel: StudioViewModel by viewModel()

    private var scaleUpAnimation: Animation? = null
    private var scaleDownAnimation: Animation? = null
    private var isToolbarExpanded = true

    private var studioAdapter: StudioRvAdapter? = null

    private var menuViewOnAniList: MenuItem? = null
    private var menuCopyLink: MenuItem? = null
    private var appSetting = AppSetting()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStudioBinding {
        return FragmentStudioBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            scaleUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
            scaleDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
            studioAppBarLayout.setExpanded(isToolbarExpanded)

            setUpToolbar(studioToolbar, "", R.drawable.ic_delete) {
                navigation.closeBrowseScreen()
            }
            studioToolbar.inflateMenu(R.menu.menu_view_on_anilist)

            menuViewOnAniList = studioToolbar.menu.findItem(R.id.itemViewOnAniList)
            menuCopyLink = studioToolbar.menu.findItem(R.id.itemCopyLink)

            menuViewOnAniList?.setOnMenuItemClickListener {
                viewModel.loadStudioLink()
                true
            }

            menuCopyLink?.setOnMenuItemClickListener {
                viewModel.copyStudioLink()
                true
            }

            studioRecyclerView.addItemDecoration(SpaceItemDecoration(top = resources.getDimensionPixelSize(R.dimen.marginFar)))
            assignAdapter(appSetting)

            studioAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                isToolbarExpanded = verticalOffset == 0
                studioSwipeRefresh.isEnabled = isToolbarExpanded

                if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                    if (studioBannerContentLayout.isVisible) {
                        studioBannerContentLayout.startAnimation(scaleDownAnimation)
                        studioBannerContentLayout.visibility = View.INVISIBLE
                    }
                } else {
                    if (studioBannerContentLayout.isInvisible) {
                        studioBannerContentLayout.startAnimation(scaleUpAnimation)
                        studioBannerContentLayout.visibility = View.VISIBLE
                    }
                }
            })

            studioSetAsFavoriteButton.clicks {
                viewModel.toggleFavorite()
            }

            studioSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }
        }
    }

    override fun setUpInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.studioCollapsingToolbar, null)
        binding.studioRecyclerView.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.studioSwipeRefresh.isRefreshing = it
            },
            viewModel.isAuthenticated.subscribe {
                binding.studioSetAsFavoriteButton.isEnabled = it

                if (!it) {
                    binding.studioSetAsFavoriteButton.apply {
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
            viewModel.studioAdapterComponent.subscribe {
                appSetting = it
                assignAdapter(it)
            },
            viewModel.studioName.subscribe {
                binding.studioNameText.text = it
            },
            viewModel.mediaCount.subscribe {
                binding.studioMediaText.text = it.getNumberFormatting()
            },
            viewModel.mediaCountVisibility.subscribe {
                binding.studioMediaLayout.show(it)
                binding.studioBarDivider1.show(it)
            },
            viewModel.favoritesCount.subscribe {
                binding.studioFavoritesText.text = it.getNumberFormatting()
            },
            viewModel.isFavorite.subscribe {
                if (it) {
                    binding.studioSetAsFavoriteButton.apply {
                        text = context.getString(R.string.your_favorite)
                        strokeWidth = context.resources.getDimensionPixelSize(R.dimen.lineWidth)
                        strokeColor = ColorStateList.valueOf(context.getAttrValue(R.attr.themePrimaryColor))
                        backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                        setTextColor(context.getAttrValue(R.attr.themePrimaryColor))
                    }
                } else {
                    binding.studioSetAsFavoriteButton.apply {
                        text = context.getString(R.string.set_as_favorite)
                        strokeWidth = 0
                        strokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
                        backgroundTintList = ColorStateList.valueOf(context.getAttrValue(R.attr.themePrimaryColor))
                        setTextColor(context.getAttrValue(R.attr.themeBackgroundColor))
                    }
                }
            },
            viewModel.studioItemList.subscribe {
                studioAdapter?.updateData(it)
            },
            viewModel.studioLink.subscribe {
                navigation.openWebView(it)
            }
        )

        arguments?.getInt(STUDIO_ID)?.let {
            viewModel.loadData(StudioParam(it))
        }
    }

    private fun assignAdapter(appSetting: AppSetting) {
        studioAdapter = StudioRvAdapter(requireContext(), listOf(), appSetting, getStudioListener())
        binding.studioRecyclerView.adapter = studioAdapter
    }

    private fun getStudioListener(): StudioListener {
        return object : StudioListener {
            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }

            override fun navigateToStudioMedia() {
                arguments?.getInt(STUDIO_ID)?.let {
                    navigation.navigateToStudioMedia(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        studioAdapter = null
        menuCopyLink = null
        menuViewOnAniList = null
    }

    companion object {
        private const val STUDIO_ID = "studioId"

        @JvmStatic
        fun newInstance(studioId: Int) =
            StudioFragment().apply {
                arguments = Bundle().apply {
                    putInt(STUDIO_ID, studioId)
                }
            }
    }
}