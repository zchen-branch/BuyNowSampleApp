package com.example.buynow.presentation.activity

import android.app.Application
import io.branch.referral.Branch


class CustomApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()

        // Branch logging for debugging
        Branch.enableLogging()

        // Branch object initialization
        Branch.getAutoInstance(this)
    }
}