package com.zen.alchan.data.network.apollo.adapter

import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.AnyAdapter
import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter

class JsonAdapter : Adapter<Any> {

    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): Any {
        return AnyAdapter.fromJson(reader, customScalarAdapters)
    }

    override fun toJson(
        writer: JsonWriter,
        customScalarAdapters: CustomScalarAdapters,
        value: Any
    ) {
        AnyAdapter.toJson(writer, customScalarAdapters, value)
    }
}