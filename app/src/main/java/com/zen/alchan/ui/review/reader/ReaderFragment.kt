package com.zen.alchan.ui.review.reader

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.zen.alchan.R
import com.zen.alchan.data.response.anilist.Review
import com.zen.alchan.databinding.FragmentReaderBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.type.ReviewRating
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.round


class ReaderFragment : BaseFragment<FragmentReaderBinding, ReaderViewModel>() {

    override val viewModel: ReaderViewModel by viewModel()

    private var menuItemViewOnAniList: MenuItem? = null
    private var menuItemCopyLink: MenuItem? = null

    private lateinit var review: Review
    private lateinit var listener: ReaderListener

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReaderBinding {
        return FragmentReaderBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            readerToolbar.let {
                setUpToolbar(it, "")
                menuItemViewOnAniList = it.menu.findItem(R.id.itemViewOnAniList)
                menuItemCopyLink = it.menu.findItem(R.id.itemCopyLink)
            }

            menuItemViewOnAniList?.setOnMenuItemClickListener {
                viewModel.loadReviewLink()
                true
            }

            menuItemCopyLink?.setOnMenuItemClickListener {
                viewModel.copyReviewLink()
                true
            }

            readerTitle.clicks {
                navigation.navigateToMedia(review.mediaId)
            }

            readerUserAvatar.clicks {
                navigation.navigateToUser(review.userId)
            }

            readerUserName.clicks {
                navigation.navigateToUser(review.userId)
            }

            readerLikeCard.clicks {
                viewModel.like()
            }

            readerDislikeCard.clicks {
                viewModel.dislike()
            }
        }
    }

    override fun setUpInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.readerCollapsingToolbar, null)
        binding.readerScrollingLayout.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
            },
            viewModel.success.subscribe {
                dialog.showToast(it)
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.bannerImage.subscribe {
                ImageUtil.loadImage(requireContext(), it, binding.readerBannerImage)
            },
            viewModel.mediaType.subscribe {
                binding.readerMediaType.text = getString(it)
            },
            viewModel.titleAndUsername.subscribe {  (title, username) ->
                binding.readerTitle.text = getString(R.string.review_of_x_by_y, title, username)
            },
            viewModel.summary.subscribe {
                binding.readerSummary.text = it
            },
            viewModel.avatar.subscribe {
                ImageUtil.loadCircleImage(requireContext(), it, binding.readerUserAvatar)
            },
            viewModel.username.subscribe {
                binding.readerUserName.text = it
            },
            viewModel.date.subscribe {
                binding.readerDate.text = it
            },
            viewModel.reviewContent.subscribe {
                MarkdownUtil.applyMarkdown(requireContext(), screenWidth, binding.readerText, it)
            },
            viewModel.score.subscribe {
                binding.readerScore.text = "${it}/100"
                val nearestTen = (round(review.score / 10.0) * 10).toInt()
                binding.readerScoreCard.setCardBackgroundColor(nearestTen.getScoreColor() ?: requireContext().getThemeCardColor())
            },
            viewModel.isLiked.subscribe {
                val (likeIconColor, dislikeIconColor) = when (it.data) {
                    true -> {
                        ContextCompat.getColor(requireContext(), R.color.green) to requireContext().getThemeTextColor()
                    }
                    false -> {
                        requireContext().getThemeTextColor() to ContextCompat.getColor(requireContext(), R.color.red)
                    }
                    null -> {
                        requireContext().getThemeTextColor() to requireContext().getThemeTextColor()
                    }
                }
                binding.readerLikeIcon.imageTintList = ColorStateList.valueOf(likeIconColor)
                binding.readerDislikeIcon.imageTintList = ColorStateList.valueOf(dislikeIconColor)
            },
            viewModel.ratingAndRatingAmount.subscribe { (rating, ratingAmount) ->
                binding.readerLikeCount.text = getString(R.string.x_out_of_y_users_liked_this_review, rating, ratingAmount)
            },
            viewModel.reviewLink.subscribe {
                navigation.openWebView(it)
            },
            viewModel.updatedReview.subscribe {
                review = it
                listener.updateRating(review)
            }
        )

        viewModel.loadData(ReaderParam(review))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        menuItemCopyLink = null
        menuItemViewOnAniList = null
    }

    companion object {
        @JvmStatic
        fun newInstance(review: Review, listener: ReaderListener) = ReaderFragment().apply {
            this.review = review
            this.listener = listener
        }
    }

    interface ReaderListener {
        fun updateRating(review: Review)
    }
}