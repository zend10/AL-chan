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
                    getColorFromHex(it.data, defaultPrimaryColor())
                )
            },
            viewModel.secondaryColor.subscribe {
                binding.customiseSecondaryColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, defaultSecondaryColor())
                )
            },
            viewModel.negativeColor.subscribe {
                binding.customiseNegativeColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, defaultNegativeColor())
                )
            },
            viewModel.textColor.subscribe {
                binding.customiseTextColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, defaultTextColor())
                )
            },
            viewModel.cardColor.subscribe {
                binding.customiseCardColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, defaultCardColor())
                )
            },
            viewModel.toolbarColor.subscribe {
                binding.customiseToolbarColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, defaultToolbarColor())
                )
            },
            viewModel.backgroundColor.subscribe {
                binding.customiseBackgroundColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, defaultBackgroundColor())
                )
            },
            viewModel.floatingButtonColor.subscribe {
                binding.customiseFloatingButtonColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, defaultFloatingButtonColor())
                )
            },
            viewModel.floatingIconColor.subscribe {
                binding.customiseFloatingIconColorIcon.setCardBackgroundColor(
                    getColorFromHex(it.data, defaultFloatingIconColor())
                )
            },






            viewModel.listTypes.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateListType(data)
                }
            },
            viewModel.primaryColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, defaultPrimaryColor()), it.second) { hexColor ->
                    viewModel.updatePrimaryColor(hexColor)
                }
            },
            viewModel.secondaryColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, defaultSecondaryColor()), it.second) { hexColor ->
                    viewModel.updateSecondaryColor(hexColor)
                }
            },
            viewModel.negativeColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, defaultNegativeColor()), it.second) { hexColor ->
                    viewModel.updateNegativeColor(hexColor)
                }
            },
            viewModel.textColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, defaultTextColor()), it.second) { hexColor ->
                    viewModel.updateTextColor(hexColor)
                }
            },
            viewModel.cardColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, defaultCardColor()), it.second) { hexColor ->
                    viewModel.updateCardColor(hexColor)
                }
            },
            viewModel.toolbarColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, defaultToolbarColor()), it.second) { hexColor ->
                    viewModel.updateToolbarColor(hexColor)
                }
            },
            viewModel.backgroundColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, defaultBackgroundColor()), it.second) { hexColor ->
                    viewModel.updateBackgroundColor(hexColor)
                }
            },
            viewModel.floatingButtonColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, defaultFloatingButtonColor()), it.second) { hexColor ->
                    viewModel.updateFloatingButtonColor(hexColor)
                }
            },
            viewModel.floatingIconColorAndHasAlpha.subscribe {
                showColorPickerDialog(getColorFromHex(it.first, defaultFloatingIconColor()), it.second) { hexColor ->
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

    @ColorInt private fun getColorFromHex(hexColor: String?, defaultColor: Int): Int {
        return if (hexColor != null)
            Color.parseColor(hexColor)
        else
            defaultColor
    }

    @ColorInt private fun defaultPrimaryColor(): Int {
        return requireContext().getAttrValue(R.attr.themePrimaryColor)
    }

    @ColorInt private fun defaultSecondaryColor(): Int {
        return requireContext().getAttrValue(R.attr.themeSecondaryColor)
    }

    @ColorInt private fun defaultNegativeColor(): Int {
        return requireContext().getAttrValue(R.attr.themeNegativeColor)
    }

    @ColorInt private fun defaultTextColor(): Int {
        return requireContext().getAttrValue(R.attr.themeContentColor)
    }

    @ColorInt private fun defaultCardColor(): Int {
        return requireContext().getAttrValue(R.attr.themeCardColor)
    }

    @ColorInt private fun defaultToolbarColor(): Int {
        return requireContext().getAttrValue(R.attr.themeCardColor)
    }

    @ColorInt private fun defaultBackgroundColor(): Int {
        return requireContext().getAttrValue(R.attr.themeBackgroundColor)
    }

    @ColorInt private fun defaultFloatingButtonColor(): Int {
        return requireContext().getAttrValue(R.attr.themeSecondaryColor)
    }

    @ColorInt private fun defaultFloatingIconColor(): Int {
        return requireContext().getAttrValue(R.attr.themeBackgroundColor)
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