package app.revanced.manager.util

import com.vk.knet.core.Knet
import com.vk.knet.core.http.HttpMethod
import com.vk.knet.core.http.HttpPayload
import com.vk.knet.core.http.HttpRequest
import com.vk.knet.core.http.HttpResponse
import com.vk.knet.core.http.body.request.HttpRequestBody
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun Knet.get(
    url: String,
    headers: Map<String, List<String>> = emptyMap(),
    body: HttpRequestBody? = null,
    payload: Map<HttpPayload, Any>? = null
): HttpResponse {
    return execute(HttpRequest(HttpMethod.GET, url, headers, body, payload))
}

inline fun <reified T> HttpResponse.body(json: Json = Json.Default): T {
    return json.decodeFromString(body!!.toString())
}