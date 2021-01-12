package com.zen.alchan.ui.browse.staff.voice


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
import com.zen.alchan.helper.pojo.StaffCharacter
import com.zen.alchan.helper.pojo.StaffCharacterMedia
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.character.CharacterFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.browse.staff.StaffFragment
import kotlinx.android.synthetic.main.bottomsheet_filter_character_media.view.*
import kotlinx.android.synthetic.main.fragment_staff_voice.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class StaffVoiceFragment : BaseFragment() {

    private val viewModel by viewModel<StaffVoiceViewModel>()

    private lateinit var adapter: StaffVoiceRvAdapter
    private lateinit var mediaAdapter: StaffVoiceMediaCharacterRvAdapter
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

        assignAdapter()

        if (viewModel.sortBy == null) {
            staffVoiceRecyclerView.adapter = adapter
        } else {
            staffVoiceRecyclerView.adapter = mediaAdapter
        }

        initLayout()
        setupObserver()
    }

    private fun assignAdapter() {
        adapter = StaffVoiceRvAdapter(activity!!, viewModel.staffCharacters, handleStaffVoiceListener)
        mediaAdapter = StaffVoiceMediaCharacterRvAdapter(requireContext(), viewModel.staffCharacters, handleStaffVoiceListener)
    }

    private val handleStaffVoiceListener = object : StaffVoiceListener {
        override fun passSelectedCharacter(characterId: Int) {
            listener?.changeFragment(BrowsePage.CHARACTER, characterId)
        }

        override fun passSelectedMedia(mediaId: Int, mediaType: MediaType) {
            listener?.changeFragment(BrowsePage.valueOf(mediaType.name), mediaId)
        }
    }

    private fun setupObserver() {
        viewModel.staffCharacterData.observe(viewLifecycleOwner, Observer {
            loadingLayout.visibility = View.GONE
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (it?.data?.staff?.id != viewModel.staffId) {
                        return@Observer
                    }

                    if (!handleSuccessLoading(it.data?.staff?.characters?.pageInfo?.hasNextPage == true)) {
                        return@Observer
                    }

                    it.data?.staff?.characters?.edges?.forEach { edge ->
                        val characterMedia = ArrayList<StaffCharacterMedia>()

                        edge?.media?.forEach { media ->
                            characterMedia.add(
                                StaffCharacterMedia(
                                    media?.id,
                                    media?.title?.userPreferred,
                                    media?.coverImage?.large,
                                    media?.type,
                                    media?.format
                                )
                            )
                        }

                        val staffCharacter = StaffCharacter(
                            edge?.node?.id,
                            edge?.role,
                            edge?.node?.name?.full,
                            edge?.node?.image?.large,
                            characterMedia
                        )
                        viewModel.staffCharacters.add(staffCharacter)
                    }

                    if (viewModel.page == 2) {
                        staffVoiceRecyclerView.adapter = adapter
                    } else {
                        adapter.notifyDataSetChanged()
                    }

                    emptyLayout.visibility = if (viewModel.staffCharacters.isNullOrEmpty()) View.VISIBLE else View.GONE
                    characterSortLayout.visibility = if (viewModel.staffCharacters.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
                ResponseStatus.ERROR -> {
                    handleErrorLoading(it.message)
                }
            }
        })

        viewModel.staffMediaCharacterData.observe(viewLifecycleOwner, Observer {
            loadingLayout.visibility = View.GONE
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (it?.data?.staff?.id != viewModel.staffId) {
                        return@Observer
                    }

                    if (!handleSuccessLoading(it.data?.staff?.characterMedia?.pageInfo?.hasNextPage == true)) {
                        return@Observer
                    }

                    it.data?.staff?.characterMedia?.edges?.forEach { edge ->
                        val characterMedia = ArrayList<StaffCharacterMedia>()

                        characterMedia.add(
                            StaffCharacterMedia(
                                edge?.node?.id,
                                edge?.node?.title?.userPreferred,
                                edge?.node?.coverImage?.large,
                                edge?.node?.type,
                                edge?.node?.format
                            )
                        )

                        edge?.characters?.forEach { character ->
                            val staffCharacter = StaffCharacter(
                                character?.id,
                                edge.characterRole,
                                character?.name?.full,
                                character?.image?.large,
                                characterMedia
                            )

                            viewModel.staffCharacters.add(staffCharacter)
                        }
                    }

                    if (viewModel.page == 2) {
                        staffVoiceRecyclerView.adapter = mediaAdapter
                    } else {
                        mediaAdapter.notifyDataSetChanged()
                    }

                    mediaAdapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.staffCharacters.isNullOrEmpty()) View.VISIBLE else View.GONE
                    characterSortLayout.visibility = if (viewModel.staffCharacters.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
                ResponseStatus.ERROR -> {
                    handleErrorLoading(it.message)
                }
            }
        })

        if (!viewModel.isInit) {
            viewModel.getStaffCharacters()
            loadingLayout.visibility = View.VISIBLE
        }
    }

    private fun handleSuccessLoading(hasNextPage: Boolean): Boolean {
        if (isLoading) {
            viewModel.staffCharacters.removeAt(viewModel.staffCharacters.lastIndex)
            if (viewModel.sortBy == null) {
                adapter.notifyItemRemoved(viewModel.staffCharacters.size)
            } else {
                mediaAdapter.notifyItemChanged(viewModel.staffCharacters.size)
            }
            isLoading = false
        }

        if (!viewModel.hasNextPage) {
            return false
        }

        viewModel.hasNextPage = hasNextPage
        viewModel.page += 1
        viewModel.isInit = true

        return true
    }

    private fun handleErrorLoading(message: String?) {
        DialogUtility.showToast(activity, message)
        if (isLoading) {
            viewModel.staffCharacters.removeAt(viewModel.staffCharacters.lastIndex)
            if (viewModel.sortBy == null) {
                adapter.notifyItemRemoved(viewModel.staffCharacters.size)
            } else {
                mediaAdapter.notifyItemChanged(viewModel.staffCharacters.size)
            }
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

        characterSortLayout.visibility = if (viewModel.staffCharacters.isNullOrEmpty()) View.GONE else View.VISIBLE

        characterSortText.text = getString(viewModel.mediaSortArray[viewModel.mediaSortList.indexOf(viewModel.sortBy)]).toUpperCase(Locale.US)
        characterSortText.setOnClickListener {
            val stringArray = viewModel.mediaSortArray.map { sort -> getString(sort).toUpperCase(Locale.US) }.toTypedArray()
            AlertDialog.Builder(requireContext())
                .setItems(stringArray) { _, which ->
                    viewModel.changeCharacterSort(viewModel.mediaSortList[which])
                    characterSortText.text = stringArray[which]

                    if (viewModel.sortBy == null) {
                        characterShowOnListLayout.visibility = View.GONE
                    } else {
                        characterShowOnListLayout.visibility = View.VISIBLE
                    }

                    loadingLayout.visibility = View.VISIBLE
                    isLoading = false
                    viewModel.getStaffCharacters(true)
                }
                .show()
        }

        characterShowOnListCheckBox.setOnClickListener {
            viewModel.onlyShowOnList = characterShowOnListCheckBox.isChecked

            loadingLayout.visibility = View.VISIBLE
            isLoading = false
            viewModel.getStaffCharacters(true)
        }

        characterShowOnListText.setOnClickListener {
            characterShowOnListCheckBox.performClick()
        }

        if (viewModel.sortBy == null) {
            characterShowOnListLayout.visibility = View.GONE
        } else {
            characterShowOnListLayout.visibility = View.VISIBLE
        }
    }

    private fun loadMore() {
        if (viewModel.hasNextPage) {
            viewModel.staffCharacters.add(null)
            if (viewModel.sortBy == null) {
                adapter.notifyItemInserted(viewModel.staffCharacters.lastIndex)
            } else {
                mediaAdapter.notifyItemInserted(viewModel.staffCharacters.lastIndex)
            }
            viewModel.getStaffCharacters()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        staffVoiceRecyclerView.adapter = null
    }
}
