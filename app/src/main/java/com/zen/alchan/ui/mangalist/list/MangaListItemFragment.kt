package com.zen.alchan.ui.mangalist.list


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap

import com.zen.alchan.R
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.pojo.AdvancedScoresItem
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.common.SetProgressDialog
import com.zen.alchan.ui.common.SetScoreDialog
import com.zen.alchan.ui.mangalist.MangaListFragment
import com.zen.alchan.ui.mangalist.editor.MangaListEditorActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_manga_list_item.*
import kotlinx.android.synthetic.main.layout_empty.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaListStatus
import type.ScoreFormat

/**
 * A simple [Fragment] subclass.
 */
class MangaListItemFragment : Fragment() {

    private val viewModel by viewModel<MangaListItemViewModel>()

    private var adapter: RecyclerView.Adapter<*>? = null
    private var mediaLists = ArrayList<MediaList>()

    private var disposable: Disposable? = null
    private var searchKeyWord = ""

    companion object {
        const val BUNDLE_ANIME_LIST_STATUS = "animeListStatus"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manga_list_item, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (!viewModel.isInit) {
            viewModel.selectedStatus = arguments?.getString(BUNDLE_ANIME_LIST_STATUS)
            viewModel.isInit = true
        }

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.mangaListData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                mediaLists.clear()
                viewModel.getSelectedList().forEach { filtered ->
                    if (filtered.media?.title?.userPreferred?.toLowerCase()?.contains(searchKeyWord) == true) {
                        mediaLists.add(filtered)
                    }
                }
                adapter?.notifyDataSetChanged()

                if (mediaLists.isEmpty()) {
                    mangaListRecyclerView.visibility = View.GONE
                    emptyLayout.visibility = View.VISIBLE
                } else {
                    mangaListRecyclerView.visibility = View.VISIBLE
                    emptyLayout.visibility = View.GONE
                }
            }
        })

        viewModel.mangaListStyleLiveData.observe(viewLifecycleOwner, Observer {
            initLayout()
        })
    }

    private fun initLayout() {
        mediaLists.clear()
        mediaLists.addAll(viewModel.getSelectedList())
        adapter = assignAdapter(mediaLists)
        mangaListRecyclerView.adapter = adapter

        // Handle search view with Rx
        if (parentFragment is MangaListFragment && disposable == null) {
            (parentFragment as MangaListFragment).source.subscribe(object : io.reactivex.Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(t: String) {
                    searchKeyWord = t
                    mediaLists.clear()
                    viewModel.getSelectedList().forEach { filtered ->
                        if (filtered.media?.title?.userPreferred?.toLowerCase()?.contains(searchKeyWord) == true) {
                            mediaLists.add(filtered)
                        }
                    }
                    adapter?.notifyDataSetChanged()

                    if (mediaLists.isEmpty()) {
                        mangaListRecyclerView.visibility = View.GONE
                        emptyLayout.visibility = View.VISIBLE
                    } else {
                        mangaListRecyclerView.visibility = View.VISIBLE
                        emptyLayout.visibility = View.GONE
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {

                }
            })
        }
    }

    private fun assignAdapter(list: List<MediaList>): RecyclerView.Adapter<*> {
        return when (viewModel.mangaListStyleLiveData.value?.listType ?: ListType.LINEAR) {
            ListType.LINEAR -> {
                mangaListRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                MangaListRvAdapter(activity!!, list, viewModel.scoreFormat, viewModel.mangaListStyleLiveData.value, handleListAction())
            }
            ListType.GRID -> {
                mangaListRecyclerView.layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
                MangaListGridRvAdapter(activity!!, list, viewModel.scoreFormat, viewModel.mangaListStyleLiveData.value, handleListAction())
            }
        }
    }

    private fun handleListAction() = object : MangaListListener {
        override fun openEditor(entryId: Int) {
            val intent = Intent(activity, MangaListEditorActivity::class.java)
            intent.putExtra(MangaListEditorActivity.INTENT_ENTRY_ID, entryId)
            startActivity(intent)
        }

        override fun openScoreDialog(mediaList: MediaList) {
            val setScoreDialog = SetScoreDialog()
            val bundle = Bundle()
            bundle.putString(SetScoreDialog.BUNDLE_SCORE_FORMAT, viewModel.scoreFormat.name)
            bundle.putDouble(SetScoreDialog.BUNDLE_CURRENT_SCORE, mediaList.score ?: 0.0)
            bundle.putStringArrayList(SetScoreDialog.BUNDLE_ADVANCED_SCORING, viewModel.advancedScoringList)

            if (viewModel.scoreFormat == ScoreFormat.POINT_10_DECIMAL || viewModel.scoreFormat == ScoreFormat.POINT_100) {
                val advancedScoresMap = (mediaList.advancedScores as Map<*, *>)["value"] as LinkedTreeMap<String, Double>
                val advancedScoresList = ArrayList<AdvancedScoresItem>()
                advancedScoresMap.forEach { (key, value) ->
                    advancedScoresList.add(AdvancedScoresItem(key, value))
                }
                bundle.putString(SetScoreDialog.BUNDLE_ADVANCED_SCORES_LIST, viewModel.gson.toJson(advancedScoresList))
            }

            setScoreDialog.arguments = bundle
            setScoreDialog.setListener(object : SetScoreDialog.SetScoreListener {
                override fun passScore(newScore: Double, newAdvancedScores: List<Double>?) {
                    viewModel.updateMangaScore(mediaList.id, newScore, newAdvancedScores)
                }
            })
            setScoreDialog.show(childFragmentManager, null)
        }

        override fun openProgressDialog(mediaList: MediaList, isVolume: Boolean) {
            val setProgressDialog = SetProgressDialog()
            val bundle = Bundle()
            if (isVolume) {
                bundle.putInt(SetProgressDialog.BUNDLE_CURRENT_PROGRESS, mediaList.progressVolumes ?: 0)
                if (mediaList.media?.volumes != null) {
                    bundle.putInt(SetProgressDialog.BUNDLE_TOTAL_EPISODES, mediaList.media?.volumes!!)
                }
            } else {
                bundle.putInt(SetProgressDialog.BUNDLE_CURRENT_PROGRESS, mediaList.progress ?: 0)
                if (mediaList.media?.chapters != null) {
                    bundle.putInt(SetProgressDialog.BUNDLE_TOTAL_EPISODES, mediaList.media?.chapters!!)
                }
            }
            setProgressDialog.arguments = bundle
            setProgressDialog.setListener(object : SetProgressDialog.SetProgressListener {
                override fun passProgress(newProgress: Int) {
                    handleUpdateProgressBehavior(mediaList, newProgress, isVolume)
                }
            })
            setProgressDialog.show(childFragmentManager, null)
        }

        override fun incrementProgress(mediaList: MediaList, isVolume: Boolean) {
            if (isVolume) {
                handleUpdateProgressBehavior(mediaList, mediaList.progressVolumes!! + 1, isVolume)
            } else {
                handleUpdateProgressBehavior(mediaList, mediaList.progress!! + 1, isVolume)
            }
        }
    }

    private fun handleUpdateProgressBehavior(mediaList: MediaList, newProgress: Int, isVolume: Boolean) {
        if (mediaList.progress == newProgress) {
            return
        }

        var status = mediaList.status
        var repeat = mediaList.repeat
        val maxLimit = if (isVolume) mediaList.media?.volumes else mediaList.media?.chapters

        if (newProgress == maxLimit) {
            DialogUtility.showOptionDialog(
                activity,
                R.string.move_to_completed,
                R.string.do_you_want_to_set_this_entry_into_completed,
                R.string.move,
                {
                    if (status == MediaListStatus.REPEATING) {
                        repeat = repeat!! + 1
                    }
                    status = MediaListStatus.COMPLETED
                    viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                },
                R.string.stay,
                {
                    viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                }
            )
        } else {
            when (mediaList.status) {
                MediaListStatus.PLANNING, MediaListStatus.PAUSED, MediaListStatus.DROPPED -> {
                    DialogUtility.showOptionDialog(
                        activity,
                        R.string.move_to_watching,
                        R.string.do_you_want_to_set_this_entry_into_watching,
                        R.string.move,
                        {
                            status = MediaListStatus.CURRENT
                            viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                        },
                        R.string.stay,
                        {
                            viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                        }
                    )
                }
                MediaListStatus.COMPLETED -> {
                    status = MediaListStatus.CURRENT
                    viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                }
                else -> {
                    viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
