package com.zen.alchan.data.network

import com.apollographql.apollo.response.CustomTypeAdapter
import com.apollographql.apollo.response.CustomTypeValue

class JsonAdapter : CustomTypeAdapter<Any> {

    override fun encode(value: Any): CustomTypeValue<*> {
        return CustomTypeValue.fromRawValue(value)
    }

    override fun decode(value: CustomTypeValue<*>): Any {
        return value
    }
}