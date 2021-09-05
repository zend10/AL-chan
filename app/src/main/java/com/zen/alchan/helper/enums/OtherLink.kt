package com.zen.alchan.helper.enums

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.convertFromSnakeCase

enum class OtherLink(val key: String, val siteName: String, @ColorInt val hexColor: Int, @DrawableRes val icon: Int?) {
    ANILIST("anilist", "AniList", Color.parseColor("#324760"), R.drawable.ic_anilist),
    TWITTER("twitter", "Twitter", Color.parseColor("#03A9F4"), R.drawable.ic_twitter),
    CRUNCHYROLL("crunchyroll", "Crunchyroll", Color.parseColor("#FF9100"), null),
    YOUTUBE("youtube", "Youtube", Color.parseColor("#FF0000"), null),
    FUNIMATION("funimation", "Funimation", Color.parseColor("#452C8A"), null),
    HIDIVE("hidive", "Hidive", Color.parseColor("#03A8EB"), null),
    VRV("vrv", "VRV", Color.parseColor("#FEDD01"), null),
    NETFLIX("netflix", "Netflix", Color.parseColor("#F44335"), null),
    AMAZON("amazon", "Amazon", Color.parseColor("#04A3DD"), null),
    HULU("hulu", "Hulu", Color.parseColor("#8AC34A"), null),
    HBO_MAX("hbomax", "HBO Max", Color.parseColor("#9531EC"), null),
    ANIMELAB("animelab", "AnimeLab", Color.parseColor("#3B0087"), null),
    VIZ("viz", "Viz", Color.parseColor("#FF0000"), null),
    ADULT_SWIM("adultswim", "Adult Swim", Color.parseColor("#171717"), null),
    RETRO_CRUSH("retrocrush", "RetroCrush", Color.parseColor("#000000"), null),
    MIDNIGHT_PULP("midnightpulp.com", "Midnight Pulp", Color.parseColor("#B7F00F"), null),
    TUBI_TV("tubitv.com", "Tubi TV", Color.parseColor("#F84C18"), null),
    CONTV("contv.com", "CONtv", Color.parseColor("#E35623"), null),
    MANGA_PLUS("mangaplus.shueisha.co.jp", "Manga Plus", Color.parseColor("#DC0812"), null),
    MANGA_CLUB("manga.club", "Manga.Club", Color.parseColor("#F47D30"), null),
    FAKKU("fakku", "Fakku", Color.parseColor("#911918"), null),
    WEBTOONS("webtoons.com/en", "Webtoons", Color.parseColor("#04CF62"), null),
    LEZHIN("lezhin.com/en", "Lezhin", Color.parseColor("#E71D31"), null),
    TOOMICS("global.toomics.com", "Toomics", Color.parseColor("#EB2C2C"), null),
    WEB_COMICS("webcomicsapp.com", "Web Comics", Color.parseColor("#F7745E"), null),
    COMICWALKER("comic-walker", "ComicWalker (JP)", Color.parseColor("#F80003"), null),
    PIXIV_COMIC("comic.pixiv.net", "Pixiv Comic (JP)", Color.parseColor("#088ED5"), null),
    COMICO("comico.jp", "Comico (JP)", Color.parseColor("#EE0208"), null),
    MANGABOX("mangabox", "Mangabox (JP)", Color.parseColor("#3999B8"), null),
    PIXIV_NOVEL("novel.pixiv.net", "Pixiv Novel (JP)", Color.parseColor("#088ED5"), null),
    PICCOMA("piccoma.com", "Piccoma (JP)", Color.parseColor("#F3C016"), null),
    POCKET_MAGAZINE("pocket.shonenmagazine.com", "Pocket Magazine (JP)", Color.parseColor("#0C2F89"), null),
    NICO_NICO_SEIGA("seiga.nicovideo.jp", "Nico Nico Seiga (JP)", Color.parseColor("#323232"), null),
    SHONEN_JUMP_PLUS("shonenjumpplus.com", "Shonen Jump Plus (JP)", Color.parseColor("#E60109"), null),
    LEZHIN_KO("lezhin.com/ko", "Lezhin (KO)", Color.parseColor("#E71D31"), null),
    NAVER("naver", "Naver (KO)", Color.parseColor("#00CE63"), null),
    DAUM_WEBTOON("webtoon.daum.net", "Daum Webtoon (KO)", Color.parseColor("#F82E40"), null),
    TOOMICS_KO("toomics.com", "Toomics (KO)", Color.parseColor("#EB2C2C"), null),
    BOMTOON("bomtoon", "Bomtoon (KO)", Color.parseColor("#F82BA8"), null),
    KAKAOPAGE("kakao", "KakaoPage (KO)", Color.parseColor("#F8CD01"), null),
    KUAIKAN_MANHUA("Kuaikanmanhua", "KuaiKan Manhua (CN)", Color.parseColor("#F8D00B"), null),
    QQ("ac.qq.com", "QQ (CN)", Color.parseColor("#FB9144"), null),
    DAJIAOCHONG_MANHUA("dajiaochongmanhua.com", "Dajiaochong Manhua (CN)", Color.parseColor("#E9CE0E"), null),
    WEIBO_MANHUA("manhua.weibo.com", "Weibo Manhua (CN)", Color.parseColor("#E0172B"), null),
    MANMAN_MANHUA("manmanapp.com", "Manman Manhua (CN)", Color.parseColor("#FF5746"), null)
}

fun OtherLink.getString(): String {
    return siteName
}