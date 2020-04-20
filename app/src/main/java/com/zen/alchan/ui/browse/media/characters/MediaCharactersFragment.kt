package com.zen.alchan.ui.browse.media.characters


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.zen.alchan.R
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.MediaCharacters
import com.zen.alchan.helper.pojo.MediaVoiceActors
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.character.CharacterFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.browse.staff.StaffFragment
import kotlinx.android.synthetic.main.fragment_media_characters.*
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
        return MediaCharactersRvAdapter(activity!!, viewModel.mediaCharacters, viewModel.staffLanguage, object : MediaCharactersRvAdapter.MediaCharactersListener {
            override fun passSelectedCharacter(characterId: Int) {
                val fragment = CharacterFragment()
                val bundle = Bundle()
                bundle.putInt(CharacterFragment.CHARACTER_ID, characterId)
                fragment.arguments = bundle
                listener?.changeFragment(fragment)
            }

            override fun passSelectedVoiceActor(voiceActorId: Int) {
                val fragment = StaffFragment()
                val bundle = Bundle()
                bundle.putInt(StaffFragment.STAFF_ID, voiceActorId)
                fragment.arguments = bundle
                listener?.changeFragment(fragment)
            }
        })
    }

    private fun setupObserver() {
        viewModel.mediaCharactersData.observe(viewLifecycleOwner, Observer {
            loadingLayout.visibility = View.GONE
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (isLoading) {
                        viewModel.mediaCharacters.removeAt(viewModel.mediaCharacters.lastIndex)
                        adapter.notifyItemRemoved(viewModel.mediaCharacters.size)
                        isLoading = false
                    }

                    viewModel.hasNextPage = it.data?.Media()?.characters()?.pageInfo()?.hasNextPage() ?: false
                    viewModel.page += 1
                    viewModel.isInit = true

                    it.data?.Media()?.characters()?.edges()?.forEach { edge ->
                        val voiceActors = ArrayList<MediaVoiceActors>()
                        edge.voiceActors()?.forEach { va ->
                            voiceActors.add(
                                MediaVoiceActors(
                                    va.id(),
                                    va.name()?.full(),
                                    va.language(),
                                    va.image()?.large()
                                )
                            )
                        }

                        val mediaCharacter = MediaCharacters(
                            edge.node()?.id(),
                            edge.node()?.name()?.full(),
                            edge.node()?.image()?.large(),
                            edge.role(),
                            voiceActors
                        )
                        viewModel.mediaCharacters.add(mediaCharacter)
                    }

                    adapter.notifyDataSetChanged()
                }
                ResponseStatus.ERROR -> {
                    DialogUtility.showToast(activity, it.message)
                    if (isLoading) {
                        viewModel.mediaCharacters.removeAt(viewModel.mediaCharacters.lastIndex)
                        adapter.notifyItemRemoved(viewModel.mediaCharacters.size)
                        isLoading = false
                    }
                }
            }
        })

        if (!viewModel.isInit) {
            viewModel.getMediaCharacters()
            loadingLayout.visibility = View.VISIBLE
        }
    }

    private fun initLayout() {
        if (viewModel.mediaType == MediaType.ANIME) {
            voiceActorLanguageScrollView.visibility = View.VISIBLE
        } else {
            voiceActorLanguageScrollView.visibility = View.GONE
        }

        voiceActorLanguageText.text = viewModel.staffLanguage.name

        voiceActorLanguageText.setOnClickListener {
            MaterialAlertDialogBuilder(activity)
                .setItems(viewModel.staffLanguageArray) { _, which ->
                    viewModel.staffLanguage = StaffLanguage.valueOf(viewModel.staffLanguageArray[which])
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

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (parentFragment is MediaFragment) {
                    (parentFragment as MediaFragment).handleChildFragmentScroll(dy, 0)
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
}
