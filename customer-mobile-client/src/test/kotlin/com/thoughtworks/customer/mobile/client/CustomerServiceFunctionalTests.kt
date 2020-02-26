package com.thoughtworks.customer.mobile.client

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

@Testcontainers
class CustomerServiceFunctionalTests {
    private val pactBrokerAddress = "172.17.0.1"

    @Container
    private val stubServer: GenericContainer<Nothing> = GenericContainer<Nothing>("pactfoundation/pact-stub-server")
            .apply {
                withExposedPorts(8080)
                withCommand("-u", "http://${pactBrokerAddress}/pacts/provider/CustomerService/consumer/AndroidClient/latest",
                        "-p", "8080")
            }

    @Test
    fun shouldRunFunctionalTest() {
        val serverAddress = "http://${stubServer.containerIpAddress}:${stubServer.getMappedPort(8080)}"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
                .uri(URI.create("$serverAddress/customers/1234"))
                .header("Accept", "application/json")
                .build()
        val response = client.send(request, BodyHandlers.ofString())

        assertEquals(200, response.statusCode())

    }
}
