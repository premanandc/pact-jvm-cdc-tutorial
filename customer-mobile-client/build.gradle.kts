plugins {
    kotlin("jvm") version "1.3.61"
    id("au.com.dius.pact") version "4.0.6"
}

group = "com.thoughtworks.customer"
version = "0.0.1-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
    testImplementation("au.com.dius:pact-jvm-consumer-junit5:4.0.6")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

pact {
    publish {
        pactDirectory = "${project.buildDir}/pacts"
        pactBrokerUrl = "http://localhost"
    }
}
