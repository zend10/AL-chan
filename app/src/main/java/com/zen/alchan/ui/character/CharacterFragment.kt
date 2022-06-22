package com.zen.alchan.ui.character

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.databinding.FragmentCharacterBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class CharacterFragment : BaseFragment<FragmentCharacterBinding, CharacterViewModel>() {

    override val viewModel: CharacterViewModel by viewModel()

    private var scaleUpAnimation: Animation? = null
    private var scaleDownAnimation: Animation? = null
    private var isToolbarExpanded = true

    private var characterAdapter: CharacterRvAdapter? = null

    private var menuViewOnAniList: MenuItem? = null
    private var menuCopyLink: MenuItem? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCharacterBinding {
        return FragmentCharacterBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            scaleUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
            scaleDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
            characterAppBarLayout.setExpanded(isToolbarExpanded)

            setUpToolbar(characterToolbar, "", R.drawable.ic_delete) {
                navigation.closeBrowseScreen()
            }
            characterToolbar.inflateMenu(R.menu.menu_view_on_anilist)

            menuViewOnAniList = characterToolbar.menu.findItem(R.id.itemViewOnAniList)
            menuCopyLink = characterToolbar.menu.findItem(R.id.itemCopyLink)

            menuViewOnAniList?.setOnMenuItemClickListener {
                viewModel.loadCharacterLink()
                true
            }

            menuCopyLink?.setOnMenuItemClickListener {
                viewModel.copyCharacterLink()
                true
            }

            characterRecyclerView.addItemDecoration(SpaceItemDecoration(top = resources.getDimensionPixelSize(R.dimen.marginFar)))
            assignAdapter(AppSetting())

            characterAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                isToolbarExpanded = verticalOffset == 0
                characterSwipeRefresh.isEnabled = isToolbarExpanded

                if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                    if (characterBannerContentLayout.isVisible) {
                        characterBannerContentLayout.startAnimation(scaleDownAnimation)
                        characterBannerContentLayout.visibility = View.INVISIBLE
                    }
                } else {
                    if (characterBannerContentLayout.isInvisible) {
                        characterBannerContentLayout.startAnimation(scaleUpAnimation)
                        characterBannerContentLayout.visibility = View.VISIBLE
                    }
                }
            })

            characterImage.clicks {
                viewModel.loadCharacterImage()
            }

            characterSetAsFavoriteButton.clicks {
                viewModel.toggleFavorite()
            }
        }
    }

    override fun setUpInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.characterCollapsingToolbar, null)
        binding.characterRecyclerView.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.characterSwipeRefresh.isRefreshing = it
            },
            viewModel.isAuthenticated.subscribe {
                binding.characterSetAsFavoriteButton.isEnabled = it

                if (!it) {
                    binding.characterSetAsFavoriteButton.apply {
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
            viewModel.characterAdapterComponent.subscribe {
                assignAdapter(it)
            },
            viewModel.characterImage.subscribe {
                ImageUtil.loadImage(requireContext(), it, binding.characterImage)
            },
            viewModel.characterName.subscribe {
                binding.characterNameText.text = it
            },
            viewModel.characterNativeName.subscribe {
                binding.characterNativeNameText.text = it
            },
            viewModel.mediaCount.subscribe {
                binding.characterMediaText.text = it.getNumberFormatting()
            },
            viewModel.mediaCountVisibility.subscribe {
                binding.characterMediaLayout.show(it)
                binding.characterBarDivider1.show(it)
            },
            viewModel.favoritesCount.subscribe {
                binding.characterFavoritesText.text = it.getNumberFormatting()
            },
            viewModel.isFavorite.subscribe {
                if (it) {
                    binding.characterSetAsFavoriteButton.apply {
                        text = context.getString(R.string.your_favorite)
                        strokeWidth = context.resources.getDimensionPixelSize(R.dimen.lineWidth)
                        strokeColor = ColorStateList.valueOf(context.getAttrValue(R.attr.themePrimaryColor))
                        backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                        setTextColor(context.getAttrValue(R.attr.themePrimaryColor))
                    }
                } else {
                    binding.characterSetAsFavoriteButton.apply {
                        text = context.getString(R.string.set_as_favorite)
                        strokeWidth = 0
                        strokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
                        backgroundTintList = ColorStateList.valueOf(context.getAttrValue(R.attr.themePrimaryColor))
                        setTextColor(context.getAttrValue(R.attr.themeBackgroundColor))
                    }
                }
            },
            viewModel.characterItemList.subscribe {
                characterAdapter?.updateData(it)
            },
            viewModel.staffMedia.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    navigation.navigateToMedia(data.getId())
                }
            },
            viewModel.characterLink.subscribe {
                navigation.openWebView(it)
            },
            viewModel.characterImageForPreview.subscribe {
                ImageUtil.showFullScreenImage(requireContext(), it, binding.characterImage)
            }
        )

        arguments?.getInt(CHARACTER_ID)?.let {
            viewModel.loadData(it)
        }
    }

    private fun assignAdapter(appSetting: AppSetting) {
        characterAdapter = CharacterRvAdapter(requireContext(), listOf(), appSetting, screenWidth, getCharacterListener())
        binding.characterRecyclerView.adapter = characterAdapter
    }

    private fun getCharacterListener(): CharacterListener {
        return object : CharacterListener {
            override fun navigateToStaff(staff: Staff) {
                navigation.navigateToStaff(staff.id)
            }

            override fun showStaffMedia(staff: Staff) {
                viewModel.loadStaffMedia(staff)
            }

            override fun navigateToCharacterMedia() {
                arguments?.let {
                    navigation.navigateToCharacterMedia(it.getInt(CHARACTER_ID))
                }
            }

            override val characterMediaListener: CharacterListener.CharacterMediaListener = object : CharacterListener.CharacterMediaListener {
                override fun navigateToMedia(media: Media) {
                    navigation.navigateToMedia(media.getId())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        characterAdapter = null
        menuCopyLink = null
        menuViewOnAniList = null
    }

    companion object {
        private const val CHARACTER_ID = "characterId"

        @JvmStatic
        fun newInstance(characterId: Int) =
            CharacterFragment().apply {
                arguments = Bundle().apply {
                    putInt(CHARACTER_ID, characterId)
                }
            }
    }
}