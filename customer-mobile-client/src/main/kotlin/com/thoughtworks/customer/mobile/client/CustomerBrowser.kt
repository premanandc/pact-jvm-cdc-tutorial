package com.thoughtworks.customer.mobile.client

import com.google.gson.Gson
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.util.concurrent.CompletableFuture

interface CustomerBrowser {
    fun findById(id: Long): CompletableFuture<Customer?>
}

private const val resource = "customers"
const val APPLICATION_JSON = "application/json"

fun HttpResponse<String>.bodyAsCustomer(): Customer = Gson()
        .fromJson(body(), Customer::class.java)

class DefaultCustomerBrowser(private val serverAddress: String, private val port: Int) : CustomerBrowser {
    private val client: HttpClient = HttpClient.newBuilder().build()

    override fun findById(id: Long): CompletableFuture<Customer?> {
        val request = HttpRequest.newBuilder()
                .uri(URI.create("http://$serverAddress:$port/$resource/$id"))
                .header("Accept", APPLICATION_JSON)
                .build()
        return client.sendAsync(request, BodyHandlers.ofString())
                .thenApply { if (it.isSuccessful()) it.bodyAsCustomer() else null }
    }
}

private fun <T> HttpResponse<T>.isSuccessful(): Boolean {
    return statusCode() in 200..299
}
