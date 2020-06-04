package com.zen.alchan.ui.profile.follows


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.FollowPage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.FollowsItem
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.browse.user.follows.UserFollowsFragment
import kotlinx.android.synthetic.main.fragment_follows.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class FollowsFragment : BaseFragment() {

    private val viewModel by viewModel<FollowsViewModel>()

    private lateinit var adapter: RecyclerView.Adapter<*>
    private var isLoading = false

    companion object {
        const val FOLLOW_PAGE = "followPage"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follows, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null && arguments?.getInt(UserFollowsFragment.USER_ID) != null && arguments?.getInt(UserFollowsFragment.USER_ID) != 0) {
            viewModel.otherUserId = arguments?.getInt(UserFollowsFragment.USER_ID)
        }

        viewModel.followPage = FollowPage.valueOf(arguments?.getString(FOLLOW_PAGE)!!)
        adapter = assignAdapter()
        followsRecyclerView.adapter = adapter

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        if (viewModel.followPage == FollowPage.FOLLOWING) {
            viewModel.getFollowingsObserver().observe(viewLifecycleOwner, Observer {
                if (viewModel.followPage != FollowPage.FOLLOWING) return@Observer
                when (it.responseStatus) {
                    ResponseStatus.SUCCESS -> {
                        loadingLayout.visibility = View.GONE
                        if (isLoading) {
                            viewModel.followsList.removeAt(viewModel.followsList.lastIndex)
                            adapter.notifyItemRemoved(viewModel.followsList.size)
                            isLoading = false
                        }

                        if (!viewModel.hasNextPage) {
                            return@Observer
                        }

                        viewModel.page += 1
                        viewModel.isInit = true
                        viewModel.hasNextPage = it.data?.page?.pageInfo?.hasNextPage == true

                        it.data?.page?.following?.forEach { follow ->
                            viewModel.followsList.add(FollowsItem(
                                id = follow?.id!!,
                                name = follow.name,
                                image = follow.avatar?.large,
                                isFollower = follow.isFollower!!,
                                isFollowing = follow.isFollowing!!,
                                siteUrl = follow.siteUrl!!
                            ))
                        }
                        adapter.notifyDataSetChanged()
                        emptyLayout.visibility = if (viewModel.followsList.isNullOrEmpty()) View.VISIBLE else View.GONE
                    }
                    ResponseStatus.ERROR -> {
                        DialogUtility.showToast(activity, it.message)

                        if (isLoading) {
                            viewModel.followsList.removeAt(viewModel.followsList.lastIndex)
                            adapter.notifyItemRemoved(viewModel.followsList.size)
                            isLoading = false
                        }

                        emptyLayout.visibility = if (viewModel.followsList.isNullOrEmpty()) View.VISIBLE else View.GONE
                    }
                }
            })

            viewModel.toggleFollowingResponse.observe(viewLifecycleOwner, Observer {
                when (it.responseStatus) {
                    ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                    ResponseStatus.SUCCESS -> {
                        loadingLayout.visibility = View.GONE
                        val user = it.data?.toggleFollow
                        val findFollowing = viewModel.followsList.find { following -> following?.id == user?.id }
                        if (findFollowing == null) {
                            viewModel.followsList.add(FollowsItem(
                                id = user?.id!!,
                                name = user.name,
                                image = user.avatar?.large,
                                isFollowing = user.isFollowing!!,
                                isFollower = user.isFollower!!,
                                siteUrl = user.siteUrl!!
                            ))
                        } else {
                            viewModel.followsList.remove(findFollowing)
                        }
                        viewModel.followsList.sortBy { follow -> follow?.name?.toLowerCase() }
                        adapter.notifyDataSetChanged()
                        emptyLayout.visibility = if (viewModel.followsList.isNullOrEmpty()) View.VISIBLE else View.GONE
                    }
                    ResponseStatus.ERROR -> {
                        loadingLayout.visibility = View.GONE
                        DialogUtility.showToast(activity, it.message)
                    }
                }
            })
        } else if (viewModel.followPage == FollowPage.FOLLOWERS) {
            viewModel.getFollowersObserver().observe(viewLifecycleOwner, Observer {
                if (viewModel.followPage != FollowPage.FOLLOWERS) return@Observer
                when (it.responseStatus) {
                    ResponseStatus.SUCCESS -> {
                        loadingLayout.visibility = View.GONE
                        if (isLoading) {
                            viewModel.followsList.removeAt(viewModel.followsList.lastIndex)
                            adapter.notifyItemRemoved(viewModel.followsList.size)
                            isLoading = false
                        }

                        if (!viewModel.hasNextPage) {
                            return@Observer
                        }

                        viewModel.page += 1
                        viewModel.isInit = true
                        viewModel.hasNextPage = it.data?.page?.pageInfo?.hasNextPage == true

                        it.data?.page?.followers?.forEach { follow ->
                            viewModel.followsList.add(FollowsItem(
                                id = follow?.id!!,
                                name = follow.name,
                                image = follow.avatar?.large,
                                isFollower = follow.isFollower!!,
                                isFollowing = follow.isFollowing!!,
                                siteUrl = follow.siteUrl!!
                            ))
                        }
                        adapter.notifyDataSetChanged()
                        emptyLayout.visibility = if (viewModel.followsList.isNullOrEmpty()) View.VISIBLE else View.GONE
                    }
                    ResponseStatus.ERROR -> {
                        DialogUtility.showToast(activity, it.message)

                        if (isLoading) {
                            viewModel.followsList.removeAt(viewModel.followsList.lastIndex)
                            adapter.notifyItemRemoved(viewModel.followsList.size)
                            isLoading = false
                        }

                        emptyLayout.visibility = if (viewModel.followsList.isNullOrEmpty()) View.VISIBLE else View.GONE
                    }
                }
            })

            viewModel.toggleFollowerResponse.observe(viewLifecycleOwner, Observer {
                when (it.responseStatus) {
                    ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                    ResponseStatus.SUCCESS -> {
                        loadingLayout.visibility = View.GONE
                        val user = it.data?.toggleFollow
                        val findFollower = viewModel.followsList.find { following -> following?.id == user?.id }
                        if (findFollower != null) {
                            val index = viewModel.followsList.indexOf(findFollower)
                            viewModel.followsList[index]?.isFollower = user?.isFollower == true
                            viewModel.followsList[index]?.isFollowing = user?.isFollowing == true
                            adapter.notifyDataSetChanged()
                            emptyLayout.visibility = if (viewModel.followsList.isNullOrEmpty()) View.VISIBLE else View.GONE
                        }
                    }
                    ResponseStatus.ERROR -> {
                        loadingLayout.visibility = View.GONE
                        DialogUtility.showToast(activity, it.message)
                    }
                }
            })
        }

        if (!viewModel.isInit) {
            loadingLayout.visibility = View.VISIBLE
            viewModel.getItem()
        }
    }

    private fun initLayout() {
        followsRefreshLayout.setOnRefreshListener {
            followsRefreshLayout.isRefreshing = false
            viewModel.followsList.clear()
            adapter.notifyDataSetChanged()
            loadingLayout.visibility = View.VISIBLE

            viewModel.page = 1
            viewModel.hasNextPage = true
            isLoading = false
            viewModel.getItem()
        }

        followsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1) && viewModel.isInit && !isLoading) {
                    loadMore()
                    isLoading = true
                }
            }
        })
    }

    private fun loadMore() {
        if (viewModel.hasNextPage) {
            viewModel.followsList.add(null)
            adapter.notifyItemInserted(viewModel.followsList.lastIndex)
            viewModel.getItem()
        }
    }

    private fun assignAdapter(): FollowsRvAdapter {
        return FollowsRvAdapter(activity!!, viewModel.followsList, viewModel.otherUserId != null, object : FollowsRvAdapter.FollowsListener {
            override fun toggleFollow(id: Int, isFollowing: Boolean) {
                DialogUtility.showOptionDialog(
                    activity,
                    if (isFollowing) R.string.follow_this_user else R.string.unfollow_this_user,
                    if (isFollowing) R.string.are_you_sure_you_want_to_follow_this_user else R.string.are_you_sure_you_want_to_shatter_this_friendship,
                    if (isFollowing) R.string.follow else R.string.unfollow,
                    {
                        viewModel.toggleFollow(id)
                    },
                    R.string.cancel,
                    { }
                )
            }

            override fun openUserPage(id: Int) {
                if (viewModel.otherUserId != null) {
                    listener?.changeFragment(BrowsePage.USER, id)
                } else {
                    val intent = Intent(activity, BrowseActivity::class.java)
                    intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.USER.name)
                    intent.putExtra(BrowseActivity.LOAD_ID, id)
                    startActivity(intent)
                }
            }

            override fun openAniListPage(url: String) {
                CustomTabsIntent.Builder().build().launchUrl(activity!!, Uri.parse(url))
            }
        })
    }
}
