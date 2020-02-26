package com.thoughtworks.customer.mobile.client

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import com.google.common.net.HttpHeaders.ACCEPT
import com.google.common.net.HttpHeaders.CONTENT_TYPE
import org.apache.http.HttpStatus.SC_NOT_FOUND
import org.apache.http.HttpStatus.SC_OK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

@ExtendWith(PactConsumerTestExt::class)
class CustomerServiceContractTests {

    @Pact(provider = "CustomerService", consumer = "AndroidClient")
    fun getDetailsForExistingCustomerId(builder: PactDslWithProvider): RequestResponsePact {
        return builder.given("an existing customer with a valid id")
                .uponReceiving("a request for an existing customer id")
                .matchPath("/customers/\\d+", "/customers/1234")
                .headers(mapOf(ACCEPT to APPLICATION_JSON))
                .willRespondWith()
                .headers(mapOf(CONTENT_TYPE to APPLICATION_JSON))
                .body(
                        PactDslJsonBody()
                                .stringMatcher("firstName", "[A-Z][\\w\\s]+", "Test")
                                .stringMatcher("lastName", "[A-Z][\\w\\s]+", "First")
                )
                .status(SC_OK)
                .toPact()
    }

    @Pact(provider = "CustomerService", consumer = "AndroidClient")
    fun getDetailsForNonExistentCustomerId(builder: PactDslWithProvider): RequestResponsePact {
        return builder.given("a non-existent customer with an invalid id")
                .uponReceiving("a request for a non-existent customer id")
                .matchPath("/customers/\\d+", "/customers/112233")
                .headers(mapOf(ACCEPT to APPLICATION_JSON))
                .willRespondWith()
                .status(SC_NOT_FOUND)
                .toPact()
    }

    @PactTestFor(pactMethod = "getDetailsForExistingCustomerId")
    @Test
    fun testForGetDetailsByIdForExistingCustomer(mockServer: MockServer) {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
                .uri(URI.create(mockServer.getUrl() + "/customers/123412121"))
                .header(ACCEPT, APPLICATION_JSON)
                .build()
        val response = client.send(request, BodyHandlers.ofString())

        assertEquals(SC_OK, response.statusCode())

        val headers = response.headers()
        assertTrue(headers.allValues(CONTENT_TYPE).contains(APPLICATION_JSON))

        val customer = response.bodyAsCustomer()
        assertEquals("Test", customer.firstName)
        assertEquals("First", customer.lastName)
    }

    @PactTestFor(pactMethod = "getDetailsForNonExistentCustomerId")
    @Test
    fun testForGetDetailsByIdForNonExistentCustomer(mockServer: MockServer) {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
                .uri(URI.create(mockServer.getUrl() + "/customers/1234"))
                .header(ACCEPT, APPLICATION_JSON)
                .build()
        val response = client.send(request, BodyHandlers.discarding())

        assertEquals(SC_NOT_FOUND, response.statusCode())
    }
}

