package com.zen.alchan.ui.social


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.search.SearchActivity
import kotlinx.android.synthetic.main.fragment_social.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class SocialFragment : Fragment() {

    private val viewModel by viewModel<SocialViewModel>()

    private lateinit var bestFriendAdapter: BestFriendRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bestFriendAdapter = assignBestFriendAdapter()
        bestFriendsRecyclerView.adapter = bestFriendAdapter

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.mostTrendingAnimeBannerLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                GlideApp.with(this).load(it).into(globalActivityImage)
            } else {
                GlideApp.with(this).load(0).into(globalActivityImage)
            }
        })

        viewModel.bestFriendChangedNotfier.observe(viewLifecycleOwner, Observer {
            viewModel.reinitBestFriends()
            bestFriendAdapter.notifyDataSetChanged()
        })

        viewModel.initData()
    }

    private fun initLayout() {
        socialRefreshLayout.setOnRefreshListener {
            socialRefreshLayout.isRefreshing = false
            // reload friends activity
        }

        visitGlobalActivityButton.setOnClickListener {
            // open browse type activity
        }

        bestFriendInfo.setOnClickListener {
            DialogUtility.showInfoDialog(
                activity,
                "Best Friend feature is only available on AL-chan. Meaning, it has nothing to do with AniList, and the data is saved locally on your phone.\n\nWith this feature, you can add users to your Best Friend list so that you can quickly access their activity and their profile.\n\nTo add someone as Best Friend, go to their profile page and click on the icon at the toolbar. Once you've added them as best friends, it will appear here.\n\nClick once at their avatar to view only their activity, click once again to disable the filter.\nLong press their avatar to go to their profile.\n\nP.S. The other person will not know you've added them as your best friends."
            )
        }

        // set filter text

        friendsActivityFilterText.setOnClickListener {
            // open filter dialog
        }
    }

    private fun assignBestFriendAdapter(): BestFriendRvAdapter {
        return BestFriendRvAdapter(activity!!, viewModel.bestFriends, AndroidUtility.getScreenWidth(activity) / 5,
            object : BestFriendRvAdapter.BestFriendListener {
                override fun passSelectedBestFriend(position: Int, id: Int?) {
                    if (position != 0) {
                        viewModel.bestFriends.forEachIndexed { index, bestFriend ->
                            if (index == position) {
                                viewModel.bestFriends[index].isSelected = !viewModel.bestFriends[index].isSelected
                            } else {
                                viewModel.bestFriends[index].isSelected = false
                            }
                        }
                        bestFriendAdapter.notifyDataSetChanged()
                        // retrieve best friend activity
                    } else {
                        val intent = Intent(activity, SearchActivity::class.java)
                        intent.putExtra(SearchActivity.SEARCH_USER, true)
                        startActivity(intent)
                    }
                }

                override fun openUserPage(id: Int) {
                    val intent = Intent(activity, BrowseActivity::class.java)
                    intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.USER.name)
                    intent.putExtra(BrowseActivity.LOAD_ID, id)
                    startActivity(intent)
                }
            })
    }
}
