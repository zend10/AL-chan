package com.zen.alchan.ui.main

import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentMainBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.helper.utils.PushNotificationUtil
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.home.HomeFragment
import com.zen.alchan.ui.medialist.MediaListFragment
import com.zen.alchan.ui.notifications.NotificationsFragment
import com.zen.alchan.ui.profile.ProfileFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private var viewPagerAdapter: MainViewPagerAdapter? = null

    private var fragments: List<Fragment?>? = null
    private var homeFragment: HomeFragment? = null
    private var animeListFragment: MediaListFragment? = null
    private var mangaListFragment: MediaListFragment? = null
    private var notificationsFragment: NotificationsFragment? = null
    private var profileFragment: ProfileFragment? = null

    private var deepLink: DeepLink? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            val isViewerAuthenticated = viewModel.isViewerAuthenticated

            homeFragment = HomeFragment.newInstance()
            notificationsFragment = NotificationsFragment.newInstance()
            animeListFragment = MediaListFragment.newInstance(MediaType.ANIME)
            mangaListFragment = MediaListFragment.newInstance(MediaType.MANGA)
            profileFragment = ProfileFragment.newInstance()

            fragments = if (isViewerAuthenticated) {
                listOf(
                    homeFragment,
                    animeListFragment,
                    mangaListFragment,
                    notificationsFragment,
                    profileFragment
                )
            } else {
                listOf(
                    homeFragment,
                    profileFragment
                )
            }

            viewPagerAdapter = MainViewPagerAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
                fragments?.filterNotNull() ?: listOf()
            )

            binding.mainViewPager.adapter = viewPagerAdapter
            mainViewPager.isUserInputEnabled = false
            mainViewPager.offscreenPageLimit = 4

            binding.mainBottomNavigation.menu.findItem(R.id.menuAnime).isVisible = isViewerAuthenticated
            binding.mainBottomNavigation.menu.findItem(R.id.menuManga).isVisible = isViewerAuthenticated
            binding.mainBottomNavigation.menu.findItem(R.id.menuNotifications).isVisible = isViewerAuthenticated

            mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mainBottomNavigation.menu[position].isChecked = true
                }
            })

            mainBottomNavigation.setOnItemSelectedListener {
                if (it.itemId == R.id.menuNotifications)
                    viewModel.clearUnreadNotificationCountBadge()

                val index = if (fragments?.size == mainBottomNavigation.menu.size()) {
                    it.order
                } else {
                    // manually manage the order
                    when (it.itemId) {
                        R.id.menuHome -> 0
                        R.id.menuProfile -> 1
                        else -> 0
                    }
                }
                mainViewPager.setCurrentItem(index, true)
                true
            }

            mainBottomNavigation.setOnItemReselectedListener {
                sharedViewModel.scrollToTop(it.order)
            }
        }
    }

    override fun setUpObserver() {
        viewModel.loadData(Unit)

        if (!sharedDisposablesAdded) {
            sharedDisposables.add(
                incomingDeepLink.subscribe {
                    handleDeepLinkNavigation(it)
                }
            )

            sharedDisposables.add(
                sharedViewModel.bottomSheetNavigation.subscribe {
                    binding.mainViewPager.setCurrentItem(it, true)
                }
            )

            sharedDisposablesAdded = true
        }

        disposables.add(
            viewModel.unreadNotificationCount.subscribe {
                if (it == 0)
                    binding.mainBottomNavigation.removeBadge(R.id.menuNotifications)
                else
                    binding.mainBottomNavigation.getOrCreateBadge(R.id.menuNotifications).number = it
            }
        )

        deepLink?.let {
            handleDeepLinkNavigation(it)
        }
    }

    private fun handleDeepLinkNavigation(deepLink: DeepLink) {
        val isViewerAuthenticated = viewModel.isViewerAuthenticated

        when {
            deepLink.isHome() -> binding.mainViewPager.currentItem = 0
            deepLink.isAnimeList() && isViewerAuthenticated -> {
                val animeListIndex = fragments?.indexOfFirst { it == animeListFragment  }
                if (animeListIndex != null && animeListIndex != -1) {
                    changeTabWithDelay(animeListIndex)
                }
            }
            deepLink.isMangaList() && isViewerAuthenticated -> {
                val mangaListIndex = fragments?.indexOfFirst { it == mangaListFragment  }
                if (mangaListIndex != null && mangaListIndex != -1) {
                    changeTabWithDelay(mangaListIndex)
                }
            }
            deepLink.isNotifications() && isViewerAuthenticated -> {
                val notificationsIndex = fragments?.indexOfFirst { it == notificationsFragment }
                if (notificationsIndex != null && notificationsIndex != -1) {
                    changeTabWithDelay(notificationsIndex)
                    context?.let { PushNotificationUtil.clearAllPushNotification(it) }
                }
            }
            deepLink.isProfile() && isViewerAuthenticated -> {
                val profileIndex = fragments?.indexOfFirst { it == profileFragment }
                if (profileIndex != null && profileIndex != -1) {
                    changeTabWithDelay(profileIndex)
                }
            }
            deepLink.isAppSettings() && isViewerAuthenticated -> {
                val profileIndex = fragments?.indexOfFirst { it == profileFragment }
                if (profileIndex != null && profileIndex != -1) {
                    binding.mainViewPager.currentItem = profileIndex
                    navigation.navigateToSettings()
                    navigation.navigateToAppSettings()
                }
            }
            deepLink.isAniListSettings() && isViewerAuthenticated -> {
                val profileIndex = fragments?.indexOfFirst { it == profileFragment }
                if (profileIndex != null && profileIndex != -1) {
                    binding.mainViewPager.currentItem = profileIndex
                    navigation.navigateToSettings()
                    navigation.navigateToAniListSettings()
                }
            }
            deepLink.isListSettings() && isViewerAuthenticated -> {
                val profileIndex = fragments?.indexOfFirst { it == profileFragment }
                if (profileIndex != null && profileIndex != -1) {
                    binding.mainViewPager.currentItem = profileIndex
                    navigation.navigateToSettings()
                    navigation.navigateToListSettings()
                }
            }
            deepLink.isSpoiler() -> {
                dialog.showSpoilerDialog(deepLink.getQueryParamOfOrNull("data") ?: "", null)
            }
            deepLink.isAnime() || deepLink.isManga() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToMedia(it.toInt()) }
            }
            deepLink.isCharacter() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToCharacter(it.toInt()) }
            }
            deepLink.isStaff() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToStaff(it.toInt()) }
            }
            deepLink.isStudio() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToStudio(it.toInt()) }
            }
            deepLink.isUser() -> {
                deepLink.getAniListPageId()?.let {
                    val isUsername = it.toIntOrNull() == null
                    if (isUsername)
                        navigation.navigateToUser(username = it)
                    else
                        navigation.navigateToUser(id = it.toInt())
                }
            }
            deepLink.isActivity() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToActivityDetail(it.toInt()) { _, _ -> } }
            }
        }

        this.deepLink = null
    }

    private fun changeTabWithDelay(index: Int) {
        Single.timer(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    binding.mainViewPager.currentItem = index
                },
                {
                }
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPagerAdapter = null
        fragments = null
        homeFragment = null
        notificationsFragment = null
        animeListFragment = null
        mangaListFragment = null
        profileFragment = null
    }

    companion object {
        @JvmStatic
        fun newInstance(deepLink: DeepLink?) = MainFragment().apply {
            this.deepLink = deepLink
        }
    }
}