package com.zen.alchan.ui.profile.favorites


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager

import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.FavoriteItem
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.browse.user.UserFragment
import com.zen.alchan.ui.profile.favorites.reorder.ReorderFavoritesActivity
import kotlinx.android.synthetic.main.fragment_favorites.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class FavoritesFragment : BaseFragment() {

    private val viewModel by viewModel<FavoritesViewModel>()
    private lateinit var favoriteAdapter: FavoritesRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null && arguments?.getInt(UserFragment.USER_ID) != null && arguments?.getInt(UserFragment.USER_ID) != 0) {
            viewModel.otherUserId = arguments?.getInt(UserFragment.USER_ID)
        }

        favoriteAdapter = FavoritesRvAdapter(activity!!, viewModel.getMixedList(), handleListenerAction())
        favoriteListRecyclerView.adapter = favoriteAdapter

        (favoriteListRecyclerView.layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (favoriteAdapter.getItemViewType(position) == FavoritesRvAdapter.VIEW_TYPE_TITLE) {
                    resources.getInteger(R.integer.gridSpan)
                } else {
                    1
                }
            }
        }

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.getAnimeObserver().observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> favoriteListLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    favoriteListLoading.visibility = View.GONE

                    if (viewModel.otherUserId != null && viewModel.otherUserId != it?.data?.user?.id) {
                        return@Observer
                    }

                    if (!viewModel.animeHasNextPage) {
                        return@Observer
                    }

                    viewModel.animeHasNextPage = it.data?.user?.favourites?.anime?.pageInfo?.hasNextPage ?: false
                    viewModel.animePage += 1
                    viewModel.animeIsInit = true
                    it.data?.user?.favourites?.anime?.edges?.forEach { edge ->
                        viewModel.animeList.add(
                            FavoriteItem(edge?.node?.id!!, edge.node.title?.userPreferred!!, edge.node.coverImage?.large!!, edge.favouriteOrder!!, BrowsePage.ANIME)
                        )
                    }

                    if (viewModel.animeHasNextPage) {
                        viewModel.getFavorites(BrowsePage.ANIME)
                    } else {
                        favoriteAdapter = FavoritesRvAdapter(activity!!, viewModel.getMixedList(), handleListenerAction())
                        favoriteListRecyclerView.adapter = favoriteAdapter
                    }
                }
                ResponseStatus.ERROR -> {
                    favoriteListLoading.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.getMangaObserver().observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> favoriteListLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    favoriteListLoading.visibility = View.GONE

                    if (viewModel.otherUserId != null && viewModel.otherUserId != it?.data?.user?.id) {
                        return@Observer
                    }

                    if (!viewModel.mangaHasNextPage) {
                        return@Observer
                    }

                    viewModel.mangaHasNextPage = it.data?.user?.favourites?.manga?.pageInfo?.hasNextPage ?: false
                    viewModel.mangaPage += 1
                    viewModel.mangaIsInit = true
                    it.data?.user?.favourites?.manga?.edges?.forEach { edge ->
                        viewModel.mangaList.add(
                            FavoriteItem(edge?.node?.id!!, edge.node.title?.userPreferred!!, edge.node.coverImage?.large!!, edge.favouriteOrder!!, BrowsePage.MANGA)
                        )
                    }

                    if (viewModel.mangaHasNextPage) {
                        viewModel.getFavorites(BrowsePage.MANGA)
                    } else {
                        favoriteAdapter = FavoritesRvAdapter(activity!!, viewModel.getMixedList(), handleListenerAction())
                        favoriteListRecyclerView.adapter = favoriteAdapter
                    }
                }
                ResponseStatus.ERROR -> {
                    favoriteListLoading.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.getCharactersObserver().observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> favoriteListLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    favoriteListLoading.visibility = View.GONE

                    if (viewModel.otherUserId != null && viewModel.otherUserId != it?.data?.user?.id) {
                        return@Observer
                    }

                    if (!viewModel.charactersHasNextPage) {
                        return@Observer
                    }

                    viewModel.charactersHasNextPage = it.data?.user?.favourites?.characters?.pageInfo?.hasNextPage ?: false
                    viewModel.charactersPage += 1
                    viewModel.charactersIsInit = true
                    it.data?.user?.favourites?.characters?.edges?.forEach { edge ->
                        viewModel.charactersList.add(
                            FavoriteItem(edge?.node?.id!!, edge.node.name?.full!!, edge.node.image?.large!!, edge.favouriteOrder!!, BrowsePage.CHARACTER)
                        )
                    }

                    if (viewModel.charactersHasNextPage) {
                        viewModel.getFavorites(BrowsePage.CHARACTER)
                    } else {
                        favoriteAdapter = FavoritesRvAdapter(activity!!, viewModel.getMixedList(), handleListenerAction())
                        favoriteListRecyclerView.adapter = favoriteAdapter
                    }
                }
                ResponseStatus.ERROR -> {
                    favoriteListLoading.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.getStaffObserver().observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> favoriteListLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    favoriteListLoading.visibility = View.GONE

                    if (viewModel.otherUserId != null && viewModel.otherUserId != it?.data?.user?.id) {
                        return@Observer
                    }

                    if (!viewModel.staffsHasNextPage) {
                        return@Observer
                    }

                    viewModel.staffsHasNextPage = it.data?.user?.favourites?.staff?.pageInfo?.hasNextPage ?: false
                    viewModel.staffsPage += 1
                    viewModel.staffsIsInit = true
                    it.data?.user?.favourites?.staff?.edges?.forEach { edge ->
                        viewModel.staffsList.add(
                            FavoriteItem(edge?.node?.id!!, edge.node.name?.full!!, edge.node.image?.large!!, edge.favouriteOrder!!, BrowsePage.STAFF)
                        )
                    }

                    if (viewModel.staffsHasNextPage) {
                        viewModel.getFavorites(BrowsePage.STAFF)
                    } else {
                        favoriteAdapter = FavoritesRvAdapter(activity!!, viewModel.getMixedList(), handleListenerAction())
                        favoriteListRecyclerView.adapter = favoriteAdapter
                    }
                }
                ResponseStatus.ERROR -> {
                    favoriteListLoading.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.getStudiosObserver().observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> favoriteStudiosLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    favoriteStudiosLoading.visibility = View.GONE

                    if (viewModel.otherUserId != null && viewModel.otherUserId != it?.data?.user?.id) {
                        return@Observer
                    }

                    if (!viewModel.studiosHasNextPage) {
                        return@Observer
                    }

                    viewModel.studiosHasNextPage = it.data?.user?.favourites?.studios?.pageInfo?.hasNextPage ?: false
                    viewModel.studiosPage += 1
                    viewModel.studiosIsInit = true
                    it.data?.user?.favourites?.studios?.edges?.forEach { edge ->
                        viewModel.studiosList.add(
                            FavoriteItem(edge?.node?.id!!, edge.node.name, null, edge.favouriteOrder!!, BrowsePage.STUDIO)
                        )
                    }

                    if (viewModel.studiosHasNextPage) {
                        viewModel.getFavorites(BrowsePage.STUDIO)
                    } else {
                        favoriteStudiosRecyclerView.adapter = FavoritesStudiosRvAdapter(viewModel.studiosList, handleListenerAction())
                        favoriteStudiosLayout.visibility = if (viewModel.studiosList.isNullOrEmpty()) View.GONE else View.VISIBLE
                    }
                }
                ResponseStatus.ERROR -> {
                    favoriteStudiosLoading.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                    favoriteStudiosLayout.visibility = if (viewModel.studiosList.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
            }
        })

        viewModel.getTriggerRefreshObserver().observe(viewLifecycleOwner, Observer {
            viewModel.refresh()
        })

        if (!viewModel.animeIsInit) {
            viewModel.getFavorites(BrowsePage.ANIME)
        }

        if (!viewModel.mangaIsInit) {
            viewModel.getFavorites(BrowsePage.MANGA)
        }

        if (!viewModel.charactersIsInit) {
            viewModel.getFavorites(BrowsePage.CHARACTER)
        }

        if (!viewModel.staffsIsInit) {
            viewModel.getFavorites(BrowsePage.STAFF)
        }

        if (!viewModel.studiosIsInit) {
            viewModel.getFavorites(BrowsePage.STUDIO)
        } else {
            favoriteStudiosRecyclerView.adapter = FavoritesStudiosRvAdapter(viewModel.studiosList, handleListenerAction())
            favoriteStudiosLayout.visibility = if (viewModel.studiosList.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun initLayout() {
        if (viewModel.otherUserId != null) {
            reorderFavoritesLayout.visibility = View.GONE
            reorderFavoritesLayout.isEnabled = false
        }

        reorderFavoritesLayout.setOnClickListener {
            AlertDialog.Builder(requireActivity())
                .setItems(viewModel.favoritePageArray) { _, which ->
                    val intent = Intent(activity, ReorderFavoritesActivity::class.java)
                    intent.putExtra(ReorderFavoritesActivity.FAVORITE_CATEGORY, viewModel.favoritePageArray[which])
                    intent.putExtra(ReorderFavoritesActivity.FAVORITE_DATA, viewModel.getFavoriteData(BrowsePage.valueOf(viewModel.favoritePageArray[which])))
                    startActivity(intent)
                }
                .show()
        }
    }

    private fun handleListenerAction() = object : FavoritesListener {
        override fun passSelectedItem(id: Int, browsePage: BrowsePage) {
            if (viewModel.otherUserId != null) {
                listener?.changeFragment(browsePage, id)
            } else {
                val intent = Intent(activity, BrowseActivity::class.java)
                intent.putExtra(BrowseActivity.TARGET_PAGE, browsePage.name)
                intent.putExtra(BrowseActivity.LOAD_ID, id)
                startActivity(intent)
            }
        }
    }
}
