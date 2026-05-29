package com.zen.alchan.data.request.graphql

const val VIEWER_QUERY = """
    query ViewerQuery {
      Viewer {
        id
        name
        about
        avatar {
          large
          medium
        }
        bannerImage
        isFollowing
        isFollower
        isBlocked
        options {
          titleLanguage
          displayAdultContent
          airingNotifications
          profileColor
          notificationOptions {
            type
            enabled
          }
          timezone
          activityMergeTime
          staffNameLanguage
          restrictMessagesToFollowing
          disabledListActivity {
            disabled
            type
          }
        }
        mediaListOptions {
          scoreFormat
          rowOrder
          animeList {
            sectionOrder
            splitCompletedSectionByFormat
            customLists
            advancedScoring
            advancedScoringEnabled
          }
          mangaList {
            sectionOrder
            splitCompletedSectionByFormat
            customLists
            advancedScoring
            advancedScoringEnabled
          }
        }
        unreadNotificationCount
        siteUrl
        donatorTier
        donatorBadge
        moderatorRoles
        createdAt
        updatedAt
        previousNames {
          name
          createdAt
          updatedAt
        }
      }
    }
"""
