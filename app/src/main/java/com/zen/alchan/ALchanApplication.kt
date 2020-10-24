package com.zen.alchan

import android.app.Application
import com.google.gson.GsonBuilder
import com.zen.alchan.data.datasource.*
import com.zen.alchan.data.localstorage.*
import com.zen.alchan.data.network.*
import com.zen.alchan.data.repository.*
import com.zen.alchan.helper.Constant
import com.zen.alchan.ui.main.MainViewModel
import com.zen.alchan.ui.animelist.AnimeListViewModel
import com.zen.alchan.ui.animelist.editor.AnimeListEditorViewModel
import com.zen.alchan.ui.auth.LoginViewModel
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.auth.SplashViewModel
import com.zen.alchan.ui.browse.activity.ActivityDetailViewModel
import com.zen.alchan.ui.browse.activity.ActivityListViewModel
import com.zen.alchan.ui.browse.character.CharacterViewModel
import com.zen.alchan.ui.common.customise.CustomiseListViewModel
import com.zen.alchan.ui.filter.MediaFilterViewModel
import com.zen.alchan.ui.home.HomeViewModel
import com.zen.alchan.ui.mangalist.MangaListViewModel
import com.zen.alchan.ui.mangalist.editor.MangaListEditorViewModel
import com.zen.alchan.ui.browse.media.MediaViewModel
import com.zen.alchan.ui.browse.media.characters.MediaCharactersViewModel
import com.zen.alchan.ui.browse.media.overview.MediaOverviewViewModel
import com.zen.alchan.ui.browse.media.reviews.MediaReviewsViewModel
import com.zen.alchan.ui.browse.media.social.MediaSocialViewModel
import com.zen.alchan.ui.browse.media.staffs.MediaStaffsViewModel
import com.zen.alchan.ui.browse.media.stats.MediaStatsViewModel
import com.zen.alchan.ui.browse.reviews.ReviewsReaderViewModel
import com.zen.alchan.ui.browse.staff.StaffViewModel
import com.zen.alchan.ui.browse.staff.anime.StaffAnimeViewModel
import com.zen.alchan.ui.browse.staff.bio.StaffBioViewModel
import com.zen.alchan.ui.browse.staff.manga.StaffMangaViewModel
import com.zen.alchan.ui.browse.staff.voice.StaffVoiceViewModel
import com.zen.alchan.ui.browse.studio.StudioViewModel
import com.zen.alchan.ui.browse.BrowseViewModel
import com.zen.alchan.ui.browse.reviews.editor.ReviewEditorViewModel
import com.zen.alchan.ui.browse.user.stats.UserStatsDetailViewModel
import com.zen.alchan.ui.browse.user.UserViewModel
import com.zen.alchan.ui.browse.user.list.UserMediaListViewModel
import com.zen.alchan.ui.calendar.CalendarViewModel
import com.zen.alchan.ui.common.ChartViewModel
import com.zen.alchan.ui.common.LikesViewModel
import com.zen.alchan.ui.common.MediaListDetailDialogViewModel
import com.zen.alchan.ui.common.TextEditorViewModel
import com.zen.alchan.ui.explore.ExploreViewModel
import com.zen.alchan.ui.filter.MediaFilterTagViewModel
import com.zen.alchan.ui.notification.NotificationViewModel
import com.zen.alchan.ui.profile.ProfileViewModel
import com.zen.alchan.ui.profile.bio.BioViewModel
import com.zen.alchan.ui.profile.favorites.FavoritesViewModel
import com.zen.alchan.ui.profile.favorites.reorder.ReorderFavoritesViewModel
import com.zen.alchan.ui.profile.follows.FollowsViewModel
import com.zen.alchan.ui.profile.reviews.UserReviewsViewModel
import com.zen.alchan.ui.profile.stats.StatsViewModel
import com.zen.alchan.ui.profile.stats.details.StatsDetailViewModel
import com.zen.alchan.ui.reviews.ReviewsViewModel
import com.zen.alchan.ui.search.SearchListViewModel
import com.zen.alchan.ui.search.SearchViewModel
import com.zen.alchan.ui.seasonal.SeasonalDialogViewModel
import com.zen.alchan.ui.seasonal.SeasonalViewModel
import com.zen.alchan.ui.settings.account.AccountSettingsViewModel
import com.zen.alchan.ui.settings.anilist.AniListSettingsViewModel
import com.zen.alchan.ui.settings.app.AppSettingsViewModel
import com.zen.alchan.ui.settings.list.ListSettingsViewModel
import com.zen.alchan.ui.settings.notifications.NotificationsSettingsViewModel
import com.zen.alchan.ui.social.SocialViewModel
import com.zen.alchan.ui.social.global.GlobalFeedFilterViewModel
import com.zen.alchan.ui.social.global.GlobalFeedViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ALchanApplication : Application() {

    private val appModules = module {
        val gson = GsonBuilder().serializeSpecialFloatingPointValues().create()

        single<LocalStorage> { LocalStorageImpl(this@ALchanApplication.applicationContext, Constant.SHARED_PREFERENCES_NAME, gson) }
        single<AppSettingsManager> { AppSettingsManagerImpl(get()) }
        single<UserManager> { UserManagerImpl(get()) }
        single<MediaManager> { MediaManagerImpl(get()) }
        single<ListStyleManager> { ListStyleManagerImpl(get()) }
        single<InfoManager> { InfoManagerImpl(get()) }

        single<HeaderInterceptor> { HeaderInterceptorImpl(get()) }
        single { ApolloHandler(get()) }
        single { GithubRestService() }
        single { JikanRestService() }

        // AniList GraphQL data source
        single<UserDataSource> { UserDataSourceImpl(get()) }
        single<MediaListDataSource> { MediaListDataSourceImpl(get()) }
        single<MediaDataSource> { MediaDataSourceImpl(get(), get()) }
        single<BrowseDataSource> { BrowseDataSourceImpl(get()) }
        single<SearchDataSource> { SearchDataSourceImpl(get()) }
        single<UserStatisticDataSource> { UserStatisticDataSourceImpl(get()) }
        single<SocialDataSource> { SocialDataSourceImpl(get()) }

        // REST API data source
        single<InfoDataSource> { InfoDataSourceImpl(get()) }

        // AniList GraphQL repository
        single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
        single<UserRepository> { UserRepositoryImpl(get(), get()) }
        single<AppSettingsRepository> { AppSettingsRepositoryImpl(get())}
        single<MediaListRepository> { MediaListRepositoryImpl(get(), get(), gson) }
        single<MediaRepository> { MediaRepositoryImpl(get(), get(), get()) }
        single<ListStyleRepository> { ListStyleRepositoryImpl(get()) }
        single<BrowseRepository> { BrowseRepositoryImpl(get()) }
        single<SearchRepository> { SearchRepositoryImpl(get()) }
        single<UserStatisticRepository> { UserStatisticRepositoryImpl(get(), get(), get()) }
        single<OtherUserRepository> { OtherUserRepositoryImpl(get()) }
        single<OtherUserStatisticRepository> { OtherUserStatisticRepositoryImpl(get(), get()) }
        single<SocialRepository> { SocialRepositoryImpl(get(), get()) }

        // REST API repository
        single<InfoRepository> { InfoRepositoryImpl(get(), get()) }

        // common
        viewModel { BaseViewModel(get()) }
        viewModel { MediaFilterViewModel(get(), get(), gson) }
        viewModel { MediaFilterTagViewModel(get()) }
        viewModel { CustomiseListViewModel(get()) }
        viewModel { MediaListDetailDialogViewModel(gson) }
        viewModel { TextEditorViewModel(get()) }
        viewModel { LikesViewModel(gson) }
        viewModel { ChartViewModel(gson) }

        // auth
        viewModel { SplashViewModel(get(), get(), get()) }
        viewModel { LoginViewModel(get()) }

        // main
        viewModel { MainViewModel(get(), get()) }

        // home, search, explore, seasonal, reviews
        viewModel { HomeViewModel(get(), get(), get(), get()) }
        viewModel { SearchViewModel() }
        viewModel { SearchListViewModel(get()) }
        viewModel { ExploreViewModel(get(), gson) }
        viewModel { SeasonalViewModel(get(), get(), get(), gson) }
        viewModel { SeasonalDialogViewModel(gson) }
        viewModel { ReviewsViewModel(get()) }
        viewModel { CalendarViewModel(get()) }

        // anime list
        viewModel { AnimeListViewModel(get(), get(), get(), get(), gson) }
        viewModel { AnimeListEditorViewModel(get(), get(), gson) }

        // manga list
        viewModel { MangaListViewModel(get(), get(), get(), gson) }
        viewModel { MangaListEditorViewModel(get(), get(), gson) }

        // browse
        viewModel { BrowseViewModel(get()) }

        // browse media
        viewModel { MediaViewModel(get(), get()) }
        viewModel { MediaOverviewViewModel(get()) }
        viewModel { MediaCharactersViewModel(get(), get()) }
        viewModel { MediaStaffsViewModel(get()) }
        viewModel { MediaStatsViewModel(get(), get(), gson) }
        viewModel { MediaReviewsViewModel(get()) }
        viewModel { ReviewEditorViewModel(get()) }
        viewModel { MediaSocialViewModel(get()) }

        // browse character, staff, studio
        viewModel { CharacterViewModel(get(), get()) }
        viewModel { StaffViewModel(get(), get()) }
        viewModel { StaffBioViewModel(get()) }
        viewModel { StaffVoiceViewModel(get()) }
        viewModel { StaffAnimeViewModel(get()) }
        viewModel { StaffMangaViewModel(get()) }
        viewModel { StudioViewModel(get(), get()) }

        // browse user
        viewModel { UserViewModel(get(), get(), get()) }
        viewModel { UserStatsDetailViewModel(get(), get(), gson) }
        viewModel { UserMediaListViewModel(get(), get(), gson) }

        // browse activity
        viewModel { ActivityDetailViewModel(get(), get(), gson) }
        viewModel { ActivityListViewModel(get(), get()) }

        // browse review
        viewModel { ReviewsReaderViewModel(get()) }

        // profile and settings
        viewModel { ProfileViewModel(get(), get()) }
        viewModel { BioViewModel(get(), get(), get()) }
        viewModel { FavoritesViewModel(get(), get(), gson) }
        viewModel { ReorderFavoritesViewModel(get(), gson) }
        viewModel { StatsViewModel(get(), get(), get(), gson) }
        viewModel { StatsDetailViewModel(get(), get(), gson) }
        viewModel { UserReviewsViewModel(get(), get()) }
        viewModel { FollowsViewModel(get(), get()) }
        viewModel { AppSettingsViewModel(get()) }
        viewModel { AniListSettingsViewModel(get()) }
        viewModel { ListSettingsViewModel(get()) }
        viewModel { NotificationsSettingsViewModel(get()) }
        viewModel { AccountSettingsViewModel(get()) }
        viewModel { NotificationViewModel(get()) }

        // social
        viewModel { SocialViewModel(get(), get(), get(), get()) }
        viewModel { GlobalFeedViewModel(get(), get()) }
        viewModel { GlobalFeedFilterViewModel(get()) }

    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ALchanApplication)
            modules(appModules)
        }
    }
}