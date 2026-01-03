package com.dallaslabs.firebase.analytics

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for Firebase Analytics SDK.
 * Use @ComponentScan to automatically discover and register components.
 */
@Module
@ComponentScan("com.dallaslabs.firebase.analytics")
public class FirebaseAnalyticsModule
