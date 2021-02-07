package com.zen.alchan.ui.common.customise

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.signature.ObjectKey
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jaredrummler.android.colorpicker.ColorShape
import com.zen.alchan.R
import com.zen.alchan.helper.*
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.helper.utils.Utility
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_customise_list.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType
import java.io.File

class CustomiseListActivity : BaseActivity() {

    private val viewModel by viewModel<CustomiseListViewModel>()
    private lateinit var adapter: ListTypeRvAdapter

    companion object {
        const val MEDIA_TYPE = "mediaType"
        private const val PERMISSION_STORAGE = 100
        private const val ACTIVITY_PICK_IMAGE = 200

        private const val COLOR_PRIMARY = 1
        private const val COLOR_SECONDARY = 2
        private const val COLOR_TEXT = 3
        private const val COLOR_CARD = 4
        private const val COLOR_TOOLBAR = 5
        private const val COLOR_BACKGROUND = 6
        private const val COLOR_FLOATING_BUTTON = 7
        private const val COLOR_FLOATING_ICON = 8
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

        customiseLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateSidePadding(windowInsets, initialPadding)
            view.updateTopPadding(windowInsets, initialPadding)
        }

        customiseFormLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateBottomPadding(windowInsets, initialPadding)
        }

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
            if (viewModel.selectedListStyle.backgroundImage == true) {
                viewModel.selectedImageUri = AndroidUtility.getImageFileFromFolder(this, viewModel.mediaType!!).toUri()
            }
        }

        // handle list type
        adapter = assignAdapter()
        listTypeRecyclerView.adapter = adapter

        // handle experience
        longPressViewDetailCheckBox.isChecked = viewModel.selectedListStyle.longPressViewDetail == true
        hideMangaVolumeCheckBox.isChecked = viewModel.selectedListStyle.hideMangaVolume == true
        hideMangaChapterCheckBox.isChecked = viewModel.selectedListStyle.hideMangaChapter == true
        hideNovelVolumeCheckBox.isChecked = viewModel.selectedListStyle.hideNovelVolume == true
        hideNovelChapterCheckBox.isChecked = viewModel.selectedListStyle.hideNovelChapter == true
        showNotesIndicatorCheckBox.isChecked = viewModel.selectedListStyle.showNotesIndicator == true
        showPriorityIndicatorCheckBox.isChecked = viewModel.selectedListStyle.showPriorityIndicator == true
        hideMediaFormatCheckBox.isChecked = viewModel.selectedListStyle.hideMediaFormat == true
        hideScoreWhenNotScoredCheckBox.isChecked = viewModel.selectedListStyle.hideScoreWhenNotScored == true
        hideAiringIndicatorCheckBox.isChecked = viewModel.selectedListStyle.hideAiringIndicator == true

        handleLayoutVisibility(viewModel.selectedListStyle.listType)

        longPressViewDetailCheckBox.setOnClickListener { viewModel.selectedListStyle.longPressViewDetail = longPressViewDetailCheckBox.isChecked }
        hideMangaVolumeCheckBox.setOnClickListener { viewModel.selectedListStyle.hideMangaVolume = hideMangaVolumeCheckBox.isChecked }
        hideMangaChapterCheckBox.setOnClickListener { viewModel.selectedListStyle.hideMangaChapter = hideMangaChapterCheckBox.isChecked }
        hideNovelVolumeCheckBox.setOnClickListener { viewModel.selectedListStyle.hideNovelVolume = hideNovelVolumeCheckBox.isChecked }
        hideNovelChapterCheckBox.setOnClickListener { viewModel.selectedListStyle.hideNovelChapter = hideNovelChapterCheckBox.isChecked }
        showNotesIndicatorCheckBox.setOnClickListener { viewModel.selectedListStyle.showNotesIndicator = showNotesIndicatorCheckBox.isChecked }
        showPriorityIndicatorCheckBox.setOnClickListener { viewModel.selectedListStyle.showPriorityIndicator = showPriorityIndicatorCheckBox.isChecked }
        hideMediaFormatCheckBox.setOnClickListener { viewModel.selectedListStyle.hideMediaFormat = hideMediaFormatCheckBox.isChecked }
        hideScoreWhenNotScoredCheckBox.setOnClickListener { viewModel.selectedListStyle.hideScoreWhenNotScored = hideScoreWhenNotScoredCheckBox.isChecked }
        hideAiringIndicatorCheckBox.setOnClickListener { viewModel.selectedListStyle.hideAiringIndicator = hideAiringIndicatorCheckBox.isChecked }

        // handle theme
        val listStyle = viewModel.selectedListStyle
        if (listStyle.primaryColor != null) primaryColorItem.setCardBackgroundColor(Color.parseColor(listStyle.primaryColor))
        if (listStyle.secondaryColor != null) secondaryColorItem.setCardBackgroundColor(Color.parseColor(listStyle.secondaryColor))
        if (listStyle.textColor != null) textColorItem.setCardBackgroundColor(Color.parseColor(listStyle.textColor))
        if (listStyle.cardColor != null) cardColorItem.setCardBackgroundColor(Color.parseColor(listStyle.cardColor))
        if (listStyle.toolbarColor != null) toolbarColorItem.setCardBackgroundColor(Color.parseColor(listStyle.toolbarColor))
        if (listStyle.backgroundColor != null) backgroundColorItem.setCardBackgroundColor(Color.parseColor(listStyle.backgroundColor))
        if (listStyle.floatingButtonColor != null) floatingButtonColorItem.setCardBackgroundColor(Color.parseColor(listStyle.floatingButtonColor))
        if (listStyle.floatingIconColor != null) floatingIconColorItem.setCardBackgroundColor(Color.parseColor(listStyle.floatingIconColor))

        // handle background image
        if (viewModel.selectedListStyle.backgroundImage == true) {
            listBackgroundImage.visibility = View.VISIBLE
            noImageSelectedText.visibility = View.GONE
            addImageText.text = getString(R.string.remove_image)
            GlideApp.with(this).load(viewModel.selectedImageUri).signature(ObjectKey(Utility.getCurrentTimestamp())).into(listBackgroundImage)
        } else {
            listBackgroundImage.visibility = View.GONE
            noImageSelectedText.visibility = View.VISIBLE
            addImageText.text = getString(R.string.add_image)
            GlideApp.with(this).load(0).signature(ObjectKey(Utility.getCurrentTimestamp())).into(listBackgroundImage)
        }

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

        floatingButtonColorItem.setOnClickListener {
            showColorPickerDialog(COLOR_FLOATING_BUTTON)
        }

        floatingIconColorItem.setOnClickListener {
            showColorPickerDialog(COLOR_FLOATING_ICON)
        }

        addImageText.setOnClickListener {
            if (viewModel.selectedImageUri != null) {
                DialogUtility.showOptionDialog(
                    this,
                    R.string.remove_image,
                    R.string.stop_using_this_image_for_your_list,
                    R.string.remove,
                    {
                        viewModel.selectedListStyle.backgroundImage = false
                        viewModel.selectedImageUri = null

                        GlideApp.with(this).load(0).signature(ObjectKey(Utility.getCurrentTimestamp())).into(listBackgroundImage)
                        listBackgroundImage.visibility = View.GONE
                        noImageSelectedText.visibility = View.VISIBLE
                        addImageText.text = getString(R.string.add_image)
                    },
                    R.string.cancel,
                    { }
                )
            } else {
                getStoragePermission()
            }
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
                    viewModel.selectedListStyle.floatingButtonColor = AndroidUtility.getResValueFromRefAttr(this, R.attr.themeSecondaryColor).toHex()
                    viewModel.selectedListStyle.floatingIconColor = AndroidUtility.getResValueFromRefAttr(this, R.attr.themeBackgroundColor).toHex()
                    viewModel.selectedListStyle.backgroundImage = false
                    viewModel.selectedImageUri = null

                    viewModel.selectedListStyle.longPressViewDetail = true
                    viewModel.selectedListStyle.hideMangaVolume = false
                    viewModel.selectedListStyle.hideMangaChapter = false
                    viewModel.selectedListStyle.hideNovelVolume = false
                    viewModel.selectedListStyle.hideNovelChapter = false
                    viewModel.selectedListStyle.showNotesIndicator = false
                    viewModel.selectedListStyle.showPriorityIndicator = false
                    viewModel.selectedListStyle.hideMediaFormat = false
                    viewModel.selectedListStyle.hideScoreWhenNotScored = false
                    viewModel.selectedListStyle.hideAiringIndicator = false

                    primaryColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themePrimaryColor))
                    secondaryColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeSecondaryColor))
                    textColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeContentColor))
                    cardColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))
                    toolbarColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))
                    backgroundColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeBackgroundColor))
                    floatingButtonColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeSecondaryColor))
                    floatingIconColorItem.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeBackgroundColor))
                    GlideApp.with(this).load(0).signature(ObjectKey(Utility.getCurrentTimestamp())).into(listBackgroundImage)
                    listBackgroundImage.visibility = View.GONE
                    noImageSelectedText.visibility = View.VISIBLE

                    viewModel.saveListSettings()
                    finish()
                },
                R.string.cancel,
                { }
            )
        }
    }

    private fun assignAdapter(): ListTypeRvAdapter {
        return ListTypeRvAdapter(this, viewModel.listTypeList, viewModel.selectedListStyle.listType ?: ListType.LINEAR, object : ListTypeRvAdapter.ListTypeListener {
            override fun passSelectedList(newListType: ListType) {
                viewModel.selectedListStyle.listType = newListType
                adapter = assignAdapter()
                listTypeRecyclerView.adapter = adapter
                handleLayoutVisibility(newListType)
            }
        })
    }

    private fun handleLayoutVisibility(listType: ListType?) {
        if (viewModel.mediaType == MediaType.MANGA) {
            hideMangaVolumeLayout.visibility = View.VISIBLE
            hideMangaChapterLayout.visibility = View.VISIBLE
            hideNovelVolumeLayout.visibility = View.VISIBLE
            hideNovelChapterLayout.visibility = View.VISIBLE
            hideAiringIndicatorLayout.visibility = View.GONE
        } else {
            hideMangaVolumeLayout.visibility = View.GONE
            hideMangaChapterLayout.visibility = View.GONE
            hideNovelVolumeLayout.visibility = View.GONE
            hideNovelChapterLayout.visibility = View.GONE
            hideAiringIndicatorLayout.visibility = View.VISIBLE
        }

        if (listType == ListType.LINEAR || listType == ListType.GRID) {
            showNotesLayout.visibility = View.VISIBLE
        } else {
            showNotesLayout.visibility = View.GONE
        }

        if (listType == ListType.GRID) {
            hideMediaFormatLayout.visibility = View.VISIBLE
        } else {
            hideMediaFormatLayout.visibility = View.GONE
        }
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
            COLOR_FLOATING_BUTTON -> viewModel.selectedListStyle.floatingButtonColor
            COLOR_FLOATING_ICON -> viewModel.selectedListStyle.floatingIconColor
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
                    COLOR_FLOATING_BUTTON -> {
                        viewModel.selectedListStyle.floatingButtonColor = color.toHex()
                        floatingButtonColorItem.setCardBackgroundColor(Color.parseColor(viewModel.selectedListStyle.floatingButtonColor))
                    }
                    COLOR_FLOATING_ICON -> {
                        viewModel.selectedListStyle.floatingIconColor = color.toHex()
                        floatingIconColorItem.setCardBackgroundColor(Color.parseColor(viewModel.selectedListStyle.floatingIconColor))
                    }
                }
            }

            override fun onDialogDismissed(dialogId: Int) {
                // do nothing
            }
        })

        dialog.show(supportFragmentManager, null)
    }

    private fun getStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_STORAGE)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"), ACTIVITY_PICK_IMAGE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStoragePermission()
            } else {
                DialogUtility.showInfoDialog(this, R.string.storage_permission_is_required_to_pick_and_save_image)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data

            viewModel.selectedImageUri = selectedImage
            viewModel.selectedListStyle.backgroundImage = true

            GlideApp.with(this).load(viewModel.selectedImageUri).signature(ObjectKey(Utility.getCurrentTimestamp())).into(listBackgroundImage)
            listBackgroundImage.visibility = View.VISIBLE
            noImageSelectedText.visibility = View.GONE
            addImageText.text = getString(R.string.remove_image)

            viewModel.isImageChanged = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.itemSave) {
            viewModel.saveListSettings()
            if (viewModel.selectedImageUri != null && viewModel.isImageChanged) {
                AndroidUtility.saveUriToFolder(this, viewModel.selectedImageUri!!, viewModel.mediaType!!) { finish() }
            } else {
                finish()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
