package com.sydev.vpnsyria.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.sectionHeader
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.sydev.vpnsyria.R
import com.sydev.vpnsyria.ui.fragments.HomeFragment
import com.sydev.vpnsyria.utilties.Constants
import kotlinx.android.synthetic.main.home_activity.*
import de.cketti.mailto.EmailIntentBuilder



class HomeActivity : AppCompatActivity() {
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppThemeNoActionBar)
        setContentView(R.layout.home_activity)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment.newInstance(),"vpn")
                .commitNow()
        }

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-9576521051407477/2134290356"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        initDrawer(savedInstanceState)
        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                if (this@HomeActivity.intent.getBooleanExtra(Constants.fromSplash,false))
                mInterstitialAd.show()
            }
        }

    }



    private fun initDrawer(savedInstanceState: Bundle?){


        drawer{

            toolbar = this@HomeActivity.toolbar
            savedInstance = savedInstanceState
            hasStableIds=true
            actionBarDrawerToggleAnimated=true
            headerViewRes=R.layout.headerview

            sectionHeader(getString(R.string.general)) {
                divider=false
            }

            primaryItem(getString(R.string.home)) {
                iicon = GoogleMaterial.Icon.gmd_home
                identifier=1L
            }

            primaryItem (getString(R.string.choose_server)){
                iicon = GoogleMaterial.Icon.gmd_flag
                selectable=false
                onClick { _->
                    this@HomeActivity.startActivity(Intent(this@HomeActivity, ServersActivity::class.java))
                    finish()
                    false
                }

            }
            primaryItem (getString(R.string.refresh)){
                iicon= GoogleMaterial.Icon.gmd_refresh
                selectable=false
                onClick {_->
                    this@HomeActivity.startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                    this@HomeActivity.finish()
                    false
                }
            }

            sectionHeader (getString(R.string.problemAndSolution)){
                divider=true

            }
            primaryItem (getString(R.string.report)){
                iicon= GoogleMaterial.Icon.gmd_report_problem
                selectable=false
                onClick { _->
                    reportIssue()
                    false
                }
            }
            primaryItem (getString(R.string.tellFriends)){
                iicon = GoogleMaterial.Icon.gmd_share
                selectable=false
                onClick { _->
                    shareApp()
                    false
                }
            }
            sectionHeader (getString(R.string.Preference)){
                divider=true
            }
            primaryItem (getString(R.string.settings)){
                iicon=GoogleMaterial.Icon.gmd_settings
                selectable=false
                onClick { _->
                    this@HomeActivity.startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
                    false
                }
            }
            primaryItem (getString(R.string.about)){
                iicon=GoogleMaterial.Icon.gmd_help
                selectable=false
                onClick { _->
                    this@HomeActivity.startActivity(Intent(this@HomeActivity, AboutActivity::class.java))
                    this@HomeActivity.finish()
                    false
                }
            }

        }.setSelection(1L)

    }

    private fun reportIssue() {

        EmailIntentBuilder.from(this)
            .to("syriandev96@gmail.com")
            .subject("report an issue")
            .body("")
            .start()
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))

        val extraText = getString(R.string.i_recommend) +" : "+ getString(R.string.playstore_link)

        intent.putExtra(Intent.EXTRA_TEXT, extraText)
        startActivity(Intent.createChooser(intent, getString(R.string.action_share_app)))
    }



}
