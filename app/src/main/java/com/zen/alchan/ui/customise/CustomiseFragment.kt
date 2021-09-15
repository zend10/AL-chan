package com.zen.alchan.ui.customise

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
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

            customisePrimaryColorIcon.clicks {
                viewModel.loadPrimaryColor()
            }

            customiseSecondaryColorIcon.clicks {
                viewModel.loadSecondaryColor()
            }

            customiseNegativeColorIcon.clicks {
                viewModel.loadNegativeColor()
            }

            customiseTextColorIcon.clicks {
                viewModel.loadTextColor()
            }

            customiseCardColorIcon.clicks {
                viewModel.loadCardColor()
            }

            customiseToolbarColorIcon.clicks {
                viewModel.loadToolbarColor()
            }

            customiseBackgroundColorIcon.clicks {
                viewModel.loadBackgroundColor()
            }

            customiseFloatingButtonColorIcon.clicks {
                viewModel.loadFloatingButtonColor()
            }

            customiseFloatingIconColorIcon.clicks {
                viewModel.loadFloatingIconColor()
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
                    getColorFromHex(it.data, requireContext().getThemePrimaryColor())
                )
            },
            viewModel.secondaryColor.subscribe {
                binding.customiseSecondaryColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, requireContext().getThemeSecondaryColor())
                )
            },
            viewModel.negativeColor.subscribe {
                binding.customiseNegativeColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, requireContext().getThemeNegativeColor())
                )
            },
            viewModel.textColor.subscribe {
                binding.customiseTextColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, requireContext().getThemeTextColor())
                )
            },
            viewModel.cardColor.subscribe {
                binding.customiseCardColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, requireContext().getThemeCardColor())
                )
            },
            viewModel.toolbarColor.subscribe {
                binding.customiseToolbarColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, requireContext().getThemeToolbarColor())
                )
            },
            viewModel.backgroundColor.subscribe {
                binding.customiseBackgroundColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, requireContext().getThemeBackgroundColor())
                )
            },
            viewModel.floatingButtonColor.subscribe {
                binding.customiseFloatingButtonColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, requireContext().getThemeFloatingButtonColor())
                )
            },
            viewModel.floatingIconColor.subscribe {
                binding.customiseFloatingIconColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, requireContext().getThemeFloatingIconColor())
                )
            },






            viewModel.listTypes.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateListType(data)
                }
            },
            viewModel.primaryColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, requireContext().getThemePrimaryColor()), it.second) { hexColor ->
                    viewModel.updatePrimaryColor(hexColor)
                }
            },
            viewModel.secondaryColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, requireContext().getThemeSecondaryColor()), it.second) { hexColor ->
                    viewModel.updateSecondaryColor(hexColor)
                }
            },
            viewModel.negativeColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, requireContext().getThemeNegativeColor()), it.second) { hexColor ->
                    viewModel.updateNegativeColor(hexColor)
                }
            },
            viewModel.textColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, requireContext().getThemeTextColor()), it.second) { hexColor ->
                    viewModel.updateTextColor(hexColor)
                }
            },
            viewModel.cardColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, requireContext().getThemeCardColor()), it.second) { hexColor ->
                    viewModel.updateCardColor(hexColor)
                }
            },
            viewModel.toolbarColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, requireContext().getThemeToolbarColor()), it.second) { hexColor ->
                    viewModel.updateToolbarColor(hexColor)
                }
            },
            viewModel.backgroundColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, requireContext().getThemeBackgroundColor()), it.second) { hexColor ->
                    viewModel.updateBackgroundColor(hexColor)
                }
            },
            viewModel.floatingButtonColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, requireContext().getThemeFloatingButtonColor()), it.second) { hexColor ->
                    viewModel.updateFloatingButtonColor(hexColor)
                }
            },
            viewModel.floatingIconColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, requireContext().getThemeFloatingIconColor()), it.second) { hexColor ->
                    viewModel.updateFloatingIconColor(hexColor)
                }
            }
        )

        viewModel.loadData()
    }

    private fun showColorPickerDialog(@ColorInt currentColor: Int, hasAlpha: Boolean, action: (hexColor: String) -> Unit) {
        val builder = ColorPickerDialog.newBuilder()

        builder.setColor(currentColor)
        builder.setDialogTitle(R.string.select_a_color)
        builder.setSelectedButtonText(R.string.select)
        builder.setAllowCustom(true)
        builder.setAllowPresets(false)
        builder.setShowAlphaSlider(hasAlpha)
        builder.setDialogType(ColorPickerDialog.TYPE_CUSTOM)

        val dialog = builder.create()
        dialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
            override fun onColorSelected(dialogId: Int, color: Int) {
                val hexColor = if (hasAlpha) color.toAlphaHex() else color.toHex()
                action(hexColor)
            }

            override fun onDialogDismissed(dialogId: Int) {
                // do nothing
            }
        })

        dialog.show(childFragmentManager, null)
    }

    @ColorInt private fun getColorFromHex(hexColor: String?, @ColorInt defaultColor: Int): Int {
        return if (hexColor != null)
            Color.parseColor(hexColor)
        else
            defaultColor
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