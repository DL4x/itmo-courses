package ru.itmo.client

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.js.Promise

class FetchClient : HttpClient {
    private fun wrapFetch(
        method: HttpMethod,
        request: HttpRequest,
    ): Promise<Response> {
        val headers = Headers()
        request.headers.value.forEach {
            headers.append(it.key, it.value)
        }

        val httpRequest = RequestInit(
            method = method.name,
            headers = headers,
            body = request.body,
        )

        val httpResponse = when (platform) {
            Platform.Browser -> {
                window.fetch(request.url, httpRequest)
            }

            Platform.Node -> {
                val nodeHttpRequest = httpRequest.asNodeOptions()
                nodeFetch(request.url, nodeHttpRequest)
            }
        }

        return httpResponse as Promise<Response>
    }

    private fun wrapHttpStatus(httpResponse: Response) =
        HttpStatus(httpResponse.status.toInt())

    private fun wrapHttpHeaders(httpResponse: Response): HttpHeaders {
        val map = mutableMapOf<String, String>()
        httpResponse
            .headers
            .asDynamic()
            .forEach { value, key ->
                map.put(key as String, value as String)
            }
        return HttpHeaders(map)
    }

    private suspend fun wrapHttpBody(httpResponse: Response) =
        httpResponse.text().await().encodeToByteArray()

    override suspend fun request(
        method: HttpMethod,
        request: HttpRequest,
    ): HttpResponse {
        val httpResponse = wrapFetch(method, request).await()

        return HttpResponse(
            status = wrapHttpStatus(httpResponse),
            headers = wrapHttpHeaders(httpResponse),
            body = wrapHttpBody(httpResponse),
        )
    }

    override fun close() {}
}

private enum class Platform { Node, Browser }

private val platform: Platform
    get() {
        val hasNodeApi = js(
            """
            (typeof process !== 'undefined' 
                && process.versions != null 
                && process.versions.node != null) ||
            (typeof window !== 'undefined' 
                && typeof window.process !== 'undefined' 
                && window.process.versions != null 
                && window.process.versions.node != null)
            """
        ) as Boolean
        return if (hasNodeApi) Platform.Node else Platform.Browser
    }

private val nodeFetch: dynamic
    get() = js("eval('require')('node-fetch')")

private fun RequestInit.asNodeOptions(): dynamic =
    js("Object").assign(js("Object").create(null), this)
