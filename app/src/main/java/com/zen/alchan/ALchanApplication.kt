package com.zen.alchan

import android.app.Application
import com.google.gson.GsonBuilder
import com.zen.alchan.data.datasource.*
import com.zen.alchan.data.localstorage.*
import com.zen.alchan.data.manager.*
import com.zen.alchan.data.network.apollo.AniListApolloHandler
import com.zen.alchan.data.network.apollo.ApolloHandler
import com.zen.alchan.data.network.interceptor.AniListHeaderInterceptorImpl
import com.zen.alchan.data.network.interceptor.HeaderInterceptor
import com.zen.alchan.data.repository.*
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.helper.service.clipboard.DefaultClipboardService
import com.zen.alchan.helper.service.pushnotification.DefaultPushNotificationService
import com.zen.alchan.helper.service.pushnotification.PushNotificationService
import com.zen.alchan.ui.activity.ActivityDetailViewModel
import com.zen.alchan.ui.activity.ActivityListViewModel
import com.zen.alchan.ui.base.BaseActivityViewModel
import com.zen.alchan.ui.calendar.CalendarViewModel
import com.zen.alchan.ui.character.CharacterViewModel
import com.zen.alchan.ui.character.media.CharacterMediaListViewModel
import com.zen.alchan.ui.common.BottomSheetMediaQuickDetailViewModel
import com.zen.alchan.ui.customise.CustomiseViewModel
import com.zen.alchan.ui.editor.EditorViewModel
import com.zen.alchan.ui.explore.ExploreViewModel
import com.zen.alchan.ui.favorite.FavoriteViewModel
import com.zen.alchan.ui.filter.FilterViewModel
import com.zen.alchan.ui.follow.FollowViewModel
import com.zen.alchan.ui.home.HomeViewModel
import com.zen.alchan.ui.landing.LandingViewModel
import com.zen.alchan.ui.login.LoginViewModel
import com.zen.alchan.ui.main.MainViewModel
import com.zen.alchan.ui.main.SharedMainViewModel
import com.zen.alchan.ui.media.character.MediaCharacterListViewModel
import com.zen.alchan.ui.media.MediaViewModel
import com.zen.alchan.ui.media.staff.MediaStaffListViewModel
import com.zen.alchan.ui.medialist.BottomSheetMediaListQuickDetailViewModel
import com.zen.alchan.ui.medialist.MediaListViewModel
import com.zen.alchan.ui.notifications.NotificationsViewModel
import com.zen.alchan.ui.profile.ProfileViewModel
import com.zen.alchan.ui.reorder.ReorderViewModel
import com.zen.alchan.ui.search.SearchViewModel
import com.zen.alchan.ui.seasonal.SeasonalViewModel
import com.zen.alchan.ui.settings.SettingsViewModel
import com.zen.alchan.ui.settings.account.AccountSettingsViewModel
import com.zen.alchan.ui.settings.anilist.AniListSettingsViewModel
import com.zen.alchan.ui.settings.app.AppSettingsViewModel
import com.zen.alchan.ui.settings.list.ListSettingsViewModel
import com.zen.alchan.ui.settings.notifications.NotificationsSettingsViewModel
import com.zen.alchan.ui.social.SocialViewModel
import com.zen.alchan.ui.splash.SplashViewModel
import com.zen.alchan.ui.staff.StaffViewModel
import com.zen.alchan.ui.staff.character.StaffCharacterListViewModel
import com.zen.alchan.ui.staff.media.StaffMediaListViewModel
import com.zen.alchan.ui.studio.StudioViewModel
import com.zen.alchan.ui.studio.media.StudioMediaListViewModel
import com.zen.alchan.ui.texteditor.TextEditorViewModel
import com.zen.alchan.ui.userstats.UserStatsViewModel

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class ALchanApplication : Application() {

    private val appModules = module {
        val gson = GsonBuilder()
            .setLenient()
            .serializeSpecialFloatingPointValues()
            .create()

        // local storage
        single<SharedPreferencesHandler> {
            DefaultSharedPreferencesHandler(
                this@ALchanApplication.applicationContext,
                Constant.SHARED_PREFERENCES_NAME,
                gson
            )
        }

        single<JsonStorageHandler> {
            DefaultJsonStorageHandler(
                this@ALchanApplication,
                gson
            )
        }

        single<FileStorageHandler> {
            DefaultFileStorageHandler(this@ALchanApplication)
        }

        // local storage manager
        single<UserManager> { DefaultUserManager(get(), get(), get()) }
        single<ContentManager> { DefaultContentManager(get()) }
        single<BrowseManager> { DefaultBrowseManager(get()) }

        // network
        single<HeaderInterceptor> { AniListHeaderInterceptorImpl(get()) }
        single<ApolloHandler> { AniListApolloHandler(get(), Constant.ANILIST_API_BASE_URL) }

        // data source
        single<ContentDataSource> { DefaultContentDataSource(get(), Constant.ANILIST_API_STATUS_VERSION, Constant.ANILIST_API_SOURCE_VERSION) }
        single<UserDataSource> { DefaultUserDataSource(get()) }
        single<MediaListDataSource> { DefaultMediaListDataSource(get(), Constant.ANILIST_API_STATUS_VERSION, Constant.ANILIST_API_SOURCE_VERSION) }
        single<BrowseDataSource> { DefaultBrowseDataSource(get(), Constant.ANILIST_API_STATUS_VERSION, Constant.ANILIST_API_SOURCE_VERSION, Constant.ANILIST_API_RELATION_TYPE_VERSION) }
        single<SocialDataSource> { DefaultSocialDataSource(get()) }

        // repository
        single<ContentRepository> { DefaultContentRepository(get(), get()) }
        single<UserRepository> { DefaultUserRepository(get(), get()) }
        single<MediaListRepository> { DefaultMediaListRepository(get(), get()) }
        single<BrowseRepository> { DefaultBrowseRepository(get(), get()) }
        single<SocialRepository> { DefaultSocialRepository(get()) }

        // service
        single<ClipboardService> { DefaultClipboardService(this.androidContext()) }
        single<PushNotificationService> { DefaultPushNotificationService(this.androidContext(), get()) }

        // view model
        viewModel { BaseActivityViewModel(get()) }

        viewModel { SplashViewModel(get()) }
        viewModel { LandingViewModel() }
        viewModel { LoginViewModel(get()) }

        viewModel { SharedMainViewModel() }
        viewModel { MainViewModel(get(), get(), get()) }

        viewModel { BottomSheetMediaQuickDetailViewModel(get()) }
        viewModel { BottomSheetMediaListQuickDetailViewModel(get(), get()) }

        viewModel { HomeViewModel(get(), get(), get()) }
        viewModel { SearchViewModel(get(), get()) }
        viewModel { SeasonalViewModel(get(), get(), get()) }
        viewModel { ExploreViewModel(get(), get()) }
        viewModel { CalendarViewModel(get(), get()) }

        viewModel { MediaListViewModel(get(), get(), get()) }

        viewModel { NotificationsViewModel(get()) }

        viewModel { ProfileViewModel(get(), get(), get(), get()) }
        viewModel { FollowViewModel(get()) }
        viewModel { UserStatsViewModel(get(), get()) }
        viewModel { FavoriteViewModel(get()) }

        viewModel { SettingsViewModel() }
        viewModel { AppSettingsViewModel(get(), get()) }
        viewModel { AniListSettingsViewModel(get()) }
        viewModel { ListSettingsViewModel(get()) }
        viewModel { NotificationsSettingsViewModel(get()) }
        viewModel { AccountSettingsViewModel(get()) }

        viewModel { ReorderViewModel() }

        viewModel { FilterViewModel(get(), get()) }
        viewModel { CustomiseViewModel(get(), get()) }

        viewModel { EditorViewModel(get(), get()) }

        viewModel { MediaViewModel(get(), get(), get(), get()) }
        viewModel { MediaCharacterListViewModel(get(), get()) }
        viewModel { MediaStaffListViewModel(get(), get()) }
        viewModel { CharacterViewModel(get(), get(), get()) }
        viewModel { CharacterMediaListViewModel(get(), get()) }
        viewModel { StaffViewModel(get(), get(), get()) }
        viewModel { StaffCharacterListViewModel(get(), get()) }
        viewModel { StaffMediaListViewModel(get(), get()) }
        viewModel { StudioViewModel(get(), get(), get()) }
        viewModel { StudioMediaListViewModel(get(), get()) }

        viewModel { SocialViewModel(get(), get(), get()) }
        viewModel { ActivityDetailViewModel(get(), get(), get()) }
        viewModel { ActivityListViewModel(get(), get(), get()) }
        viewModel { TextEditorViewModel(get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ALchanApplication)
            modules(appModules)
        }
    }
}