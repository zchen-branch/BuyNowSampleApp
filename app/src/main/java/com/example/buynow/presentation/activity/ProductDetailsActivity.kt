package com.example.buynow.presentation.activity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buynow.presentation.adapter.ProductAdapter
import com.example.buynow.R
import com.example.buynow.data.model.Product
import com.example.buynow.utils.DefaultCard.GetDefCard
import com.example.buynow.utils.Extensions.cardXXGen
import com.example.buynow.utils.Extensions.toast
import com.example.buynow.data.local.room.CartViewModel
import com.example.buynow.data.local.room.ProductEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.util.BRANCH_STANDARD_EVENT
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import io.branch.referral.Branch.BranchLinkShareListener
import io.branch.referral.QRCode.BranchQRCode
import io.branch.referral.QRCode.BranchQRCode.BranchQRCodeImageHandler
import io.branch.referral.SharingHelper
import io.branch.referral.util.ShareSheetStyle
import java.lang.Exception



class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var product: Product
    var productIndex: Int = -1
    lateinit var ProductFrom: String
    private lateinit var cartViewModel: CartViewModel
    private val TAG = "TAG"
    lateinit var productImage_ProductDetailsPage: ImageView
    lateinit var backIv_ProfileFrag: ImageView
    lateinit var productName_ProductDetailsPage: TextView
    lateinit var productPrice_ProductDetailsPage: TextView
    lateinit var productBrand_ProductDetailsPage: TextView
    lateinit var productDes_ProductDetailsPage: TextView
    lateinit var RatingProductDetails: TextView
    lateinit var productRating_singleProduct: RatingBar
    lateinit var ShareLink: Button
    lateinit var QRCode: Button
    lateinit var pushnotification: Button
    lateinit var sharesheet: Button



    lateinit var RecomRecView_ProductDetailsPage: RecyclerView
    lateinit var newProductAdapter: ProductAdapter
    lateinit var newProduct: ArrayList<Product>

    lateinit var pName: String
    var qua: Int = 1
    var pPrice: Int = 0
    lateinit var pPid: String
    lateinit var pImage: String
    lateinit var pDes:String

    lateinit var cardNumber: String


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        productIndex = intent.getIntExtra("ProductIndex", -1)
        ProductFrom = intent.getStringExtra("ProductFrom").toString()

        productImage_ProductDetailsPage = findViewById(R.id.productImage_ProductDetailsPage)
        productName_ProductDetailsPage = findViewById(R.id.productName_ProductDetailsPage)
        productPrice_ProductDetailsPage = findViewById(R.id.productPrice_ProductDetailsPage)
        productBrand_ProductDetailsPage = findViewById(R.id.productBrand_ProductDetailsPage)
        productDes_ProductDetailsPage = findViewById(R.id.productDes_ProductDetailsPage)
        productRating_singleProduct = findViewById(R.id.productRating_singleProduct)
        RatingProductDetails = findViewById(R.id.RatingProductDetails)
        RecomRecView_ProductDetailsPage = findViewById(R.id.RecomRecView_ProductDetailsPage)
        backIv_ProfileFrag = findViewById(R.id.backIv_ProfileFrag)
        val addToCart_ProductDetailsPage: Button = findViewById(R.id.addToCart_ProductDetailsPage)
        val shippingAddress_productDetailsPage:LinearLayout = findViewById(R.id.shippingAddress_productDetailsPage)
        val cardNumberProduct_Details:TextView = findViewById(R.id.cardNumberProduct_Details)
        ShareLink=findViewById(R.id.share_button)
        ShareLink.setOnClickListener {
            val buo = BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle(pName.toString())
                .setContentDescription(pDes.toString())
                .setContentImageUrl(pImage.toString())
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(ContentMetadata().addCustomMetadata("Branch", "value1"))

            val lp = LinkProperties()
                .setChannel("SMS")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")
                .addControlParameter("custom", "data")

            buo.generateShortUrl(this,lp, Branch.BranchLinkCreateListener { url, error ->
                if (url != null) {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("branch_link", url)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this, url + "Link copied to clipboard", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i("branch SDK", "Error generating link: $error")
                }
            })
        }
        QRCode=findViewById(R.id.qr_code)
        QRCode.setOnClickListener {
            val qrCode = BranchQRCode()
                .setCodeColor("#a4c639")
                .setBackgroundColor(Color.WHITE)
                .setMargin(1)
                .setWidth(512)
                .setImageFormat(BranchQRCode.BranchImageFormat.JPEG)
                .setCenterLogo("https://cdn.branch.io/branch-assets/1598575682753-og_image.png")

            val buo = BranchUniversalObject()
               .setCanonicalIdentifier("content/12345")
               .setTitle("My Content Title")
               .setContentDescription("My Content Description")
               .setContentImageUrl("https://lorempixel.com/400/400")

            val lp= LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                 .setStage("new user")

            qrCode.getQRCodeAsImage(this, buo,lp,object : BranchQRCodeImageHandler<Any?>{
                override fun onSuccess(qrCodeImage: Bitmap) {}
                override fun onFailure(e: Exception?) {
                    Log.d("Failed", e.toString())
                }
            })

        }
        sharesheet=findViewById(R.id.share_sheet)
        sharesheet.setOnClickListener {
            val buo = BranchUniversalObject()
                .setCanonicalIdentifier("content12345")
                .setTitle("My Title")

            val lp = LinkProperties()
                .setChannel("FB")
                .setFeature("sharing")
            val ss = ShareSheetStyle(this, "Check out this new product!", "Check out this new product!")
                .setCopyUrlStyle(resources.getDrawable(android.R.drawable.ic_menu_send), "Copy", "Add to clipboard")
                .setMoreOptionStyle(resources.getDrawable (android.R.drawable.ic_menu_search), "ShowMore")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.SNAPCHAT)

            buo.showShareSheet(this,lp,ss, object: BranchLinkShareListener{
                override fun onShareLinkDialogLaunched() {}
                override fun onShareLinkDialogDismissed() {}
                override fun onLinkShareResponse(sharedLink: String?, sharedChannel: String?, error: BranchError?) {}
                override fun onChannelSelected(channelName: String) {}
            })
        }

            //Create a notification channel
            val CHANNEL_Id = "com.example.buynow.notification"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Branch"
                val description = "Branch Push"
                val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_Id, name, importance)
                channel.description = description

            //register channel with the system
                val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
            //create a notification builder and set its properties. This is currently static
            val NotificationBuilder = NotificationCompat.Builder(this,CHANNEL_Id )
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setContentTitle("New Product Alert! Get into the app right now!")
                .setContentText("If you open the app today everything is free")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Use the notification manager to display the notification

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            pushnotification = findViewById(R.id.push_notification)
            pushnotification.setOnClickListener {
                val resultIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sundychen.app.link/1LfaRHF34nb"))
                resultIntent.putExtra("Branch_force_new_session", true)
                val resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                NotificationBuilder.setContentIntent(resultPendingIntent)
                notificationManager.notify(0,NotificationBuilder.build())
                //val resultIntent = Intent(this,SettingsActivity::class.java)
                   // resultIntent.putExtra("Branch","https://sundychen.app.link/1LfaRHF34nb")
                   // resultIntent.putExtra("Branch_force_new_session", true)
                //val resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            }



        cardNumber = GetDefCard()

        if(cardNumber == "" || cardNumber == null){
            cardNumberProduct_Details.text = "You Have No Cards"
        }
        else{
            cardNumberProduct_Details.text = cardXXGen(cardNumber)
        }


        shippingAddress_productDetailsPage.setOnClickListener {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }


        newProduct = arrayListOf()
        setProductData()
        setRecData()

        RecomRecView_ProductDetailsPage.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        RecomRecView_ProductDetailsPage.setHasFixedSize(true)
        newProductAdapter = ProductAdapter(newProduct, this)
        RecomRecView_ProductDetailsPage.adapter = newProductAdapter

        backIv_ProfileFrag.setOnClickListener {
            onBackPressed()
        }

        addToCart_ProductDetailsPage.setOnClickListener {

            val bottomSheetDialod = BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme
            )

            val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
                R.layout.fragment_add_to_bag,
                findViewById<ConstraintLayout>(R.id.bottomSheet)
            )

            bottomSheetView.findViewById<View>(R.id.addToCart_BottomSheet).setOnClickListener {

                pPrice *= bottomSheetView.findViewById<EditText>(R.id.quantityEtBottom).text.toString()
                    .toInt()
                addProductToBag()
                bottomSheetDialod.dismiss()
            }

            bottomSheetView.findViewById<LinearLayout>(R.id.minusLayout).setOnClickListener {
                if(bottomSheetView.findViewById<EditText>(R.id.quantityEtBottom).text.toString()
                        .toInt() > 1){
                    qua--
                    bottomSheetView.findViewById<EditText>(R.id.quantityEtBottom).setText(qua.toString())
                }
            }

            bottomSheetView.findViewById<LinearLayout>(R.id.plusLayout).setOnClickListener {
                if(bottomSheetView.findViewById<EditText>(R.id.quantityEtBottom).text.toString()
                        .toInt() < 10){
                    qua++
                    bottomSheetView.findViewById<EditText>(R.id.quantityEtBottom).setText(qua.toString())
                }
            }

            bottomSheetDialod.setContentView(bottomSheetView)
            bottomSheetDialod.show()
        }

    }

    private fun addProductToBag() {
        BranchEvent(BRANCH_STANDARD_EVENT.ADD_TO_CART).logEvent(applicationContext)
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)

        cartViewModel.insert(ProductEntity(pName, qua, pPrice, pPid, pImage))
        toast("Add to Bag Successfully")
    }

    fun getJsonData(context: Context, fileName: String): String? {


        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }

        return jsonString
    }

    private fun setProductData() {

        var fileJson: String = ""

        if (ProductFrom.equals("Cover")) {
            fileJson = "CoverProducts.json"
        }
        if (ProductFrom.equals("New")) {
            fileJson = "NewProducts.json"
        }


        val jsonFileString = this.let {

            getJsonData(it, fileJson)
        }

        val gson = Gson()


        val listCoverType = object : TypeToken<List<Product>>() {}.type

        var coverD: List<Product> = gson.fromJson(jsonFileString, listCoverType)

        Glide.with(applicationContext)
            .load(coverD[productIndex].productImage)
            .into(productImage_ProductDetailsPage)

        productName_ProductDetailsPage.text = coverD[productIndex].productName
        productPrice_ProductDetailsPage.text = "$" + coverD[productIndex].productPrice
        productBrand_ProductDetailsPage.text = coverD[productIndex].productBrand
        productDes_ProductDetailsPage.text = coverD[productIndex].productDes
        productRating_singleProduct.rating = coverD[productIndex].productRating
        RatingProductDetails.text = coverD[productIndex].productRating.toString() + " Rating on this Product."

        pName = coverD[productIndex].productName
        pPrice = coverD[productIndex].productPrice.toInt()
        pPid = coverD[productIndex].productId
        pImage = coverD[productIndex].productImage
        pDes =coverD[productIndex].productDes

    }

    private fun setRecData() {


        var fileJson: String = ""

        if (ProductFrom.equals("Cover")) {
            fileJson = "NewProducts.json"
        }
        if (ProductFrom.equals("New")) {
            fileJson = "CoverProducts.json"
        }


        val jsonFileString = this.let {

            getJsonData(it, fileJson)
        }
        val gson = Gson()

        val listCoverType = object : TypeToken<List<Product>>() {}.type

        var coverD: List<Product> = gson.fromJson(jsonFileString, listCoverType)

        coverD.forEachIndexed { idx, person ->

            if (idx < 9) {
                newProduct.add(person)
            }


        }

    }

}


