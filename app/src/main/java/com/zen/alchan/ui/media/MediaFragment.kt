package com.zen.alchan.ui.media

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.FragmentMediaBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.getString
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.profile.ProfileRvAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class MediaFragment : BaseFragment<FragmentMediaBinding, MediaViewModel>() {

    override val viewModel: MediaViewModel by viewModel()

    private var scaleUpAnimation: Animation? = null
    private var scaleDownAnimation: Animation? = null

    private var mediaAdapter: MediaRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaBinding {
        return FragmentMediaBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            scaleUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
            scaleDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)

            mediaRecyclerView.addItemDecoration(SpaceItemDecoration(top = resources.getDimensionPixelSize(R.dimen.marginFar)))
            assignAdapter(AppSetting())

            mediaAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                mediaSwipeRefresh.isEnabled = verticalOffset == 0

                if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                    if (mediaBannerContentLayout.isVisible) {
                        mediaBannerContentLayout.startAnimation(scaleDownAnimation)
                        mediaBannerContentLayout.visibility = View.INVISIBLE
                    }
                } else {
                    if (mediaBannerContentLayout.isInvisible) {
                        mediaBannerContentLayout.startAnimation(scaleUpAnimation)
                        mediaBannerContentLayout.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.mediaSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {

            },
            viewModel.mediaAdapterComponent.subscribe {
                assignAdapter(it)
            },
            viewModel.bannerImage.subscribe {
                ImageUtil.loadImage(requireContext(), it, binding.mediaBannerImage)
            },
            viewModel.coverImage.subscribe {
                ImageUtil.loadImage(requireContext(), it, binding.mediaCoverImage)
            },
            viewModel.mediaTitle.subscribe {
                binding.mediaTitleText.text = it
            },
            viewModel.mediaFormat.subscribe {
                binding.mediaFormatText.text = it.data?.getString()
            },
            viewModel.airingSchedule.subscribe {
                binding.mediaAiringLayout.show(it.data != null)

                it?.data?.let { airingSchedule ->
                    binding.mediaAiringText.text = getString(R.string.ep_x_on_y, airingSchedule.episode, TimeUtil.displayInDayDateTimeFormat(airingSchedule.airingAt))
                }
            },
            viewModel.averageScore.subscribe {
                binding.mediaScoreText.text = "$it%"
            },
            viewModel.favorites.subscribe {
                binding.mediaFavoritesText.text = it.toString()
            },
            viewModel.mediaItemList.subscribe {
                mediaAdapter?.updateData(it)
            }
        )

        arguments?.getInt(MEDIA_ID)?.let {
            viewModel.loadOnce(it)
        }
    }

    private fun assignAdapter(appSetting: AppSetting) {
        mediaAdapter = MediaRvAdapter(requireContext(), listOf(), appSetting, screenWidth, getMediaListener())
        binding.mediaRecyclerView.adapter = mediaAdapter
    }

    private fun getMediaListener(): MediaListener {
        return object : MediaListener {
            override val mediaCharacterListener: MediaListener.MediaCharacterListener = getMediaCharacterListener()
        }
    }

    private fun getMediaCharacterListener(): MediaListener.MediaCharacterListener {
        return object : MediaListener.MediaCharacterListener {
            override fun navigateToMediaCharacter(media: Media) {

            }

            override fun navigateToCharacter(character: Character) {
                navigation.navigateToCharacter(character.id)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaAdapter = null
    }

    companion object {
        private const val MEDIA_ID = "mediaId"

        @JvmStatic
        fun newInstance(mediaId: Int) =
            MediaFragment().apply {
                arguments = Bundle().apply {
                    putInt(MEDIA_ID, mediaId)
                }
            }
    }
}