package com.zen.alchan.ui.browse.staff.voice


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.zen.alchan.R
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.StaffCharacter
import com.zen.alchan.helper.pojo.StaffCharacterMedia
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.character.CharacterFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.browse.staff.StaffFragment
import kotlinx.android.synthetic.main.fragment_staff_voice.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType

/**
 * A simple [Fragment] subclass.
 */
class StaffVoiceFragment : BaseFragment() {

    private val viewModel by viewModel<StaffVoiceViewModel>()

    private lateinit var adapter: StaffVoiceRvAdapter
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff_voice, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.staffId = arguments?.getInt(StaffFragment.STAFF_ID)
        adapter = assignAdapter()
        staffVoiceRecyclerView.adapter = adapter

        initLayout()
        setupObserver()
    }

    private fun assignAdapter(): StaffVoiceRvAdapter {
        return StaffVoiceRvAdapter(activity!!, viewModel.staffCharacters, object : StaffVoiceRvAdapter.StaffVoiceListener {
            override fun passSelectedCharacter(characterId: Int) {
                val fragment = CharacterFragment()
                val bundle = Bundle()
                bundle.putInt(CharacterFragment.CHARACTER_ID, characterId)
                fragment.arguments = bundle
                listener?.changeFragment(fragment)
            }

            override fun passSelectedMedia(mediaId: Int, mediaType: MediaType) {
                val fragment = MediaFragment()
                val bundle = Bundle()
                bundle.putInt(MediaFragment.MEDIA_ID, mediaId)
                bundle.putString(MediaFragment.MEDIA_TYPE, mediaType.name)
                fragment.arguments = bundle
                listener?.changeFragment(fragment)
            }
        })
    }

    private fun setupObserver() {
        viewModel.staffCharacterData.observe(viewLifecycleOwner, Observer {
            loadingLayout.visibility = View.GONE
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (isLoading) {
                        viewModel.staffCharacters.removeAt(viewModel.staffCharacters.lastIndex)
                        adapter.notifyItemRemoved(viewModel.staffCharacters.size)
                        isLoading = false
                    }

                    if (!viewModel.hasNextPage) {
                        return@Observer
                    }

                    viewModel.hasNextPage = it.data?.Staff()?.characters()?.pageInfo()?.hasNextPage() ?: false
                    viewModel.page += 1
                    viewModel.isInit = true

                    it.data?.Staff()?.characters()?.edges()?.forEach { edge ->
                        val characterMedia = ArrayList<StaffCharacterMedia>()

                        edge.media()?.forEach { media ->
                            characterMedia.add(
                                StaffCharacterMedia(
                                    media.id(),
                                    media.title()?.userPreferred(),
                                    media.coverImage()?.large(),
                                    media.type(),
                                    media.format()
                                )
                            )
                        }

                        val staffCharacter = StaffCharacter(
                            edge.node()?.id(),
                            edge.role(),
                            edge.node()?.name()?.full(),
                            edge.node()?.image()?.large(),
                            characterMedia
                        )
                        viewModel.staffCharacters.add(staffCharacter)
                    }

                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.staffCharacters.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    DialogUtility.showToast(activity, it.message)
                    if (isLoading) {
                        viewModel.staffCharacters.removeAt(viewModel.staffCharacters.lastIndex)
                        adapter.notifyItemRemoved(viewModel.staffCharacters.size)
                        isLoading = false
                    }
                    emptyLayout.visibility = if (viewModel.staffCharacters.isNullOrEmpty()) View.VISIBLE else View.GONE
                    if (!viewModel.isInit) {
                        retryButton.visibility = View.VISIBLE
                        retryButton.setOnClickListener { viewModel.getStaffCharacters() }
                    } else {
                        retryButton.visibility = View.GONE
                    }
                }
            }
        })

        if (!viewModel.isInit) {
            viewModel.getStaffCharacters()
            loadingLayout.visibility = View.VISIBLE
        }
    }

    private fun initLayout() {
        staffVoiceRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            viewModel.staffCharacters.add(null)
            adapter.notifyItemInserted(viewModel.staffCharacters.lastIndex)
            viewModel.getStaffCharacters()
        }
    }
}
