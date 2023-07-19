package com.zen.alchan.data.network.apollo.adapter

import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.AnyAdapter
import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter

class CountryCodeAdapter : Adapter<String> {

    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): String {
        return AnyAdapter.fromJson(reader, customScalarAdapters) as String
    }

    override fun toJson(
        writer: JsonWriter,
        customScalarAdapters: CustomScalarAdapters,
        value: String
    ) {
        AnyAdapter.toJson(writer, customScalarAdapters, value)
    }
}