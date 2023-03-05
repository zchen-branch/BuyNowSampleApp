package com.example.buynow.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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

        if(FirebaseUtils1.firebaseUser?.isEmailVerified == true){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (FirebaseUtils1.firebaseUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (FirebaseUtils1.firebaseUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        //set Adobe ID
        Branch.getInstance().setRequestMetadata("\$marketing_cloud_visitor_id", "sundy")
        //initialize the Branch SDK
        Branch.sessionBuilder(this).withCallback { branchUniversalObject, linkProperties, error ->
            if (error != null) {
                Log.e("BranchSDK_Tester", "branch init failed. Caused by -" + error.message)
            } else {
                Log.e("BranchSDK_Tester", "branch init complete!")
                if (branchUniversalObject != null) {
                    Log.e("BranchSDK_Tester", "title " + branchUniversalObject.title)
                    Log.e(
                        "BranchSDK_Tester",
                        "CanonicalIdentifier " + branchUniversalObject.canonicalIdentifier
                    )
                    Log.e(
                        "BranchSDK_Tester",
                        "metadata " + branchUniversalObject.contentMetadata.convertToJson()
                    )
                }
                if (linkProperties != null) {
                    Log.e("BranchSDK_Tester", "Channel " + linkProperties.channel)
                    Log.e("BranchSDK_Tester", "control params " + linkProperties.controlParams)
                }
                val webOnlyKey = "\$web_only"


                val sessionParam = Branch.getInstance().latestReferringParams

                    //print out whether the link itself contains the routing indicators
                    Log.e("BranchRouting", sessionParam.toString())
                    Log.d("BranchRouting", "sessionParam.has(\"\\\$canonical_identifier\"): ${sessionParam.has("\$canonical_identifier")}")
                    Log.d("BranchRouting", "sessionParam.getString(\"setting\"): ${sessionParam.getString("\$canonical_identifier") == "setting"}")
                    Log.d("BranchRouting", "sessionParam.has(\"\\\$web_only\"): ${sessionParam.getString("\$web_only") == "true"}")

                // code to route web-only links to the web
                if (sessionParam.has("\$web_only") && sessionParam.getString("\$web_only").toBoolean()) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")))
                }
                // code to route within the app setting page
                if (sessionParam.has("\$canonical_identifier") && sessionParam.getString("\$canonical_identifier") == "setting") {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                // code to route within the app payment page
                if (sessionParam.has("\$canonical_identifier") && sessionParam.getString("\$canonical_identifier") == "payment") {
                    val intent = Intent(this, PaymentMethodActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                //if (sessionParam.has("\$product_id") && sessionParam.getString("\$product_id") == "1") {
                  //  val pPid = sessionParam.getString("\$product_id")
                   // val intent = Intent(this, ProductDetailsActivity::class.java)
                   // intent.putExtra(pPid, pPid)
                 //   startActivity(intent)
                  //  finish()
               // }


            }
        }.withData(this.intent.data).init()



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
}



