package com.zen.alchan.ui.browse.character


import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stfalcon.imageviewer.StfalconImageViewer

import com.zen.alchan.R
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.CharacterMedia
import com.zen.alchan.helper.pojo.CharacterVoiceActors
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.browse.staff.StaffFragment
import kotlinx.android.synthetic.main.fragment_character.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType
import type.StaffLanguage
import kotlin.math.abs

/**
 * A simple [Fragment] subclass.
 */
class CharacterFragment : BaseFragment() {

    private val viewModel by viewModel<CharacterViewModel>()

    private lateinit var scaleUpAnim: Animation
    private lateinit var scaleDownAnim: Animation

    private lateinit var adapter: CharacterMediaRvAdapter
    private lateinit var itemOpenAniList: MenuItem
    private lateinit var itemCopyLink: MenuItem

    companion object {
        const val CHARACTER_ID = "charactedId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.characterId = arguments?.getInt(CHARACTER_ID)
        scaleUpAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
        scaleDownAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)

        characterToolbar.setNavigationOnClickListener { activity?.finish() }
        characterToolbar.navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_delete)
        characterToolbar.inflateMenu(R.menu.menu_anilist_link)
        itemOpenAniList = characterToolbar.menu.findItem(R.id.itemOpenAnilist)
        itemCopyLink = characterToolbar.menu.findItem(R.id.itemCopyLink)

        adapter = assignAdapter()
        characterMediaRecyclerView.adapter = adapter

        setupObserver()
        initLayout()
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkCharacterIsFavorite()
    }

    private fun setupObserver() {
        viewModel.characterData.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (it.data?.Character() != null) {
                        viewModel.currentCharacterData = it.data.Character()
                        setupHeader()
                        handleDescription()
                    }
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.characterMediaData.observe(viewLifecycleOwner, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                viewModel.hasNextPage = it.data?.Character()?.media()?.pageInfo()?.hasNextPage() ?: false
                viewModel.page += 1
                viewModel.isInit = true

                it.data?.Character()?.media()?.edges()?.forEach { edge ->
                    val voiceActors = ArrayList<CharacterVoiceActors>()
                    edge.voiceActors()?.forEach { va ->
                        voiceActors.add(
                            CharacterVoiceActors(
                                va.id(),
                                va.name()?.full(),
                                va.image()?.large(),
                                va.language()
                            )
                        )
                    }

                    val characterMedia = CharacterMedia(
                        edge.node()?.id(),
                        edge.node()?.title()?.userPreferred(),
                        edge.node()?.coverImage()?.extraLarge(),
                        edge.node()?.type(),
                        edge.characterRole(),
                        voiceActors
                    )
                    viewModel.characterMedia.add(characterMedia)
                }

                if (viewModel.hasNextPage) {
                    viewModel.getCharacterMedia()
                } else {
                    adapter = assignAdapter()
                    characterMediaRecyclerView.adapter = adapter
                }
            }
        })

        viewModel.characterIsFavoriteData.observe(viewLifecycleOwner, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                if (it.data?.Character()?.isFavourite == true) {
                    characterFavoriteButton.text = getString(R.string.favorited)
                    characterFavoriteButton.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    characterFavoriteButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.transparent))
                    characterFavoriteButton.strokeColor = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    characterFavoriteButton.strokeWidth = 2
                } else {
                    characterFavoriteButton.text = getString(R.string.set_as_favorite)
                    characterFavoriteButton.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeBackgroundColor))
                    characterFavoriteButton.backgroundTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    characterFavoriteButton.strokeWidth = 0
                }

                characterFavoriteButton.isEnabled = true
            }
        })

        viewModel.toggleFavouriteResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    viewModel.checkCharacterIsFavorite()
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, R.string.change_saved)
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.getCharacter()
        if (!viewModel.isInit) {
            viewModel.getCharacterMedia()
        }
    }

    private fun setupHeader() {
        GlideApp.with(this).load(viewModel.currentCharacterData?.image()?.large()).apply(RequestOptions.circleCropTransform()).into(leftImage)

        if (viewModel.currentCharacterData?.image()?.large() != null) {
            leftImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(context, arrayOf(viewModel.currentCharacterData?.image()?.large())) { view, image ->
                    GlideApp.with(context!!).load(image).into(view)
                }.withTransitionFrom(leftImage).show(true)
            }
        }

        leftText.text = viewModel.currentCharacterData?.name()?.full()
        characterNativeNameText.text = viewModel.currentCharacterData?.name()?.native_()

        if (!viewModel.currentCharacterData?.name()?.alternative().isNullOrEmpty()) {
            var aliasesString = ""
            viewModel.currentCharacterData?.name()?.alternative()?.forEachIndexed { index, s ->
                aliasesString += s
                if (index != viewModel.currentCharacterData?.name()?.alternative()?.lastIndex) aliasesString += ", "
            }
            characterAliasesText.text = aliasesString
            characterAliasesText.visibility = View.VISIBLE
        } else {
            characterAliasesText.visibility = View.GONE
        }

        characterFavoriteCountText.text = viewModel.currentCharacterData?.favourites()?.toString()

        itemOpenAniList.isVisible = true
        itemCopyLink.isVisible = true

        itemOpenAniList.setOnMenuItemClickListener {
            CustomTabsIntent.Builder()
                .build()
                .launchUrl(activity!!, Uri.parse(viewModel.currentCharacterData?.siteUrl()))
            true
        }

        itemCopyLink.setOnMenuItemClickListener {
            AndroidUtility.copyToClipboard(activity, viewModel.currentCharacterData?.siteUrl()!!)
            DialogUtility.showToast(activity, R.string.link_copied)
            true
        }
    }

    private fun initLayout() {
        characterRefreshLayout.setOnRefreshListener {
            characterRefreshLayout.isRefreshing = false
            viewModel.getCharacter()
            if (!viewModel.isInit) {
                viewModel.getCharacterMedia()
            }
        }

        characterAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            // disable refresh when toolbar is not fully expanded
            characterRefreshLayout.isEnabled = verticalOffset == 0

            // 50 is magic number gotten from trial and error
            if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                if (characterBannerContentLayout.isVisible) {
                    characterBannerContentLayout.startAnimation(scaleDownAnim)
                    characterBannerContentLayout.visibility = View.INVISIBLE
                }
            } else {
                if (characterBannerContentLayout.isInvisible) {
                    characterBannerContentLayout.startAnimation(scaleUpAnim)
                    characterBannerContentLayout.visibility = View.VISIBLE
                }
            }
        })

        voiceActorLanguageText.text = viewModel.staffLanguage.name

        voiceActorLanguageText.setOnClickListener {
            MaterialAlertDialogBuilder(activity)
                .setItems(viewModel.staffLanguageArray) { _, which ->
                    viewModel.staffLanguage = StaffLanguage.valueOf(viewModel.staffLanguageArray[which])
                    voiceActorLanguageText.text = viewModel.staffLanguageArray[which]
                    adapter = assignAdapter()
                    characterMediaRecyclerView.adapter = adapter
                }
                .show()
        }

        characterFavoriteButton.setOnClickListener {
            viewModel.updateFavorite()
        }
    }

    private fun handleDescription() {
        val spoilerRegex = "(?<=<span class='markdown_spoiler'><span>).+?(?=<\\/span><\\/span>)".toRegex()
        val spoilerTag = "[Spoiler]"
        val spoilerText = spoilerRegex.findAll(viewModel.currentCharacterData?.description() ?: "No description.").toList()
        val spoilerDescription = viewModel.currentCharacterData?.description()?.replace(spoilerRegex, spoilerTag)
        val spanned = HtmlCompat.fromHtml(spoilerDescription ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)

        val spannableString = SpannableString(spanned.toString())

        var findIndex = spanned.indexOf(spoilerTag)
        var counter = 0
        while (findIndex != -1) {
            val clickableSpan = object : ClickableSpan() {
                val internalCounter = counter
                override fun onClick(widget: View) {
                    DialogUtility.showInfoDialog(activity, HtmlCompat.fromHtml(spoilerText[internalCounter].value, HtmlCompat.FROM_HTML_MODE_LEGACY).toString())
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color =  AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeNegativeColor)
                }
            }
            spannableString.setSpan(clickableSpan, findIndex, findIndex + spoilerTag.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            counter += 1
            findIndex = spanned.indexOf(spoilerTag, findIndex + spoilerTag.length)
        }

        characterDescriptionText.text = spannableString
        characterDescriptionText.movementMethod = LinkMovementMethod.getInstance()

        characterDescriptionArrow.setOnClickListener {
            if (dummyCharacterDescriptionText.isVisible) {
                dummyCharacterDescriptionText.visibility = View.GONE
                GlideApp.with(this).load(R.drawable.ic_chevron_up).into(characterDescriptionArrow)
            } else {
                dummyCharacterDescriptionText.visibility = View.VISIBLE
                GlideApp.with(this).load(R.drawable.ic_chevron_down).into(characterDescriptionArrow)
            }
        }
    }

    private fun assignAdapter(): CharacterMediaRvAdapter {
        return CharacterMediaRvAdapter(activity!!, viewModel.characterMedia, viewModel.staffLanguage, object : CharacterMediaRvAdapter.CharacterMediaListener {
            override fun passSelectedMedia(mediaId: Int, mediaType: MediaType) {
                val fragment = MediaFragment()
                val bundle = Bundle()
                bundle.putInt(MediaFragment.MEDIA_ID, mediaId)
                bundle.putString(MediaFragment.MEDIA_TYPE, mediaType.name)
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
}
