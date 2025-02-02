package ru.itmo.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpRequest.Builder
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.HttpResponse

class JvmHttpClient : HttpClient {
    private val httpClient = newHttpClient()

    private fun buildRequestHeaders(
        request: HttpRequest,
        httpRequestBuilder: Builder,
    ): Builder {
        val values = request.headers.value

        if (values.isEmpty()) {
            return httpRequestBuilder
        }

        val flatHeaders = values
            .flatMap { (key, value) ->
                listOf(key, value)
            }
            .toTypedArray()

        return httpRequestBuilder.headers(*flatHeaders)
    }

    private fun buildRequestMethod(
        method: HttpMethod,
        request: HttpRequest,
        httpRequestBuilder: Builder,
    ): Builder = when (method) {
        HttpMethod.GET -> {
            httpRequestBuilder.GET()
        }

        HttpMethod.POST -> {
            httpRequestBuilder.POST(BodyPublishers.ofByteArray(request.body))
        }

        HttpMethod.PUT -> {
            httpRequestBuilder.PUT(BodyPublishers.ofByteArray(request.body))
        }

        HttpMethod.DELETE -> {
            httpRequestBuilder.DELETE()
        }
    }

    private fun wrapHttpStatus(httpResponse: HttpResponse<String>): HttpStatus =
        HttpStatus(httpResponse.statusCode())

    private fun wrapHttpHeaders(httpResponse: HttpResponse<String>): HttpHeaders {
        val values = httpResponse
            .headers()
            .map()
            .mapValues { it.value.single() }

        return HttpHeaders(values)
    }

    private fun wrapHttpBody(httpResponse: HttpResponse<String>) =
        httpResponse.body().toByteArray()

    override suspend fun request(
        method: HttpMethod,
        request: HttpRequest,
    ): ru.itmo.client.HttpResponse {
        var httpRequestBuilder = newBuilder()
            .uri(URI.create(request.url))

        httpRequestBuilder = buildRequestHeaders(request, httpRequestBuilder)
        httpRequestBuilder = buildRequestMethod(method, request, httpRequestBuilder)

        val httpResponse = withContext(Dispatchers.IO) {
            httpClient.send(httpRequestBuilder.build(), BodyHandlers.ofString())
        }

        return HttpResponse(
            status = wrapHttpStatus(httpResponse),
            headers = wrapHttpHeaders(httpResponse),
            body = wrapHttpBody(httpResponse),
        )
    }

    override fun close() {}
}
