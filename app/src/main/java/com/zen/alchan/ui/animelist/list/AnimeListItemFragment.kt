package com.zen.alchan.ui.animelist.list


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson

import com.zen.alchan.R
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.animelist.AnimeListFragment
import com.zen.alchan.ui.animelist.editor.AnimeListEditorActivity
import com.zen.alchan.ui.general.SetProgressDialog
import com.zen.alchan.ui.general.SetScoreDialog
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_set_progress.*
import kotlinx.android.synthetic.main.dialog_set_progress.view.*
import kotlinx.android.synthetic.main.dialog_set_score.*
import kotlinx.android.synthetic.main.dialog_set_score.view.*
import kotlinx.android.synthetic.main.fragment_anime_list_item.*
import kotlinx.android.synthetic.main.layout_empty.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaListStatus

/**
 * A simple [Fragment] subclass.
 */
class AnimeListItemFragment : Fragment() {

    private val viewModel by viewModel<AnimeListItemViewModel>()

    private var adapter: AnimeListRvAdapter? = null
    private var mediaLists = ArrayList<MediaList>()

    private var disposable: Disposable? = null
    private var searchKeyWord = ""

    private val gson = Gson()

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
            viewModel.selectedStatus = arguments?.getString(BUNDLE_ANIME_LIST_STATUS)
            animeListRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            viewModel.isInit = true
        }

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.animeListData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                mediaLists.clear()
                viewModel.getSelectedList().forEach { filtered ->
                    if (filtered.media?.title?.userPreferred?.toLowerCase()?.contains(searchKeyWord) == true) {
                        mediaLists.add(filtered)
                    }
                }
                adapter?.notifyDataSetChanged()

                if (mediaLists.isEmpty()) {
                    animeListRecyclerView.visibility = View.GONE
                    emptyLayout.visibility = View.VISIBLE
                } else {
                    animeListRecyclerView.visibility = View.VISIBLE
                    emptyLayout.visibility = View.GONE
                }
            }
        })
    }

    private fun initLayout() {
        mediaLists.clear()
        mediaLists.addAll(viewModel.getSelectedList())
        adapter = assignAdapter(mediaLists)
        animeListRecyclerView.adapter = adapter

        // Handle search view with Rx
        if (parentFragment is AnimeListFragment) {
            (parentFragment as AnimeListFragment).source.subscribe(object : io.reactivex.Observer<String> {
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
                        animeListRecyclerView.visibility = View.GONE
                        emptyLayout.visibility = View.VISIBLE
                    } else {
                        animeListRecyclerView.visibility = View.VISIBLE
                        emptyLayout.visibility = View.GONE
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                    Log.d(viewModel.selectedStatus, "done")
                }
            })
        }
    }

    private fun assignAdapter(list: List<MediaList>): AnimeListRvAdapter {
        return AnimeListRvAdapter(
            activity!!,
            list,
            viewModel.scoreFormat,
            object :
                AnimeListRvAdapter.AnimeListListener {
                override fun openEditor(entryId: Int) {
                    val intent = Intent(activity, AnimeListEditorActivity::class.java)
                    intent.putExtra(AnimeListEditorActivity.INTENT_ENTRY_ID, entryId)
                    intent.putExtra(AnimeListEditorActivity.INTENT_SELECTED_STATUS, viewModel.selectedStatus)
                    startActivity(intent)
                }

                override fun openScoreDialog(mediaList: MediaList) {
                    val setScoreDialog = SetScoreDialog()
                    val bundle = Bundle()
                    bundle.putString(SetScoreDialog.BUNDLE_SCORE_FORMAT, viewModel.scoreFormat.name)
                    bundle.putString(SetScoreDialog.BUNDLE_MEDIA_LIST, gson.toJson(mediaList))
                    bundle.putStringArrayList(SetScoreDialog.BUNDLE_ADVANCED_SCORING, viewModel.advancedScoringList)
                    setScoreDialog.arguments = bundle
                    setScoreDialog.setListener(object : SetScoreDialog.SetScoreListener {
                        override fun passScore(newScore: Double, newAdvancedScores: List<Double>?) {
                            viewModel.updateAnimeScore(mediaList.id, newScore, newAdvancedScores)
                        }
                    })
                    setScoreDialog.show(childFragmentManager, null)
                }

                override fun openProgressDialog(mediaList: MediaList) {
                    val setProgressDialog = SetProgressDialog()
                    val bundle = Bundle()
                    bundle.putString(SetProgressDialog.BUNDLE_MEDIA_LIST, gson.toJson(mediaList))
                    setProgressDialog.arguments = bundle
                    setProgressDialog.setListener(object : SetProgressDialog.SetProgressListener {
                        override fun passProgress(newProgress: Int) {
                            handleUpdateProgressBehavior(mediaList, newProgress)
                        }
                    })
                    setProgressDialog.show(childFragmentManager, null)
                }

                override fun incrementProgress(mediaList: MediaList) {
                    handleUpdateProgressBehavior(mediaList, mediaList.progress!! + 1)
                }
            })
    }

    private fun handleUpdateProgressBehavior(mediaList: MediaList, newProgress: Int) {
        if (mediaList.progress == newProgress) {
            return
        }

        var status = mediaList.status
        var repeat = mediaList.repeat

        if (newProgress == mediaList.media?.episodes) {
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
                    viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
                },
                R.string.stay,
                {
                    viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
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
                            viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
                        },
                        R.string.stay,
                        {
                            viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
                        }
                    )
                }
                MediaListStatus.COMPLETED -> {
                    status = MediaListStatus.CURRENT
                    viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
                }
                else -> {
                    viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
