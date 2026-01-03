import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    id("maven-publish")
}

group = "com.dallaslabs.sdk"
version = findProperty("version")?.toString()?.takeIf { it != "unspecified" } ?: "1.0.0"

kotlin {
    explicitApi()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FirebaseAnalyticsSDK"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            api(libs.gitlive.firebase.analytics)
            api(libs.koin.core)
            api(libs.koin.annotations)
        }
    }
}

android {
    namespace = "com.dallaslabs.firebase.analytics"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosX64", libs.koin.ksp.compiler)
    add("kspIosArm64", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_DEFAULT_MODULE", "false")
}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = "com.dallaslabs.sdk"
            artifactId = when (name) {
                "kotlinMultiplatform" -> "firebase-analytics"
                else -> "firebase-analytics-$name"
            }

            pom {
                name.set("Firebase Analytics SDK")
                description.set("Kotlin Multiplatform Firebase Analytics SDK for Android and iOS")
                url.set("https://github.com/erikg84/firebase-analytics")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("erikg84")
                        name.set("Erik G")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/erikg84/firebase-analytics.git")
                    developerConnection.set("scm:git:ssh://github.com/erikg84/firebase-analytics.git")
                    url.set("https://github.com/erikg84/firebase-analytics")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/erikg84/firebase-analytics")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: findProperty("gpr.user")?.toString()
                password = System.getenv("GITHUB_TOKEN") ?: findProperty("gpr.token")?.toString()
            }
        }
    }
}
