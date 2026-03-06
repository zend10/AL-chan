package com.zen.alchan.data.request.graphql

const val TRENDING_MEDIA = """
    query HomeDataQuery {
      trendingAnime: Page(page: 1, perPage: 10) {
        data: media(type: ANIME, sort: [TRENDING_DESC], isAdult: false) {
          id
          idMal
          title {
            romaji
            english
            native
            userPreferred
          }
          bannerImage
          coverImage {
            color
            extraLarge
            large
            medium
          }
          episodes
          chapters
          volumes
          nextAiringEpisode {
            episode
          }
        }
      }
      trendingManga: Page(page: 1, perPage: 10) {
        data: media(type: MANGA, sort: [TRENDING_DESC], isAdult: false) {
          id
          idMal
          title {
            romaji
            english
            native
            userPreferred
          }
          bannerImage
          coverImage {
            color
            extraLarge
            large
            medium
          }
          episodes
          chapters
          volumes
          nextAiringEpisode {
            episode
          }
        }
      }
    }
"""