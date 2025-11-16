package com.example.deviseapp.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateService {
    // Using open.er-api.com (free & no key required)
    @GET("v6/latest/{base}")
    suspend fun latest(
        @Path("base") base: String
    ): LatestResponse
}

@JsonClass(generateAdapter = true)
data class LatestResponse(
    @Json(name = "result") val result: String,
    @Json(name = "documentation") val documentation: String? = null,
    @Json(name = "terms_of_use") val termsOfUse: String? = null,
    @Json(name = "time_last_update_unix") val timeLastUpdateUnix: Long? = null,
    @Json(name = "time_next_update_unix") val timeNextUpdateUnix: Long? = null,
    @Json(name = "base_code") val baseCode: String? = null,
    @Json(name = "rates") val rates: Map<String, Double>? = null,
    @Json(name = "error-type") val errorType: String? = null
)


