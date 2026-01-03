plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    id("maven-publish")
}

group = "com.dallaslabs.sdk"
version = "1.0.0"

kotlin {
    explicitApi()
    
    androidTarget {
        publishLibraryVariants("release", "debug")
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "FirebaseAnalytics"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.gitlive.firebase.analytics)
                api(libs.koin.core)
                implementation(libs.koin.annotations)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.android)
            }
        }
        
        val iosMain by creating {
            dependsOn(commonMain)
        }
        
        val iosX64Main by getting {
            dependsOn(iosMain)
        }
        
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
    }
}

android {
    namespace = "com.dallaslabs.sdk.analytics"
    compileSdk = 35
    
    defaultConfig {
        minSdk = 24
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

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                name.set("Firebase Analytics SDK")
                description.set("Kotlin Multiplatform Firebase Analytics SDK")
                url.set("https://github.com/erikg84/firebase-analytics")
                
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
