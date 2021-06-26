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
    private var animeListFragment: MediaListFragment? = null
    private var mangaListFragment: MediaListFragment? = null
    private var socialFragment: SocialFragment? = null
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

            mainViewPager.isUserInputEnabled = false
            mainViewPager.offscreenPageLimit = 4

            mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mainBottomNavigation.menu[position].isChecked = true
                }
            })

            mainBottomNavigation.setOnNavigationItemSelectedListener {
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

            mainBottomNavigation.setOnNavigationItemReselectedListener {
                sharedViewModel.scrollToTop(it.order)
            }
        }
    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.isAuthenticated.subscribe {
                fragments = if (it) {
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