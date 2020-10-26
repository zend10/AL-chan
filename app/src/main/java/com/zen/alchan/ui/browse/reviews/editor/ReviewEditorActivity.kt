package com.zen.alchan.ui.browse.reviews.editor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import com.zen.alchan.R
import com.zen.alchan.helper.changeStatusBarColor
import com.zen.alchan.helper.enums.EditorType
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.common.SetScoreDialog
import com.zen.alchan.ui.common.TextEditorActivity
import kotlinx.android.synthetic.main.activity_anime_list_editor.*
import kotlinx.android.synthetic.main.activity_review_editor.*
import kotlinx.android.synthetic.main.dialog_input.*
import kotlinx.android.synthetic.main.dialog_input.view.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType
import type.ScoreFormat
import java.util.*
import kotlin.collections.ArrayList

class ReviewEditorActivity : BaseActivity() {

    private val viewModel by viewModel<ReviewEditorViewModel>()

    companion object {
        const val REVIEW_ID = "reviewId"
        const val MEDIA_ID = "mediaId"
        const val IS_DELETE = "isDelete"

        const val ACTIVITY_EDITOR = 200

        private const val MIN_SUMMARY_LENGTH = 20
        private const val MAX_SUMMARY_LENGTH = 120

        private const val MIN_REVIEW_LENGTH = 2200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_editor)

        changeStatusBarColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))
        setSupportActionBar(toolbarLayout)

        viewModel.reviewId = intent.getIntExtra(REVIEW_ID, 0)
        viewModel.mediaId = intent.getIntExtra(MEDIA_ID, 0)

        supportActionBar?.title = if (viewModel.reviewId == 0) {
            getString(R.string.new_review)
        } else {
            getString(R.string.edit_review)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_delete)

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.reviewDetailData.observe(this, androidx.lifecycle.Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (viewModel.reviewId == it.data?.review?.id) {
                        viewModel.reviewString = it.data?.review?.body ?: ""
                        viewModel.summaryString = it.data?.review?.summary ?: ""
                        viewModel.score = it.data?.review?.score ?: 0
                        viewModel.isPrivate = it.data?.review?.private_ ?: false
                        initLayout()
                    }
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    if (viewModel.reviewId == it.data?.review?.id) {
                        DialogUtility.showToast(this, it.message)
                    }
                }
            }
        })

        viewModel.saveReviewResponse.observe(this, androidx.lifecycle.Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    val intent = Intent()
                    intent.putExtra(IS_DELETE, false)
                    setResult(Activity.RESULT_OK, intent)
                    finish()

                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, it.message)
                }
            }
        })

        viewModel.deleteReviewResponse.observe(this, androidx.lifecycle.Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    val intent = Intent()
                    intent.putExtra(IS_DELETE, true)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, it.message)
                }
            }
        })

        if (viewModel.reviewId != null && viewModel.reviewId != 0) {
            viewModel.getReviewDetail()
        }
    }

    private fun initLayout() {
        reviewText.text = if (viewModel.reviewString.isBlank()) {
            getString(R.string.write)
        } else {
            getString(R.string.edit)
        }

        reviewWarningText.visibility = if (viewModel.reviewString.length < MIN_REVIEW_LENGTH) {
            View.VISIBLE
        } else {
            View.GONE
        }

        reviewText.setOnClickListener {
            val intent = Intent(this, TextEditorActivity::class.java)
            intent.putExtra(TextEditorActivity.EDITOR_TYPE, EditorType.REVIEW.name)
            intent.putExtra(TextEditorActivity.TEXT_CONTENT, viewModel.reviewString)
            startActivityForResult(intent, EditorType.REVIEW.ordinal)
        }

        reviewSummaryText.addTextChangedListener {
            viewModel.summaryString = it.toString()

            if (viewModel.summaryString.trim().length < MIN_SUMMARY_LENGTH || viewModel.summaryString.trim().length > MAX_SUMMARY_LENGTH) {
                reviewSummaryWarningText.visibility = View.VISIBLE
                reviewSummaryCountText.setTextColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeNegativeColor))
            } else {
                reviewSummaryWarningText.visibility = View.GONE
                reviewSummaryCountText.setTextColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeSecondaryColor))
            }

            reviewSummaryCountText.text = "${viewModel.summaryString.trim().length.toString()} / $MAX_SUMMARY_LENGTH"
        }

        reviewSummaryText.setText(viewModel.summaryString)

        reviewScoreText.text = viewModel.score.toString()

        reviewScoreText.setOnClickListener {
            val dialog = SetScoreDialog()
            dialog.setListener(object : SetScoreDialog.SetScoreListener {
                override fun passScore(newScore: Double, newAdvancedScores: List<Double>?) {
                    viewModel.score = newScore.toInt()
                    reviewScoreText.text = viewModel.score.toString()
                }
            })
            val bundle = Bundle()
            bundle.putString(SetScoreDialog.BUNDLE_SCORE_FORMAT, ScoreFormat.POINT_100.name)
            bundle.putDouble(SetScoreDialog.BUNDLE_CURRENT_SCORE, viewModel.score.toDouble())
            bundle.putStringArrayList(SetScoreDialog.BUNDLE_ADVANCED_SCORING, ArrayList())
            dialog.arguments = bundle
            dialog.show(supportFragmentManager, null)
        }

        setPrivateCheckBox.isChecked = viewModel.isPrivate

        setPrivateCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isPrivate = isChecked
        }

        setPrivateText.setOnClickListener { setPrivateCheckBox.performClick() }

        deleteReviewButton.visibility = if (viewModel.reviewId != 0) {
            View.VISIBLE
        } else {
            View.GONE
        }

        deleteReviewButton.setOnClickListener {
            DialogUtility.showOptionDialog(
                this,
                R.string.delete_review,
                R.string.are_you_sure_you_want_to_delete_this_review,
                R.string.delete,
                {
                    viewModel.deleteReview()
                },
                R.string.cancel,
                { }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemPost) {
            if (viewModel.reviewString.replace(" ", "").replace("\n", "").length < MIN_REVIEW_LENGTH ||
                viewModel.summaryString.trim().length < MIN_SUMMARY_LENGTH ||
                viewModel.summaryString.trim().length > MAX_SUMMARY_LENGTH ||
                viewModel.score <= 0
            ) {
                DialogUtility.showToast(this, R.string.you_need_to_hit_the_character_limit_to_submit_your_review)
                return false
            }

            DialogUtility.showOptionDialog(
                this,
                R.string.post_review,
                R.string.are_you_sure_you_want_to_post_this_review,
                R.string.post,
                {
                    viewModel.saveReview()
                },
                R.string.cancel,
                { }
            )

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EditorType.REVIEW.ordinal && resultCode == Activity.RESULT_OK) {
            viewModel.reviewString = data?.extras?.get(TextEditorActivity.TEXT_CONTENT)?.toString() ?: ""

            reviewText.text = if (viewModel.reviewString.isBlank()) {
                getString(R.string.write)
            } else {
                getString(R.string.edit)
            }

            reviewWarningText.visibility = if (viewModel.reviewString.replace(" ", "").replace("\n", "").length < MIN_REVIEW_LENGTH) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}