package com.zen.alchan.ui.browse.character


import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebChromeClient
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
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.handleSpoilerAndLink
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.CharacterMedia
import com.zen.alchan.helper.pojo.CharacterVoiceActors
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.browse.media.characters.MediaCharactersRvAdapter
import com.zen.alchan.ui.browse.staff.StaffFragment
import com.zen.alchan.ui.browse.user.UserFragment
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
    private lateinit var voiceActorAdapter: CharacterVoiceActorRvAdapter
    private var itemOpenAniList: MenuItem? = null
    private var itemCopyLink: MenuItem? = null

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

        voiceActorAdapter = assignVoiceActorAdapter()
        characterVoiceActorsRecyclerView.adapter = voiceActorAdapter

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
                    if (it.data?.character != null) {
                        viewModel.currentCharacterData = it.data.character
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
                if (!viewModel.hasNextPage) {
                    return@Observer
                }

                viewModel.hasNextPage = it.data?.character?.media?.pageInfo?.hasNextPage ?: false
                viewModel.page += 1
                viewModel.isInit = true

                it.data?.character?.media?.edges?.forEach { edge ->
                    val characterMedia = CharacterMedia(
                        edge?.node?.id,
                        edge?.node?.title?.userPreferred,
                        edge?.node?.coverImage?.large,
                        edge?.node?.type,
                        edge?.node?.format,
                        edge?.characterRole
                    )
                    viewModel.characterMedia.add(characterMedia)

                    edge?.voiceActors?.forEach { va ->
                        val findVa = viewModel.characterVoiceActors.find { cva -> cva.voiceActorId == va?.id }
                        if (findVa != null) {
                            val vaIndex = viewModel.characterVoiceActors.indexOf(findVa)
                            viewModel.characterVoiceActors[vaIndex].characterMediaList?.add(characterMedia)
                        } else {
                            val voiceActor = CharacterVoiceActors(
                                va?.id, va?.name?.full, va?.image?.large, va?.language, arrayListOf(characterMedia)
                            )
                            viewModel.characterVoiceActors.add(voiceActor)
                        }
                    }
                }

                if (viewModel.hasNextPage) {
                    viewModel.getCharacterMedia()
                } else {
                    adapter = assignAdapter()
                    characterMediaRecyclerView.adapter = adapter

                    voiceActorAdapter = assignVoiceActorAdapter()
                    characterVoiceActorsRecyclerView.adapter = voiceActorAdapter
                    characterVoiceActorsLayout.visibility = if (viewModel.characterVoiceActors.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
            }
        })

        viewModel.characterIsFavoriteData.observe(viewLifecycleOwner, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                if (it.data?.character?.isFavourite == true) {
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
        GlideApp.with(this).load(viewModel.currentCharacterData?.image?.large).apply(RequestOptions.circleCropTransform()).into(characterImage)

        if (viewModel.currentCharacterData?.image?.large != null) {
            characterImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(context, arrayOf(viewModel.currentCharacterData?.image?.large)) { view, image ->
                    GlideApp.with(context!!).load(image).into(view)
                }.withTransitionFrom(characterImage).show(true)
            }
        }

        characterNameText.text = viewModel.currentCharacterData?.name?.full
        characterNativeNameText.text = viewModel.currentCharacterData?.name?.native_

        if (!viewModel.currentCharacterData?.name?.alternative.isNullOrEmpty()) {
            var aliasesString = ""
            viewModel.currentCharacterData?.name?.alternative?.forEachIndexed { index, s ->
                aliasesString += s
                if (index != viewModel.currentCharacterData?.name?.alternative?.lastIndex) aliasesString += ", "
            }
            characterAliasesText.text = aliasesString

            if (aliasesString.isBlank()) {
                characterAliasesText.visibility = View.GONE
            } else {
                characterAliasesText.visibility = View.VISIBLE
            }
        } else {
            characterAliasesText.visibility = View.GONE
        }

        characterFavoriteCountText.text = viewModel.currentCharacterData?.favourites?.toString()

        itemOpenAniList?.isVisible = true
        itemCopyLink?.isVisible = true

        itemOpenAniList?.setOnMenuItemClickListener {
            CustomTabsIntent.Builder()
                .build()
                .launchUrl(activity!!, Uri.parse(viewModel.currentCharacterData?.siteUrl))
            true
        }

        itemCopyLink?.setOnMenuItemClickListener {
            AndroidUtility.copyToClipboard(activity, viewModel.currentCharacterData?.siteUrl!!)
            DialogUtility.showToast(activity, R.string.link_copied)
            true
        }
    }

    private fun initLayout() {
        characterRefreshLayout.setOnRefreshListener {
            characterRefreshLayout.isRefreshing = false
            viewModel.getCharacter()
            viewModel.checkCharacterIsFavorite()
            if (!viewModel.isInit) {
                viewModel.getCharacterMedia()
            }
        }

        characterAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            // disable refresh when toolbar is not fully expanded
            characterRefreshLayout?.isEnabled = verticalOffset == 0

            // 50 is magic number gotten from trial and error
            if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                if (characterBannerContentLayout?.isVisible == true) {
                    characterBannerContentLayout?.startAnimation(scaleDownAnim)
                    characterBannerContentLayout?.visibility = View.INVISIBLE
                }
            } else {
                if (characterBannerContentLayout?.isInvisible == true) {
                    characterBannerContentLayout?.startAnimation(scaleUpAnim)
                    characterBannerContentLayout?.visibility = View.VISIBLE
                }
            }
        })

        characterFavoriteButton.setOnClickListener {
            viewModel.updateFavorite()
        }

        characterVoiceActorsLayout.visibility = if (viewModel.characterVoiceActors.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun handleDescription() {
        characterDescriptionText.text = viewModel.currentCharacterData?.description?.handleSpoilerAndLink(activity!!) { page, id ->
            listener?.changeFragment(page, id)
        }
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
        return CharacterMediaRvAdapter(activity!!, viewModel.characterMedia, object : CharacterMediaRvAdapter.CharacterMediaListener {
            override fun passSelectedMedia(mediaId: Int, mediaType: MediaType) {
                listener?.changeFragment(BrowsePage.valueOf(mediaType.name), mediaId)
            }
        })
    }

    private fun assignVoiceActorAdapter(): CharacterVoiceActorRvAdapter {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val width = metrics.widthPixels / resources.getInteger(R.integer.horizontalListCharacterDivider)
        return CharacterVoiceActorRvAdapter(activity!!, viewModel.characterVoiceActors, width, object : CharacterVoiceActorRvAdapter.CharacterVoiceActorListener {
            override fun passSelectedVoiceActor(voiceActorId: Int) {
                listener?.changeFragment(BrowsePage.STAFF, voiceActorId)
            }

            override fun showMediaList(list: List<CharacterMedia>) {
                val titleList = ArrayList<String>()
                list.forEach {
                    titleList.add("${it.mediaTitle} (${it.mediaFormat})")
                }
                MaterialAlertDialogBuilder(requireActivity())
                    .setItems(titleList.toTypedArray()) { _, which ->
                        listener?.changeFragment(BrowsePage.valueOf(list[which].mediaType?.name!!), list[which].mediaId!!)
                    }
                    .show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        characterMediaRecyclerView.adapter = null
        characterVoiceActorsRecyclerView.adapter = null
        itemOpenAniList = null
        itemCopyLink = null
    }
}
