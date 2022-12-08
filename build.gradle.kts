val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val prometeus_version: String by project
val koin_version: String by project

plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("io.ktor.plugin") version "2.1.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20"
    id("com.google.devtools.ksp") version "1.7.20-1.0.6"
}

group = "com.competeSystem"
version = "0.0.3"
application {
    mainClass.set("com.compete.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
//    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

val exposedVersion: String by project
dependencies {
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeus_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.google.code.gson:gson:2.10")
    implementation("io.ktor:ktor-server-core-jvm:2.2.1")
    implementation("io.ktor:ktor-server-cors-jvm:2.2.1")
    implementation("io.ktor:ktor-server-host-common-jvm:2.2.1")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.2.1")
    implementation("io.ktor:ktor-server-compression-jvm:2.2.1")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.2.1")
    implementation("io.ktor:ktor-server-metrics-jvm:2.2.1")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:2.2.1")
    implementation("io.ktor:ktor-server-mustache-jvm:2.2.1")
    implementation("io.ktor:ktor-server-content-negotiation:2.2.1")
    implementation("io.ktor:ktor-server-websockets-jvm:2.2.1")
    implementation("io.ktor:ktor-server-netty-jvm:2.2.1")
    implementation("io.ktor:ktor-server-auth-jvm:2.2.1")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.2.1")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.2.1")
    implementation("io.ktor:ktor-serialization-gson-jvm:2.2.1")
    implementation("io.ktor:ktor-client-core-jvm:2.2.1")
    implementation("io.ktor:ktor-client-okhttp-jvm:2.2.1")
    implementation("io.ktor:ktor-client-content-negotiation:2.2.1")
    implementation("io.ktor:ktor-serialization-gson:2.2.1")
    implementation("io.ktor:ktor-server-partial-content-jvm:2.2.1")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // Koin Core features
    implementation("io.insert-koin:koin-core:$koin_version")
    // Koin for Ktor
    implementation("io.insert-koin:koin-ktor:$koin_version")
    // SLF4J Logger
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    //

//    implementation(fileTree("libs"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.40.0.0")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.2.1")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.2.1")
}