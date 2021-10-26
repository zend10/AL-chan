package com.zen.alchan.ui.main

import android.net.Uri
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentMainBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.home.HomeFragment
import com.zen.alchan.ui.medialist.MediaListFragment
import com.zen.alchan.ui.profile.ProfileFragment
import com.zen.alchan.ui.social.SocialFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private var viewPagerAdapter: MainViewPagerAdapter? = null

    private var fragments: List<Fragment?>? = null
    private var homeFragment: HomeFragment? = null
    private var animeListFragment: MediaListFragment? = null
    private var mangaListFragment: MediaListFragment? = null
    private var socialFragment: SocialFragment? = null
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
            socialFragment = SocialFragment.newInstance()
            animeListFragment = MediaListFragment.newInstance(MediaType.ANIME)
            mangaListFragment = MediaListFragment.newInstance(MediaType.MANGA)
            profileFragment = ProfileFragment.newInstance()

            fragments = if (isViewerAuthenticated) {
                listOf(
                    homeFragment,
                    animeListFragment,
                    mangaListFragment,
                    socialFragment,
                    profileFragment
                )
            } else {
                listOf(
                    homeFragment,
                    socialFragment,
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

            mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mainBottomNavigation.menu[position].isChecked = true
                }
            })

            mainBottomNavigation.setOnItemSelectedListener {
                val index = if (fragments?.size == mainBottomNavigation.menu.size()) {
                    it.order
                } else {
                    // manually manage the order
                    when (it.itemId) {
                        R.id.menuHome -> 0
                        R.id.menuSocial -> 1
                        R.id.menuProfile -> 2
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
        viewModel.loadData()

        disposables.add(
            incomingDeepLink.subscribe {
                handleDeepLinkNavigation(it)
            }
        )

        deepLink?.let {
            handleDeepLinkNavigation(it)
        }
    }

    private fun handleDeepLinkNavigation(deepLink: DeepLink) {
        when {
            deepLink.isHome() -> binding.mainViewPager.currentItem = 0
            deepLink.isAnimeList() -> {
                val animeListIndex = fragments?.indexOfFirst { it == animeListFragment  }
                if (animeListIndex != null && animeListIndex != -1)
                    binding.mainViewPager.currentItem = animeListIndex
            }
            deepLink.isMangaList() -> {
                val mangaListIndex = fragments?.indexOfFirst { it == mangaListFragment  }
                if (mangaListIndex != null && mangaListIndex != -1)
                    binding.mainViewPager.currentItem = mangaListIndex
            }
            deepLink.isAppSettings() -> {
                val profileIndex = fragments?.indexOfFirst { it == profileFragment }
                if (profileIndex != null && profileIndex != -1)
                    binding.mainViewPager.currentItem = profileIndex
                navigation.navigateToSettings()
                navigation.navigateToAppSettings()
            }
        }

        this.deepLink = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPagerAdapter = null
        fragments = null
        homeFragment = null
        socialFragment = null
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