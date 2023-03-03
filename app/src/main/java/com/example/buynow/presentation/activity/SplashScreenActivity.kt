package com.example.buynow.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.buynow.R
import io.branch.referral.Branch
import io.branch.referral.BranchError
import org.json.JSONObject
import com.example.buynow.utils.FirebaseUtils as FirebaseUtils1

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        Handler().postDelayed({

            checkUser()

        }, 1000)

    }

    private fun checkUser() {
        /*
        if(FirebaseUtils.firebaseUser?.isEmailVerified == true){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
*/
        if(FirebaseUtils1.firebaseUser != null){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        if(FirebaseUtils1.firebaseUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    override fun onStart() {
        super.onStart()
        Branch.sessionBuilder(this).withCallback { branchUniversalObject, linkProperties, error ->
            if (error != null) {
                Log.e("BranchSDK_Tester", "branch init failed. Caused by -" + error.message)
            } else {
                Log.e("BranchSDK_Tester", "branch init complete!")
                if (branchUniversalObject != null) {
                    Log.e("BranchSDK_Tester", "title " + branchUniversalObject.title)
                    Log.e("BranchSDK_Tester", "CanonicalIdentifier " + branchUniversalObject.canonicalIdentifier)
                    Log.e("BranchSDK_Tester", "metadata " + branchUniversalObject.contentMetadata.convertToJson())
                }
                if (linkProperties != null) {
                    Log.e("BranchSDK_Tester", "Channel " + linkProperties.channel)
                    Log.e("BranchSDK_Tester", "control params " + linkProperties.controlParams)
                }
            }
        }.withData(this.intent.data).init()

        //get the latest data
        val sessionParams = Branch.getInstance().latestReferringParams
        onInitFinished(sessionParams, null)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Branch.sessionBuilder(this).withCallback { referringParams, error ->
            if (error != null) {
                Log.e("BranchSDK_Tester", error.message)
            } else if (referringParams != null) {
                Log.e("BranchSDK_Tester", referringParams.toString())
            }
        }.reInit()
    }

    fun onInitFinished(referringParams: JSONObject?, error: BranchError?) {
        if (error == null) {
            // Extract the data you need from the location
            val deeplinkPath = referringParams?.optString("location")
            Log.d("deeplink", "deeplinkPath: $deeplinkPath")
            navigateToActivity(deeplinkPath)

        }
            }

    private fun navigateToActivity(deeplinkPath: String?) {
        Log.d("deeplink", "Navigating to deeplinkPath: location")
        val intent = when (deeplinkPath) {
            "setting" ->  Intent (this, PaymentMethodActivity::class.java)
            "settings"->  Intent (this, SettingsActivity::class.java)
            else ->  {Intent(this, HomeActivity::class.java)}
        }
        startActivity(intent)
        finish()
    }
}

