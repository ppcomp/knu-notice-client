package com.ppcomp.knu.activity

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.MakerData
import kotlinx.android.synthetic.main.activity_maker.*
import org.json.JSONArray
import java.util.*

/**
 * A simple activity showing the use of [AdView] ads in
 * a [RecyclerView] widget.
 *
 * The [RecyclerView] widget is a more advanced and flexible version of
 * ListView. This widget helps simplify the display and handling of large data sets
 * by allowing the layout manager to determine when to reuse (recycle) item views that
 * are no longer visible to the user. Recycling views improves performance by avoiding
 * the creation of unnecessary views or performing expensive findViewByID() lookups.
 */
class MakerActivity : AppCompatActivity() {

    // A banner ad is placed in every 8th position in the RecyclerView.
    var ITEMS_PER_AD: Int = 8

    private val UNIT_ID: String = "ca-app-pub-3940256099942544/4177191030"

    // The RecyclerView that holds and displays banner ads and menu items.
    private lateinit var makerRecyclerView: RecyclerView

    // List of banner ads and MenuItems that populate the RecyclerView.
    private var recyclerViewItems: MutableList<Any> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maker)
        makerRecyclerView = findViewById(R.id.maker)

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView.
        makerRecyclerView.setHasFixedSize(true)

        // Specify a linear layout manager.
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        makerRecyclerView.setLayoutManager(layoutManager)

        // Update the RecyclerView item's list with menu items and banner ads.
        getMakers()
        addBannerAds()
        loadBannerAds()

        // Specify an adapter.
        val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder> = MakerAdapter(
            this,
            recyclerViewItems
        )
        makerRecyclerView.setAdapter(adapter)
    }

    override fun onResume() {
        for (item in recyclerViewItems) {
            if (item is AdView) {
                item.resume()
            }
        }
        super.onResume()
    }

    override fun onPause() {
        for (item in recyclerViewItems) {
            if (item is AdView) {
                item.pause()
            }
        }
        super.onPause()
    }

    override fun onDestroy() {
        for (item in recyclerViewItems) {
            if (item is AdView) {
                item.destroy()
            }
        }
        super.onDestroy()
    }

    /**
     * Adds banner ads to the items list.
     */
    private fun addBannerAds() {
        // Loop through the items array and place a new banner ad in every ith position in
        // the items List.
        var i = 0
        while (i <= recyclerViewItems.size) {
            val adView = AdView(this@MakerActivity)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = UNIT_ID
            recyclerViewItems.add(i, adView)
            i += ITEMS_PER_AD
        }
    }

    /**
     * Sets up and loads the banner ads.
     */
    private fun loadBannerAds() {
        // Load the first banner ad in the items list (subsequent ads will be loaded automatically
        // in sequence).
        loadBannerAd(0)
    }

    /**
     * Loads the banner ads in the items list.
     */
    private fun loadBannerAd(index: Int) {
        if (index >= recyclerViewItems.size) {
            return
        }
        val item = recyclerViewItems[index] as? AdView
            ?: throw ClassCastException(
                "Expected item at index " + index + " to be a banner ad"
                        + " ad."
            )
        val adView = item

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                // The previous banner ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadBannerAd(index + ITEMS_PER_AD)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // The previous banner ad failed to load. Call this method again to load
                // the next ad in the items list.
                val error = String.format(
                    "domain: %s, code: %d, message: %s",
                    loadAdError.domain, loadAdError.code, loadAdError.message
                )
                Log.e(
                    "MainActivity",
                    "The previous banner ad failed to load with error: "
                            + error
                            + ". Attempting to"
                            + " load the next banner ad in the items list."
                )
                loadBannerAd(index + ITEMS_PER_AD)
            }
        }

        // Load the banner ad.
        adView.loadAd(AdRequest.Builder().build())
    }

    /**
     * Adds [MenuItem]'s from a JSON file.
     */
    private fun getMakers() {
        val makerAdapter =  MakerAdapter(this, recyclerViewItems)
        maker.adapter = makerAdapter

        // LayoutManager 설정. RecyclerView 에서는 필수
        val lm = LinearLayoutManager(this)
        maker.layoutManager = lm
        maker.setHasFixedSize(true)

        //start
        StrictMode.enableDefaults()
        try {
//            val jsonString = loadData(this, "maker")
            val assetManager = resources.assets
            val inputStream = assetManager.open("maker.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jArray = JSONArray(jsonString)

            // 모든 공지 noticeList 에 저장
            for (i in 0 until jArray.length()) {

                val obj = jArray.getJSONObject(i)

                val listLine = MakerData(
                    obj.getString("이름"),
                    obj.getString("이메일"),
                    obj.getString("깃주소"),
                    obj.getString("소속"),
                    obj.getString("사진")
                )
                recyclerViewItems.add(listLine)
            }
        } catch (e: Exception) {
            val listLine =
                MakerData(
                    e.toString(),
                    "오류",
                    "오류",
                    "오류",
                    "오류"
                )
            recyclerViewItems.add(listLine)
        }
    }
}