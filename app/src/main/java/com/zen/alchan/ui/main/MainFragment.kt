package com.zen.alchan.ui.main

import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.get
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentMainBinding
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.home.HomeFragment
import com.zen.alchan.ui.medialist.MediaListFragment
import com.zen.alchan.ui.profile.ProfileFragment
import com.zen.alchan.ui.social.SocialFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType


class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private var viewPagerAdapter: FragmentStateAdapter? = null

    private var fragments: List<Fragment?>? = null
    private var homeFragment: HomeFragment? = null
    private var socialFragment: SocialFragment? = null
    private var animeListFragment: MediaListFragment? = null
    private var mangaListFragment: MediaListFragment? = null
    private var profileFragment: ProfileFragment? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            homeFragment = HomeFragment.newInstance()
            socialFragment = SocialFragment.newInstance()
            animeListFragment = MediaListFragment.newInstance(MediaType.ANIME)
            mangaListFragment = MediaListFragment.newInstance(MediaType.MANGA)
            profileFragment = ProfileFragment.newInstance()

            fragments = listOf(
                homeFragment,
                socialFragment,
                animeListFragment,
                mangaListFragment,
                profileFragment
            )

            viewPagerAdapter = MainViewPagerAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
                fragments?.filterNotNull() ?: listOf()
            )
            mainViewPager.isUserInputEnabled = false
            mainViewPager.adapter = viewPagerAdapter
            mainViewPager.offscreenPageLimit = 4

            mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mainBottomNavigation.menu[position].isChecked = true
                }
            })

            mainBottomNavigation.setOnNavigationItemSelectedListener {
                mainViewPager.setCurrentItem(it.order, true)
                true
            }

            mainBottomNavigation.setOnNavigationItemReselectedListener {
                sharedViewModel.scrollToTop(it.order)
            }
        }
    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.isAuthenticated.subscribe {
                binding.mainBottomNavigation.menu.findItem(R.id.menuAnime).isVisible = it
                binding.mainBottomNavigation.menu.findItem(R.id.menuManga).isVisible = it
            }
        )

        viewModel.loadData()
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
        fun newInstance() = MainFragment()
    }
}