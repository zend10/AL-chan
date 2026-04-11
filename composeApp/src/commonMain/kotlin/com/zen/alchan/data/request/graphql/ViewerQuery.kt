package com.zen.alchan.data.request.graphql

const val VIEWER_QUERY = """
    query ViewerQuery {
      Viewer {
        id
        name
        avatar {
          large
          medium
        }
        bannerImage
        about
      }
    }
"""