package com.zen.alchan.ui.settings.app

import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.pojo.AppThemeItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import kotlin.collections.ArrayList

class AppThemeListViewModel : BaseViewModel() {

    private val appThemeItemsSubject = BehaviorSubject.createDefault<List<AppThemeItem>>(listOf())

    val appThemeItems: Observable<List<AppThemeItem>>
        get() = appThemeItemsSubject

    override fun loadData() {
        getAppThemeItems()
    }

    private fun getAppThemeItems() {
        val items = ArrayList<AppThemeItem>()
        var header = ""
        AppTheme.values().forEach {
            val splitAppThemeName = it.name.split("_")
            val appThemeName = splitAppThemeName.take(splitAppThemeName.size - 1).joinToString(" ") { it.toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault()) }
            if (header != appThemeName) {
                header = appThemeName
                items.add(AppThemeItem(header = header))
            }
            items.add(AppThemeItem(appTheme = it))
        }
        appThemeItemsSubject.onNext(items)
    }
}