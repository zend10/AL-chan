package com.zen.alchan.ui.animelist


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.zen.alchan.R
import kotlinx.android.synthetic.main.fragment_anime_list_item.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class AnimeListItemFragment : Fragment() {

    private val viewModel by viewModel<AnimeListItemViewModel>()

    private lateinit var adapter: AnimeListRvAdapter

    companion object {
        const val BUNDLE_ANIME_LIST_STATUS = "animeListStatus"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_anime_list_item, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (!viewModel.isInit) {
            viewModel.selectedFormat = arguments?.getString(BUNDLE_ANIME_LIST_STATUS)
            viewModel.isInit = true
            animeListRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.animeListData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                initLayout()
            }
        })
    }

    private fun initLayout() {
        val selectedList = viewModel.getSelectedList()
        if (!selectedList.isNullOrEmpty()) {
            adapter = AnimeListRvAdapter(activity!!, selectedList, viewModel.scoreFormat)
            animeListRecyclerView.adapter = adapter
        }
    }
}
