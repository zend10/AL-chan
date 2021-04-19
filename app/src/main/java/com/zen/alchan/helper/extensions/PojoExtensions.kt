package com.zen.alchan.helper.extensions

import com.zen.alchan.helper.pojo.Genre


fun Genre.getHexColor(): String {
    return when (name) {
        "Action" -> "#24687B"
        "Adventure" -> "#014037"
        "Comedy" -> "#E6977E"
        "Drama" -> "#7E1416"
        "Ecchi" -> "#7E174A"
        "Fantasy" -> "#989D60"
        "Hentai" -> "#37286B"
        "Horror" -> "#5B1765"
        "Mahou Shoujo" -> "#BF5264"
        "Mecha" -> "#542437"
        "Music" -> "#329669"
        "Mystery" -> "#3D3251"
        "Psychological" -> "#D85C43"
        "Romance" -> "#C02944"
        "Sci-Fi" -> "#85B14B"
        "Slice of Life" -> "#D3B042"
        "Sports" -> "#6B9145"
        "Supernatural" -> "#338074"
        "Thriller" -> "#224C80"
        else -> "#00000000"
    }
}