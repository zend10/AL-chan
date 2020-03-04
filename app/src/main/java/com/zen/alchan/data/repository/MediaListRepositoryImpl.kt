package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.MediaListDataSource
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaListCollection
import com.zen.alchan.data.response.MediaListGroup
import com.zen.alchan.helper.libs.SingleLiveEvent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MediaListRepositoryImpl(private val mediaListDataSource: MediaListDataSource,
                              private val userManager: UserManager
) : MediaListRepository {

    private val _animeListDataResponse = SingleLiveEvent<Resource<Boolean>>()
    override val animeListDataResponse: LiveData<Resource<Boolean>>
        get() = _animeListDataResponse

    private val _animeListData = MutableLiveData<MediaListCollection>()
    override val animeListData: LiveData<MediaListCollection>
        get() = _animeListData

    override fun getAnimeListData() {

    }

    @SuppressLint("CheckResult")
    override fun retrieveAnimeListData() {
        if (userManager.viewerData?.id == null) {
            return
        }

        _animeListDataResponse.postValue(Resource.Loading())

        mediaListDataSource.getAnimeListData(userManager.viewerData?.id!!).subscribeWith(object : Observer<Response<AnimeListCollectionQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<AnimeListCollectionQuery.Data>) {
                if (t.hasErrors()) {
                    _animeListDataResponse.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    var animeListCollection = Converter.convertMediaListCollection(t.data()?.MediaListCollection())
                    animeListCollection = MediaListCollection(sortAnimeListGrouping(animeListCollection.lists))
                    _animeListDataResponse.postValue(Resource.Success(true))
                    _animeListData.postValue(animeListCollection)
                }
            }

            override fun onError(e: Throwable) {
                _animeListDataResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    private fun sortAnimeListGrouping(animeListGroup: List<MediaListGroup>?): ArrayList<MediaListGroup> {
        if (animeListGroup.isNullOrEmpty()) {
            return ArrayList()
        }

        val sortedList = ArrayList<MediaListGroup>()

        val sectionOrder = userManager.viewerData?.mediaListOptions?.animeList?.sectionOrder
        val customList = userManager.viewerData?.mediaListOptions?.animeList?.customLists

        sectionOrder?.forEach { section ->
            val groupList = animeListGroup.find { group -> group.name == section && group.isCustomList == false }
            if (groupList != null) {
                sortedList.add(groupList)
            }
        }

        customList?.forEach { custom ->
            val groupList = animeListGroup.find { group -> group.name == custom && group.isCustomList == true }
            if (groupList != null) {
                sortedList.add(groupList)
            }
        }

        return sortedList
    }
}