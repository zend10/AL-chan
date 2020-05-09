package com.zen.alchan.data.network

import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue

class JsonAdapter : CustomTypeAdapter<Any> {

    override fun encode(value: Any): CustomTypeValue<*> {
        return CustomTypeValue.fromRawValue(value)
    }

    override fun decode(value: CustomTypeValue<*>): Any {
        return value
    }
}