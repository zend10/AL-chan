package com.zen.alchan.ui.common.customise

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jaredrummler.android.colorpicker.ColorShape
import com.zen.alchan.R
import com.zen.alchan.helper.changeStatusBarColor
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.toAlphaHex
import com.zen.alchan.helper.toHex
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_customise_list.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType

class CustomiseListActivity : BaseActivity() {

    private val viewModel by viewModel<CustomiseListViewModel>()
    private lateinit var adapter: ListTypeRvAdapter

    companion object {
        const val MEDIA_TYPE = "mediaType"

        private const val COLOR_PRIMARY = 1
        private const val COLOR_SECONDARY = 2
        private const val COLOR_TEXT = 3
        private const val COLOR_CARD = 4
        private const val COLOR_TOOLBAR = 5
        private const val COLOR_BACKGROUND = 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customise_list)

        if (intent.getStringExtra(MEDIA_TYPE) == MediaType.ANIME.name) {
            viewModel.mediaType = MediaType.ANIME
        } else if (intent.getStringExtra(MEDIA_TYPE) == MediaType.MANGA.name) {
            viewModel.mediaType = MediaType.MANGA
        }

        changeStatusBarColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))

        setSupportActionBar(toolbarLayout)
        supportActionBar?.apply {
            title = if (viewModel.mediaType == MediaType.ANIME) getString(R.string.customise_anime_list) else getString(R.string.customise_manga_list)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_delete)
        }

        initLayout()
    }

    private fun initLayout() {
        if (!viewModel.isInit) {
            viewModel.isInit = true
            viewModel.selectedListStyle = if (viewModel.mediaType == MediaType.ANIME) viewModel.animeListStyle else viewModel.mangaListStyle
        }

        adapter = assignAdapter()
        listTypeRecyclerView.adapter = adapter

        val listStyle = viewModel.selectedListStyle
        if (listStyle.primaryColor != null) primaryColorItem.setCardBackgroundColor(Color.parseColor(listStyle.primaryColor))
        if (listStyle.secondaryColor != null) secondaryColorItem.setCardBackgroundColor(Color.parseColor(listStyle.secondaryColor))
        if (listStyle.textColor != null) textColorItem.setCardBackgroundColor(Color.parseColor(listStyle.textColor))
        if (listStyle.cardColor != null) cardColorItem.setCardBackgroundColor(Color.parseColor(listStyle.cardColor))
        if (listStyle.toolbarColor != null) toolbarColorItem.setCardBackgroundColor(Color.parseColor(listStyle.toolbarColor))
        if (listStyle.backgroundColor != null) backgroundColorItem.setCardBackgroundColor(Color.parseColor(listStyle.backgroundColor))

        primaryColorItem.setOnClickListener {
            showColorPickerDialog(COLOR_PRIMARY)
        }

        secondaryColorItem.setOnClickListener {
            showColorPickerDialog(COLOR_SECONDARY)
        }

        textColorItem.setOnClickListener {
            showColorPickerDialog(COLOR_TEXT)
        }

        cardColorItem.setOnClickListener {
            showColorPickerDialog(COLOR_CARD)
        }

        toolbarColorItem.setOnClickListener {
            showColorPickerDialog(COLOR_TOOLBAR)
        }

        backgroundColorItem.setOnClickListener {
            showColorPickerDialog(COLOR_BACKGROUND)
        }

        addImageText.setOnClickListener {

        }

        resetDefaultButton.setOnClickListener {
            DialogUtility.showOptionDialog(
                this,
                R.string.reset_to_default,
                R.string.this_will_reset_your_anime_list_style_to_default_style,
                R.string.reset,
                {
                    viewModel.selectedListStyle.primaryColor = AndroidUtility.getResValueFromRefAttr(this, R.attr.themePrimaryColor).toHex()
                    viewModel.selectedListStyle.secondaryColor = AndroidUtility.getResValueFromRefAttr(this, R.attr.themeSecondaryColor).toHex()
                    viewModel.selectedListStyle.textColor = AndroidUtility.getResValueFromRefAttr(this, R.attr.themeContentColor).toHex()
                    viewModel.selectedListStyle.cardColor = AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor).toHex()
                    viewModel.selectedListStyle.toolbarColor = AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor).toHex()
                    viewModel.selectedListStyle.backgroundColor = AndroidUtility.getResValueFromRefAttr(this, R.attr.themeBackgroundColor).toHex()

                    primaryColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themePrimaryColor))
                    secondaryColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeSecondaryColor))
                    textColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeContentColor))
                    cardColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))
                    toolbarColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))
                    backgroundColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeBackgroundColor))

                    viewModel.saveListSettings()
                    finish()
                },
                R.string.cancel,
                { }
            )
        }
    }

    private fun assignAdapter(): ListTypeRvAdapter {
        return ListTypeRvAdapter(this, viewModel.listTypeList, viewModel.selectedListStyle.listType, object : ListTypeRvAdapter.ListTypeListener {
            override fun passSelectedList(newListType: ListType) {
                viewModel.selectedListStyle.listType = newListType
                adapter = assignAdapter()
                listTypeRecyclerView.adapter = adapter
            }
        })
    }

    private fun showColorPickerDialog(colorCode: Int) {
        val builder = ColorPickerDialog.newBuilder()

        val currentColor = when (colorCode) {
            COLOR_PRIMARY -> viewModel.selectedListStyle.primaryColor
            COLOR_SECONDARY -> viewModel.selectedListStyle.secondaryColor
            COLOR_TEXT -> viewModel.selectedListStyle.textColor
            COLOR_CARD -> viewModel.selectedListStyle.cardColor
            COLOR_TOOLBAR -> viewModel.selectedListStyle.toolbarColor
            COLOR_BACKGROUND -> viewModel.selectedListStyle.backgroundColor
            else -> null
        }

        if (currentColor != null) {
            builder.setColor(Color.parseColor(currentColor))
        }

        builder.setColorShape(ColorShape.CIRCLE)
        builder.setAllowCustom(true)
        builder.setAllowPresets(false)
        builder.setShowAlphaSlider(colorCode == COLOR_CARD || colorCode == COLOR_TOOLBAR)
        builder.setDialogType(ColorPickerDialog.TYPE_CUSTOM)

        val dialog = builder.create()

        dialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
            override fun onColorSelected(dialogId: Int, color: Int) {
                when (colorCode) {
                    COLOR_PRIMARY -> {
                        viewModel.selectedListStyle.primaryColor = color.toHex()
                        primaryColorItem.setCardBackgroundColor(Color.parseColor(viewModel.selectedListStyle.primaryColor))
                    }
                    COLOR_SECONDARY -> {
                        viewModel.selectedListStyle.secondaryColor = color.toHex()
                        secondaryColorItem.setCardBackgroundColor(Color.parseColor(viewModel.selectedListStyle.secondaryColor))
                    }
                    COLOR_TEXT -> {
                        viewModel.selectedListStyle.textColor = color.toHex()
                        textColorItem.setCardBackgroundColor(Color.parseColor(viewModel.selectedListStyle.textColor))
                    }
                    COLOR_CARD -> {
                        viewModel.selectedListStyle.cardColor = color.toLong().toAlphaHex()
                        cardColorItem.setCardBackgroundColor(Color.parseColor(viewModel.selectedListStyle.cardColor))
                    }
                    COLOR_TOOLBAR -> {
                        viewModel.selectedListStyle.toolbarColor = color.toLong().toAlphaHex()
                        toolbarColorItem.setCardBackgroundColor(Color.parseColor(viewModel.selectedListStyle.toolbarColor))
                    }
                    COLOR_BACKGROUND -> {
                        viewModel.selectedListStyle.backgroundColor = color.toHex()
                        backgroundColorItem.setCardBackgroundColor(Color.parseColor(viewModel.selectedListStyle.backgroundColor))
                    }
                }
            }

            override fun onDialogDismissed(dialogId: Int) {
                // do nothing
            }
        })

        dialog.show(supportFragmentManager, null)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.itemSave) {
            viewModel.saveListSettings()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
