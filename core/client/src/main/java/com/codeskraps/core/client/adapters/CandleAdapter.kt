package com.codeskraps.core.client.adapters

import com.codeskraps.core.client.model.CandleDto
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

class CandleAdapter {

    @FromJson
    fun fromJson(reader: JsonReader): List<CandleDto> {
        val result = mutableListOf<CandleDto>()

        reader.beginArray()

        while (reader.hasNext()) {

            reader.beginArray()

            while (reader.hasNext()) {

                result.add(
                    CandleDto(
                        openTime = (reader.readJsonValue() as Double).toLong(),
                        open = (reader.readJsonValue() as String).toDouble(),
                        high = (reader.readJsonValue() as String).toDouble(),
                        low = (reader.readJsonValue() as String).toDouble(),
                        close = (reader.readJsonValue() as String).toDouble(),
                        volume = (reader.readJsonValue() as String).toDouble(),
                        closeTime = (reader.readJsonValue() as Double).toLong(),
                    )
                )
                (0 until 5).forEach { _ -> reader.readJsonValue() }
            }
            reader.endArray()
        }
        reader.endArray()

        return result
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: List<CandleDto>) {
        writer.value(value.toString())
    }
}