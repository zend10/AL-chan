package com.zen.alchan.ui.browse.staff


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
import com.stfalcon.imageviewer.StfalconImageViewer

import com.zen.alchan.R
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.StaffCharacter
import com.zen.alchan.helper.pojo.StaffMedia
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.character.CharacterFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import kotlinx.android.synthetic.main.fragment_staff.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType
import kotlin.math.abs

/**
 * A simple [Fragment] subclass.
 */
class StaffFragment : BaseFragment() {

    private val viewModel by viewModel<StaffViewModel>()

    private lateinit var scaleUpAnim: Animation
    private lateinit var scaleDownAnim: Animation

    private lateinit var characterAdapter: StaffCharacterRvAdapter
    private lateinit var mediaAdapter: StaffMediaRvAdapter

    private lateinit var itemOpenAniList: MenuItem
    private lateinit var itemCopyLink: MenuItem

    companion object {
        const val STAFF_ID = "staffId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.staffId = arguments?.getInt(STAFF_ID)
        scaleUpAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
        scaleDownAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)

        staffToolbar.setNavigationOnClickListener { activity?.finish() }
        staffToolbar.navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_delete)
        staffToolbar.inflateMenu(R.menu.menu_anilist_link)
        itemOpenAniList = staffToolbar.menu.findItem(R.id.itemOpenAnilist)
        itemCopyLink = staffToolbar.menu.findItem(R.id.itemCopyLink)

        characterAdapter = assignCharacterAdapter()
        staffVoiceRecyclerView.adapter = characterAdapter

        mediaAdapter = assignMediaAdapter()
        staffRolesRecyclerView.adapter = mediaAdapter

        setupObserver()
        initLayout()
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkStaffIsFavorite()
    }

    private fun setupObserver() {
        viewModel.staffData.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (it.data?.Staff() != null) {
                        viewModel.currentStaffData = it.data.Staff()
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

        viewModel.staffCharacterData.observe(viewLifecycleOwner, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                viewModel.characterHasNextPage = it.data?.Staff()?.characters()?.pageInfo()?.hasNextPage() ?: false
                viewModel.characterPage += 1
                viewModel.isCharacterInit = true

                it.data?.Staff()?.characters()?.edges()?.forEach { edge ->
                    edge.media()?.forEach { media ->
                        val staffCharacter = StaffCharacter(
                            media.id(),
                            media.title()?.userPreferred(),
                            media.coverImage()?.extraLarge(),
                            media.type(),
                            edge.role(),
                            edge.node()?.id(),
                            edge.node()?.name()?.full(),
                            edge.node()?.image()?.large()
                        )
                        viewModel.staffCharacterList.add(staffCharacter)
                    }
                }

                if (viewModel.characterHasNextPage) {
                    viewModel.getStaffCharacter()
                } else {
                    characterAdapter = assignCharacterAdapter()
                    staffVoiceRecyclerView.adapter = characterAdapter

                    if (viewModel.staffCharacterList.isNullOrEmpty()) {
                        staffVoiceLayout.visibility = View.GONE
                    } else {
                        staffVoiceLayout.visibility = View.VISIBLE
                    }
                }
            }
        })

        viewModel.staffMediaData.observe(viewLifecycleOwner, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                viewModel.mediaHasNextPage = it.data?.Staff()?.staffMedia()?.pageInfo()?.hasNextPage() ?: false
                viewModel.mediaPage += 1
                viewModel.isMediaInit = true

                it.data?.Staff()?.staffMedia()?.edges()?.forEach { edge ->
                    val staffMedia = StaffMedia(
                        edge.node()?.id(),
                        edge.node()?.title()?.userPreferred(),
                        edge.node()?.coverImage()?.extraLarge(),
                        edge.node()?.type(),
                        edge.staffRole()
                    )
                    viewModel.staffMediaList.add(staffMedia)
                }

                if (viewModel.mediaHasNextPage) {
                    viewModel.getStaffMedia()
                } else {
                    mediaAdapter = assignMediaAdapter()
                    staffRolesRecyclerView.adapter = mediaAdapter

                    if (viewModel.staffMediaList.isNullOrEmpty()) {
                        staffRolesLayout.visibility = View.GONE
                    } else {
                        staffRolesLayout.visibility = View.VISIBLE
                    }
                }
            }
        })

        viewModel.staffIsFavoriteData.observe(viewLifecycleOwner, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                if (it.data?.Staff()?.isFavourite == true) {
                    staffFavoriteButton.text = getString(R.string.favorited)
                    staffFavoriteButton.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    staffFavoriteButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.transparent))
                    staffFavoriteButton.strokeColor = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    staffFavoriteButton.strokeWidth = 2
                } else {
                    staffFavoriteButton.text = getString(R.string.set_as_favorite)
                    staffFavoriteButton.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeBackgroundColor))
                    staffFavoriteButton.backgroundTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    staffFavoriteButton.strokeWidth = 0
                }

                staffFavoriteButton.isEnabled = true
            }
        })

        viewModel.toggleFavouriteResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    viewModel.checkStaffIsFavorite()
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, R.string.change_saved)
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.getStaff()

        if (!viewModel.isCharacterInit) {
            viewModel.getStaffCharacter()
        }

        if (!viewModel.isMediaInit) {
            viewModel.getStaffMedia()
        }
    }

    private fun setupHeader() {
        GlideApp.with(this).load(viewModel.currentStaffData?.image()?.large()).apply(RequestOptions.circleCropTransform()).into(leftImage)

        if (viewModel.currentStaffData?.image()?.large() != null) {
            leftImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(context, arrayOf(viewModel.currentStaffData?.image()?.large())) { view, image ->
                    GlideApp.with(context!!).load(image).into(view)
                }.withTransitionFrom(leftImage).show(true)
            }
        }

        leftText.text = viewModel.currentStaffData?.name()?.full()
        staffNativeNameText.text = viewModel.currentStaffData?.name()?.native_()

        if (!viewModel.currentStaffData?.name()?.alternative().isNullOrEmpty()) {
            var aliasesString = ""
            viewModel.currentStaffData?.name()?.alternative()?.forEachIndexed { index, s ->
                aliasesString += s
                if (index != viewModel.currentStaffData?.name()?.alternative()?.lastIndex) aliasesString += ", "
            }
            staffAliasesText.text = aliasesString
            staffAliasesText.visibility = View.VISIBLE
        } else {
            staffAliasesText.visibility = View.GONE
        }

        staffFavoriteCountText.text = viewModel.currentStaffData?.favourites()?.toString()

        itemOpenAniList.isVisible = true
        itemCopyLink.isVisible = true

        itemOpenAniList.setOnMenuItemClickListener {
            CustomTabsIntent.Builder()
                .build()
                .launchUrl(activity!!, Uri.parse(viewModel.currentStaffData?.siteUrl()))
            true
        }

        itemCopyLink.setOnMenuItemClickListener {
            AndroidUtility.copyToClipboard(activity, viewModel.currentStaffData?.siteUrl()!!)
            DialogUtility.showToast(activity, R.string.link_copied)
            true
        }
    }

    private fun initLayout() {
        staffRefreshLayout.setOnRefreshListener {
            staffRefreshLayout.isRefreshing = false
            viewModel.getStaff()

            if (viewModel.isCharacterInit) {
                viewModel.getStaffCharacter()
            }

            if (!viewModel.isMediaInit) {
                viewModel.getStaffMedia()
            }
        }

        staffAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            // disable refresh when toolbar is not fully expanded
            staffRefreshLayout.isEnabled = verticalOffset == 0

            // 50 is magic number gotten from trial and error
            if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                if (staffBannerContentLayout.isVisible) {
                    staffBannerContentLayout.startAnimation(scaleDownAnim)
                    staffBannerContentLayout.visibility = View.INVISIBLE
                }
            } else {
                if (staffBannerContentLayout.isInvisible) {
                    staffBannerContentLayout.startAnimation(scaleUpAnim)
                    staffBannerContentLayout.visibility = View.VISIBLE
                }
            }
        })

        staffFavoriteButton.setOnClickListener {
            viewModel.updateFavorite()
        }
    }

    private fun handleDescription() {
        val urlRegex = "(?<=<a href=\").+?(?=\">)".toRegex()
        val linkRegex =  "(?<=\">).+?(?=<\\/a>)".toRegex()
        val description = viewModel.currentStaffData?.description() ?: "No description."
        val urlList = urlRegex.findAll(description).toList()
        val linkList = linkRegex.findAll(description).toList()
        val spanned = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY)

        val spannableString = SpannableString(spanned.toString())

        if (!linkList.isNullOrEmpty()) {
            var counter = 0
            var findIndex = spanned.indexOf(linkList[counter].value)

            while (findIndex != -1) {
                val clickableSpan = object : ClickableSpan() {
                    val internalCounter = counter

                    override fun onClick(widget: View) {
                        CustomTabsIntent.Builder()
                            .build()
                            .launchUrl(activity!!, Uri.parse(urlList[internalCounter].value))
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                        ds.color =  AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeNegativeColor)
                    }
                }
                spannableString.setSpan(clickableSpan, findIndex, findIndex + linkList[counter].value.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                counter += 1
                findIndex = if (counter < linkList.size) {
                    spanned.indexOf(linkList[counter].value, findIndex + linkList[counter - 1].value.length)
                } else {
                    -1
                }
            }
        }

        staffDescriptionText.text = spannableString
        staffDescriptionText.movementMethod = LinkMovementMethod.getInstance()

        staffDescriptionArrow.setOnClickListener {
            if (dummyStaffDescriptionText.isVisible) {
                dummyStaffDescriptionText.visibility = View.GONE
                GlideApp.with(this).load(R.drawable.ic_chevron_up).into(staffDescriptionArrow)
            } else {
                dummyStaffDescriptionText.visibility = View.VISIBLE
                GlideApp.with(this).load(R.drawable.ic_chevron_down).into(staffDescriptionArrow)
            }
        }
    }

    private fun assignCharacterAdapter(): StaffCharacterRvAdapter {
        return StaffCharacterRvAdapter(activity!!, viewModel.staffCharacterList, object : StaffCharacterRvAdapter.StaffCharacterListener {
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

    private fun assignMediaAdapter(): StaffMediaRvAdapter {
        return StaffMediaRvAdapter(activity!!, viewModel.staffMediaList, object : StaffMediaRvAdapter.StaffMediaListener {
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
}
