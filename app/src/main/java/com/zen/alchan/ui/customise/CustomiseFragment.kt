package com.zen.alchan.ui.customise

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentCustomiseBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.getString
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class CustomiseFragment : BaseFragment<FragmentCustomiseBinding, CustomiseViewModel>() {

    override val viewModel: CustomiseViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedCustomiseViewModel>()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCustomiseBinding {
        return FragmentCustomiseBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.mediaType = MediaType.valueOf(it.getString(MEDIA_TYPE) ?: MediaType.ANIME.name)
        }
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.customise_list))
            customiseApplyLayout.positiveButton.text = getString(R.string.apply)
            customiseApplyLayout.negativeButton.text = getString(R.string.reset_to_default)

            customiseSelectedListLayout.clicks {
                viewModel.loadListTypes()
            }

            customiseApplyLayout.positiveButton.clicks {
                viewModel.saveCurrentListStyle()
            }

            customiseApplyLayout.negativeButton.clicks {
                viewModel.resetCurrentListStyle()
            }
        }
    }

    override fun setUpInsets() {
        binding.apply {
            defaultToolbar.defaultToolbar.applyTopPaddingInsets()
            customiseLayout.applySidePaddingInsets()
            customiseApplyLayout.twoButtonsLayout.applyBottomSidePaddingInsets()
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.listStyle.subscribe {
                sharedViewModel.updateListStyleResult(it)
                goBack()
            },
            viewModel.listType.subscribe {
                binding.customiseSelectedListText.text = it.getString()
            },
            viewModel.primaryColor.subscribe {
                binding.customisePrimaryColorIcon.setCardBackgroundColor(
                    if (it.data == null)
                        requireContext().getAttrValue(R.attr.themePrimaryColor)
                    else
                        Color.parseColor(it.data)
                )
            },
            viewModel.secondaryColor.subscribe {
                binding.customiseSecondaryColorIcon.setCardBackgroundColor(
                    if (it.data == null)
                        requireContext().getAttrValue(R.attr.themeSecondaryColor)
                    else
                        Color.parseColor(it.data)
                )
            },
            viewModel.negativeColor.subscribe {
                binding.customiseNegativeColorIcon.setCardBackgroundColor(
                    if (it.data == null)
                        requireContext().getAttrValue(R.attr.themeNegativeColor)
                    else
                        Color.parseColor(it.data)
                )
            },
            viewModel.textColor.subscribe {
                binding.customiseTextColorIcon.setCardBackgroundColor(
                    if (it.data == null)
                        requireContext().getAttrValue(R.attr.themeContentColor)
                    else
                        Color.parseColor(it.data)
                )
            },
            viewModel.cardColor.subscribe {
                binding.customiseCardColorIcon.setCardBackgroundColor(
                    if (it.data == null)
                        requireContext().getAttrValue(R.attr.themeCardColor)
                    else
                        Color.parseColor(it.data)
                )
            },
            viewModel.toolbarColor.subscribe {
                binding.customiseToolbarColorIcon.setCardBackgroundColor(
                    if (it.data == null)
                        requireContext().getAttrValue(R.attr.themeCardColor)
                    else
                        Color.parseColor(it.data)
                )
            },
            viewModel.backgroundColor.subscribe {
                binding.customiseBackgroundColorIcon.setCardBackgroundColor(
                    if (it.data == null)
                        requireContext().getAttrValue(R.attr.themeBackgroundColor)
                    else
                        Color.parseColor(it.data)
                )
            },
            viewModel.floatingButtonColor.subscribe {
                binding.customiseFloatingButtonColorIcon.setCardBackgroundColor(
                    if (it.data == null)
                        requireContext().getAttrValue(R.attr.themeSecondaryColor)
                    else
                        Color.parseColor(it.data)
                )
            },
            viewModel.floatingIconColor.subscribe {
                binding.customiseFloatingIconColorIcon.setCardBackgroundColor(
                    if (it.data == null)
                        requireContext().getAttrValue(R.attr.themeBackgroundColor)
                    else
                        Color.parseColor(it.data)
                )
            },






            viewModel.listTypes.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateListType(data)
                }
            }
        )

        viewModel.loadData()
    }

    companion object {
        private const val MEDIA_TYPE = "mediaType"

        @JvmStatic
        fun newInstance(mediaType: MediaType) =
            CustomiseFragment().apply {
                arguments = Bundle().apply {
                    putString(MEDIA_TYPE, mediaType.name)
                }
            }
    }
}