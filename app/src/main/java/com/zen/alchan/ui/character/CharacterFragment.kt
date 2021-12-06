package com.zen.alchan.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.databinding.FragmentCharacterBinding
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class CharacterFragment : BaseFragment<FragmentCharacterBinding, CharacterViewModel>() {

    override val viewModel: CharacterViewModel by viewModel()

    private var scaleUpAnimation: Animation? = null
    private var scaleDownAnimation: Animation? = null

    private var characterAdapter: CharacterRvAdapter? = null

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

            characterRecyclerView.addItemDecoration(SpaceItemDecoration(top = resources.getDimensionPixelSize(R.dimen.marginFar)))
            assignAdapter(AppSetting())

            characterAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                characterSwipeRefresh.isEnabled = verticalOffset == 0

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
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.characterSwipeRefresh.isRefreshing = it
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
                binding.characterMediaText.text = it.toString()
            },
            viewModel.favoritesCount.subscribe {
                binding.characterFavoritesText.text = it.toString()
            },
            viewModel.characterItemList.subscribe {
                characterAdapter?.updateData(it)
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

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        characterAdapter = null
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