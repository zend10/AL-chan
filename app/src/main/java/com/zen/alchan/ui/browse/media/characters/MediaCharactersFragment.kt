package com.zen.alchan.ui.browse.media.characters


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.MediaCharacters
import com.zen.alchan.helper.pojo.MediaVoiceActors
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import kotlinx.android.synthetic.main.fragment_media_characters.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType
import type.StaffLanguage

/**
 * A simple [Fragment] subclass.
 */
class MediaCharactersFragment : BaseFragment() {

    private val viewModel by viewModel<MediaCharactersViewModel>()

    private lateinit var adapter: MediaCharactersRvAdapter
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_characters, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.mediaId = arguments?.getInt(MediaFragment.MEDIA_ID)
        viewModel.mediaType = MediaType.valueOf(arguments?.getString(MediaFragment.MEDIA_TYPE)!!)
        adapter = assignAdapter()
        charactersRecyclerView.adapter = adapter

        initLayout()
        setupObserver()
    }

    private fun assignAdapter(): MediaCharactersRvAdapter {
        return MediaCharactersRvAdapter(activity!!, viewModel.mediaCharacters, viewModel.staffLanguage ?: StaffLanguage.JAPANESE, object : MediaCharactersRvAdapter.MediaCharactersListener {
            override fun passSelectedCharacter(characterId: Int) {
                listener?.changeFragment(BrowsePage.CHARACTER, characterId)
            }

            override fun passSelectedVoiceActor(voiceActorId: Int) {
                listener?.changeFragment(BrowsePage.STAFF, voiceActorId)
            }
        })
    }

    private fun setupObserver() {
        viewModel.mediaCharactersData.observe(viewLifecycleOwner, Observer {
            loadingLayout.visibility = View.GONE
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (it.data?.media?.id != viewModel.mediaId) {
                        return@Observer
                    }

                    if (isLoading) {
                        viewModel.mediaCharacters.removeAt(viewModel.mediaCharacters.lastIndex)
                        adapter.notifyItemRemoved(viewModel.mediaCharacters.size)
                        isLoading = false
                    }

                    if (!viewModel.hasNextPage) {
                        return@Observer
                    }

                    viewModel.hasNextPage = it.data?.media?.characters?.pageInfo?.hasNextPage ?: false
                    viewModel.page += 1
                    viewModel.isInit = true

                    it.data?.media?.characters?.edges?.forEach { edge ->
                        val voiceActors = ArrayList<MediaVoiceActors>()
                        edge?.voiceActors?.forEach { va ->
                            voiceActors.add(
                                MediaVoiceActors(
                                    va?.id,
                                    va?.name?.full,
                                    va?.language,
                                    va?.image?.large
                                )
                            )
                        }

                        val mediaCharacter = MediaCharacters(
                            edge?.node?.id,
                            edge?.node?.name?.full,
                            edge?.node?.image?.large,
                            edge?.role,
                            voiceActors
                        )
                        viewModel.mediaCharacters.add(mediaCharacter)
                    }

                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.mediaCharacters.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    DialogUtility.showToast(activity, it.message)
                    if (isLoading) {
                        viewModel.mediaCharacters.removeAt(viewModel.mediaCharacters.lastIndex)
                        adapter.notifyItemRemoved(viewModel.mediaCharacters.size)
                        isLoading = false
                    }

                    emptyLayout.visibility = if (viewModel.mediaCharacters.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
            }
        })

        viewModel.triggerMediaCharacter.observe(viewLifecycleOwner, Observer {
            isLoading = false
            viewModel.refresh()
        })

        if (!viewModel.isInit) {
            viewModel.getMediaCharacters()
            loadingLayout.visibility = View.VISIBLE
        }
    }

    private fun initLayout() {
        if (viewModel.mediaType == MediaType.ANIME) {
            voiceActorLanguageLayout.visibility = View.VISIBLE
        } else {
            voiceActorLanguageLayout.visibility = View.GONE
        }

        voiceActorLanguageText.text = viewModel.staffLanguage?.name

        voiceActorLanguageText.setOnClickListener {
            AlertDialog.Builder(requireActivity())
                .setItems(viewModel.staffLanguageArray) { _, which ->
                    viewModel.changeVoiceActorLanguage(which)
                    voiceActorLanguageText.text = viewModel.staffLanguageArray[which]
                    adapter = assignAdapter()
                    charactersRecyclerView.adapter = adapter
                }
                .show()
        }

        charactersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            viewModel.mediaCharacters.add(null)
            adapter.notifyItemInserted(viewModel.mediaCharacters.lastIndex)
            viewModel.getMediaCharacters()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        charactersRecyclerView.adapter = null
    }
}
