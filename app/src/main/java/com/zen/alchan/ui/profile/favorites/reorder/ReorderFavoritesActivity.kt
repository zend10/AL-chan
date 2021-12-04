package com.zen.alchan.ui.profile.favorites.reorder

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.*
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.DragListener
import com.zen.alchan.helper.libs.ItemMoveCallback
import com.zen.alchan.helper.pojo.FavoriteItem
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_reorder_favorites.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReorderFavoritesActivity : BaseActivity() {

    private val viewModel by viewModel<ReorderFavoritesViewModel>()

    private lateinit var adapter: ReorderFavoritesRvAdapter
    private lateinit var touchHelper: ItemTouchHelper

    companion object {
        const val FAVORITE_DATA = "favoriteData"
        const val FAVORITE_CATEGORY = "favoriteCategory"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reorder_favorites)

        changeStatusBarColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))

        reorderFavoritesLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateTopPadding(windowInsets, initialPadding)
            view.updateSidePadding(windowInsets, initialPadding)
        }

        reorderFavoritesRecyclerView.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateBottomPadding(windowInsets, initialPadding)
        }

        if (intent.getStringExtra(FAVORITE_DATA) != null && viewModel.favoriteList.isNullOrEmpty()) {
            val favoriteItemList = viewModel.gson.fromJson<List<FavoriteItem>>(intent.getStringExtra(FAVORITE_DATA), genericType<List<FavoriteItem>>())
            viewModel.favoriteList.addAll(favoriteItemList)
        }

        viewModel.favoriteCategory = BrowsePage.valueOf(intent.getStringExtra(FAVORITE_CATEGORY) ?: "")

        setSupportActionBar(toolbarLayout)
        supportActionBar?.apply {
            title = "${getString(R.string.reorder_favorite)} ${viewModel.favoriteCategory?.name?.toLowerCase()?.capitalize()}"
            setDisplayHomeAsUpEnabled(true)
        }

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.reorderFavoritesResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, R.string. change_saved)
                    finish()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, it.message)
                }
            }
        })
    }

    private fun initLayout() {
        if (viewModel.favoriteList.isNullOrEmpty()) {
            emptyLayout.visibility = View.VISIBLE
            reorderFavoritesRecyclerView.visibility = View.GONE
            return
        }

        emptyLayout.visibility = View.GONE
        reorderFavoritesRecyclerView.visibility = View.VISIBLE

        adapter = ReorderFavoritesRvAdapter(this, viewModel.favoriteList, object : DragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                touchHelper.startDrag(viewHolder)
            }
        })
        touchHelper = ItemTouchHelper(ItemMoveCallback(adapter))
        touchHelper.attachToRecyclerView(reorderFavoritesRecyclerView)
        reorderFavoritesRecyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemSave) {
            if (viewModel.favoriteList.isNullOrEmpty()) {
                DialogUtility.showToast(this, R.string.you_have_no_favorite_yet)
                return false
            }
            viewModel.saveOrder()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
