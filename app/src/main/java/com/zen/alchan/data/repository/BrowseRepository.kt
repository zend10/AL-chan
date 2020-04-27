package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import type.MediaSort

interface BrowseRepository {
    val characterData: LiveData<Resource<CharacterQuery.Data>>
    val characterMediaData: LiveData<Resource<CharacterMediaConnectionQuery.Data>>
    val characterIsFavoriteData: LiveData<Resource<CharacterIsFavoriteQuery.Data>>

    val staffData: LiveData<Resource<StaffQuery.Data>>
    val staffCharacterData: LiveData<Resource<StaffCharacterConnectionQuery.Data>>
    val staffMediaData: LiveData<Resource<StaffMediaConnectionQuery.Data>>
    val staffIsFavoriteData: LiveData<Resource<StaffIsFavoriteQuery.Data>>

    val studioData: LiveData<Resource<StudioQuery.Data>>
    val studioMediaData: LiveData<Resource<StudioMediaConnectionQuery.Data>>
    val studioIsFavoriteData: LiveData<Resource<StudioIsFavoriteQuery.Data>>

    fun getCharacter(id: Int)
    fun getCharacterMedia(id: Int, page: Int)
    fun checkCharacterIsFavorite(id: Int)

    fun getStaff(id: Int)
    fun getStaffCharacter(id: Int, page: Int)
    fun getStaffMedia(id:Int, page: Int)
    fun checkStaffIsFavorite(id: Int)

    fun getStudio(id: Int)
    fun getStudioMedia(id: Int, page: Int, sort: MediaSort)
    fun checkStudioIsFavorite(id: Int)
}