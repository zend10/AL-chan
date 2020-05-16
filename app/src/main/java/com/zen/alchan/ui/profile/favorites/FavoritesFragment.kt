package com.zen.alchan.ui.profile.favorites


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.FavoriteItem
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.profile.favorites.reorder.ReorderFavoritesActivity
import kotlinx.android.synthetic.main.fragment_favorites.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class FavoritesFragment : Fragment() {

    private val viewModel by viewModel<FavoritesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.favoriteAnimeResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> favoriteAnimeLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    favoriteAnimeLoading.visibility = View.GONE

                    if (!viewModel.animeHasNextPage) {
                        return@Observer
                    }

                    viewModel.animeHasNextPage = it.data?.viewer?.favourites?.anime?.pageInfo?.hasNextPage ?: false
                    viewModel.animePage += 1
                    viewModel.animeIsInit = true
                    it.data?.viewer?.favourites?.anime?.edges?.forEach { edge ->
                        viewModel.animeList.add(
                            FavoriteItem(edge?.node?.id!!, edge.node.title?.userPreferred!!, edge.node.coverImage?.large!!, edge.favouriteOrder!!, BrowsePage.ANIME)
                        )
                    }

                    if (viewModel.animeHasNextPage) {
                        viewModel.getFavorites(BrowsePage.ANIME)
                    } else {
                        favoriteAnimeRecyclerView.adapter = FavoritesRvAdapter(activity!!, viewModel.animeList, handleListenerAction())
                        favoriteAnimeLayout.visibility = if (viewModel.animeList.isNullOrEmpty()) View.GONE else View.VISIBLE
                    }
                }
                ResponseStatus.ERROR -> {
                    favoriteAnimeLoading.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                    favoriteAnimeLayout.visibility = if (viewModel.animeList.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
            }
        })

        viewModel.favoriteMangaResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> favoriteMangaLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    favoriteMangaLoading.visibility = View.GONE

                    if (!viewModel.mangaHasNextPage) {
                        return@Observer
                    }

                    viewModel.mangaHasNextPage = it.data?.viewer?.favourites?.manga?.pageInfo?.hasNextPage ?: false
                    viewModel.mangaPage += 1
                    viewModel.mangaIsInit = true
                    it.data?.viewer?.favourites?.manga?.edges?.forEach { edge ->
                        viewModel.mangaList.add(
                            FavoriteItem(edge?.node?.id!!, edge.node.title?.userPreferred!!, edge.node.coverImage?.large!!, edge.favouriteOrder!!, BrowsePage.MANGA)
                        )
                    }

                    if (viewModel.mangaHasNextPage) {
                        viewModel.getFavorites(BrowsePage.MANGA)
                    } else {
                        favoriteMangaRecyclerView.adapter = FavoritesRvAdapter(activity!!, viewModel.mangaList, handleListenerAction())
                        favoriteMangaLayout.visibility = if (viewModel.mangaList.isNullOrEmpty()) View.GONE else View.VISIBLE
                    }
                }
                ResponseStatus.ERROR -> {
                    favoriteMangaLoading.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                    favoriteMangaLayout.visibility = if (viewModel.mangaList.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
            }
        })

        viewModel.favoriteCharactersResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> favoriteCharactersLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    favoriteCharactersLoading.visibility = View.GONE

                    if (!viewModel.charactersHasNextPage) {
                        return@Observer
                    }

                    viewModel.charactersHasNextPage = it.data?.viewer?.favourites?.characters?.pageInfo?.hasNextPage ?: false
                    viewModel.charactersPage += 1
                    viewModel.charactersIsInit = true
                    it.data?.viewer?.favourites?.characters?.edges?.forEach { edge ->
                        viewModel.charactersList.add(
                            FavoriteItem(edge?.node?.id!!, edge.node.name?.full!!, edge.node.image?.large!!, edge.favouriteOrder!!, BrowsePage.CHARACTER)
                        )
                    }

                    if (viewModel.charactersHasNextPage) {
                        viewModel.getFavorites(BrowsePage.CHARACTER)
                    } else {
                        favoriteCharactersRecyclerView.adapter = FavoritesRvAdapter(activity!!, viewModel.charactersList, handleListenerAction())
                        favoriteCharactersLayout.visibility = if (viewModel.charactersList.isNullOrEmpty()) View.GONE else View.VISIBLE
                    }
                }
                ResponseStatus.ERROR -> {
                    favoriteCharactersLoading.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                    favoriteCharactersLayout.visibility = if (viewModel.charactersList.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
            }
        })

        viewModel.favoriteStaffsResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> favoriteStaffsLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    favoriteStaffsLoading.visibility = View.GONE

                    if (!viewModel.staffsHasNextPage) {
                        return@Observer
                    }

                    viewModel.staffsHasNextPage = it.data?.viewer?.favourites?.staff?.pageInfo?.hasNextPage ?: false
                    viewModel.staffsPage += 1
                    viewModel.staffsIsInit = true
                    it.data?.viewer?.favourites?.staff?.edges?.forEach { edge ->
                        viewModel.staffsList.add(
                            FavoriteItem(edge?.node?.id!!, edge.node.name?.full!!, edge.node.image?.large!!, edge.favouriteOrder!!, BrowsePage.STAFF)
                        )
                    }

                    if (viewModel.staffsHasNextPage) {
                        viewModel.getFavorites(BrowsePage.STAFF)
                    } else {
                        favoriteStaffsRecyclerView.adapter = FavoritesRvAdapter(activity!!, viewModel.staffsList, handleListenerAction())
                        favoriteStaffsLayout.visibility = if (viewModel.staffsList.isNullOrEmpty()) View.GONE else View.VISIBLE
                    }
                }
                ResponseStatus.ERROR -> {
                    favoriteStaffsLoading.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                    favoriteStaffsLayout.visibility = if (viewModel.staffsList.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
            }
        })

        viewModel.favoriteStudiosResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> favoriteStudiosLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    favoriteStudiosLoading.visibility = View.GONE

                    if (!viewModel.studiosHasNextPage) {
                        return@Observer
                    }

                    viewModel.studiosHasNextPage = it.data?.viewer?.favourites?.studios?.pageInfo?.hasNextPage ?: false
                    viewModel.studiosPage += 1
                    viewModel.studiosIsInit = true
                    it.data?.viewer?.favourites?.studios?.edges?.forEach { edge ->
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

        viewModel.triggerRefreshFavorite.observe(viewLifecycleOwner, Observer {
            viewModel.refresh()
        })

        if (!viewModel.animeIsInit) {
            viewModel.getFavorites(BrowsePage.ANIME)
        } else {
            favoriteAnimeRecyclerView.adapter = FavoritesRvAdapter(activity!!, viewModel.animeList, handleListenerAction())
            favoriteAnimeLayout.visibility = if (viewModel.animeList.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        if (!viewModel.mangaIsInit) {
            viewModel.getFavorites(BrowsePage.MANGA)
        } else {
            favoriteMangaRecyclerView.adapter = FavoritesRvAdapter(activity!!, viewModel.mangaList, handleListenerAction())
            favoriteMangaLayout.visibility = if (viewModel.mangaList.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        if (!viewModel.charactersIsInit) {
            viewModel.getFavorites(BrowsePage.CHARACTER)
        } else {
            favoriteCharactersRecyclerView.adapter = FavoritesRvAdapter(activity!!, viewModel.charactersList, handleListenerAction())
            favoriteCharactersLayout.visibility = if (viewModel.charactersList.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        if (!viewModel.staffsIsInit) {
            viewModel.getFavorites(BrowsePage.STAFF)
        } else {
            favoriteStaffsRecyclerView.adapter = FavoritesRvAdapter(activity!!, viewModel.staffsList, handleListenerAction())
            favoriteStaffsLayout.visibility = if (viewModel.staffsList.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        if (!viewModel.studiosIsInit) {
            viewModel.getFavorites(BrowsePage.STUDIO)
        } else {
            favoriteStudiosRecyclerView.adapter = FavoritesStudiosRvAdapter(viewModel.studiosList, handleListenerAction())
            favoriteStudiosLayout.visibility = if (viewModel.studiosList.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun initLayout() {
        reorderFavoritesLayout.setOnClickListener {
            MaterialAlertDialogBuilder(activity)
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
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra(BrowseActivity.TARGET_PAGE, browsePage.name)
            intent.putExtra(BrowseActivity.LOAD_ID, id)
            startActivity(intent)
        }
    }
}
