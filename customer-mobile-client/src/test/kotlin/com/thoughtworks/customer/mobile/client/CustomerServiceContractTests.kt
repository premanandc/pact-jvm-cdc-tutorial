package com.thoughtworks.customer.mobile.client

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

@ExtendWith(PactConsumerTestExt::class)
class CustomerServiceContractTests {

    @Pact(provider = "CustomerService", consumer = "AndroidClient")
    fun getDetailsById(builder: PactDslWithProvider): RequestResponsePact {
        return builder.given("A customer with an existing ID")
                .uponReceiving("a request for customer details")
                .path("/customers/1234")
                .headers(mapOf("Accept" to "application/json"))
                .willRespondWith()
                .headers(mapOf("Content-Type" to "application/json"))
                .body(
                        PactDslJsonBody()
                                .stringType("firstName", "Test")
                                .stringType("lastName", "First")
                )
                .status(200)
                .toPact()
    }

    @PactTestFor(pactMethod = "getDetailsById")
    @Test
    fun testForGetDetailsById(mockServer: MockServer) {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
                .uri(URI.create(mockServer.getUrl() + "/customers/1234"))
                .header("Accept", "application/json")
                .build()
        val response = client.send(request, BodyHandlers.ofString())

        assertEquals(200, response.statusCode())

        val headers = response.headers()
        assertTrue(headers.allValues("Content-Type").contains("application/json"))

        val customer = response.bodyAsCustomer()
        assertEquals("Test", customer.firstName)
        assertEquals("First", customer.lastName)

    }
}

private fun HttpResponse<String>.bodyAsCustomer(): Customer = Gson()
        .fromJson(body(), Customer::class.java)
