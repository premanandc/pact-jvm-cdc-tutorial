package com.thoughtworks.customer.mobile.client

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class CustomerServiceFunctionalTests {
    private val logger = LoggerFactory.getLogger(CustomerServiceFunctionalTests::class.java)
    private val pactBrokerAddress = "172.17.0.1"

    @Container
    private val stubServer: GenericContainer<Nothing> = GenericContainer<Nothing>("pactfoundation/pact-stub-server")
            .apply {
                withExposedPorts(8080)
                withCommand("-u", "http://${pactBrokerAddress}/pacts/provider/CustomerService/consumer/AndroidClient/latest",
                        "-p", "8080",
                        "-s", "an existing customer with a valid id")
                withLogConsumer(Slf4jLogConsumer(logger))
            }

    @Test
    fun shouldRunFunctionalTest() {
        val serverAddress = stubServer.containerIpAddress
        val port = stubServer.getMappedPort(8080)
        val browser: CustomerBrowser = DefaultCustomerBrowser(serverAddress, port)

        val customer = browser.findById(1234L).get()
        assertNotNull(customer!!)
        assertEquals("Test", customer.firstName)
        assertEquals("First", customer.lastName)
    }
}
