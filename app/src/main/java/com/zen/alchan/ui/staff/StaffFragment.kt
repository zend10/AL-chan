package com.zen.alchan.ui.staff

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
import com.zen.alchan.databinding.FragmentStaffBinding
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
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.staffSwipeRefresh.isRefreshing = it
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
                binding.staffFavoritesText.text = it.toString()
            },
            viewModel.staffItemList.subscribe {
                staffAdapter?.updateData(it)
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

        }
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