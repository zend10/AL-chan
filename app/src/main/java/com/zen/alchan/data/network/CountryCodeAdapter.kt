package com.zen.alchan.data.network

import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue

class CountryCodeAdapter : CustomTypeAdapter<String> {

    override fun encode(value: String): CustomTypeValue<*> {
        return CustomTypeValue.fromRawValue(value)
    }

    override fun decode(value: CustomTypeValue<*>): String {
        return value.value.toString()
    }
}