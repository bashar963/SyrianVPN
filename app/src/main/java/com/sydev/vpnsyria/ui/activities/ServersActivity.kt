package com.sydev.vpnsyria.ui.activities

import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sydev.vpnsyria.R
import com.sydev.vpnsyria.data.ServerRepository
import com.sydev.vpnsyria.model.Servers
import com.sydev.vpnsyria.ui.adapters.ServerItem
import com.sydev.vpnsyria.ui.adapters.ServerListAdapter
import com.sydev.vpnsyria.utilties.Constants
import com.sydev.vpnsyria.utilties.RecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_servers.*
import kotlinx.android.synthetic.main.activity_servers.toolbar2

class ServersActivity : AppCompatActivity() {
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var myDataSet= mutableListOf<ServerItem>()
    private lateinit var serverRepository : ServerRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppThemeNoActionBar)
        setContentView(R.layout.activity_servers)
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar2.navigationIcon!!.setColorFilter(ContextCompat.getColor(this,R.color.md_white_1000), PorterDuff.Mode.SRC_ATOP)

        toolbar2.setNavigationOnClickListener {
            onBackPressed()
        }
        serverRepository = ServerRepository(this.application)
        myDataSet.add(ServerItem("all",resources.getIdentifier("all","drawable",packageName),getString(R.string.Auto_select_server),getDrawable(R.drawable.ic_connect_excellent)!!))
        initList()

    }


    private fun initList(){

        viewManager = LinearLayoutManager(this)
        viewAdapter =ServerListAdapter(myDataSet)
        serverList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        fillTheServerList()
        serverList.addOnItemTouchListener(RecyclerItemClickListener(this,serverList,object : RecyclerItemClickListener.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@ServersActivity,HomeActivity::class.java)
                intent.putExtra(Constants.From_Server_List,true)
                intent.putExtra(Constants.KEY_COUNTRY_LONG,myDataSet[position].serverName)
                startActivity(intent)
                this@ServersActivity.finish()
            }

            override fun onLongItemClick(view: View?, position: Int) { }
        }))

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ServersActivity,HomeActivity::class.java)
        intent.putExtra(Constants.From_Server_List,true)
        intent.putExtra(Constants.KEY_COUNTRY_LONG,getString(R.string.Auto_select_server))
        startActivity(intent)
        this@ServersActivity.finish()
    }
    private fun fillTheServerList() {
        val countryServerList = serverRepository.getUniqueCountries()
        parseToItemList(countryServerList)
        serverList.adapter!!.notifyDataSetChanged()
    }

    private fun parseToItemList(countryServerList: List<Servers>) {
        for (i in 0 until countryServerList.size){
            val serverItem=ServerItem("",0,"",getDrawable(R.drawable.ic_connect_excellent)!!)
            serverItem.serverId = countryServerList[i].ip!!
            serverItem.serverName = countryServerList[i].countryLong!!
            when(countryServerList[i].quality){
                1->{serverItem.ServerStrengthRes=getDrawable(R.drawable.ic_connect_bad)!!}
                2->{serverItem.ServerStrengthRes=getDrawable(R.drawable.ic_connect_good)!!}
                3->{serverItem.ServerStrengthRes=getDrawable(R.drawable.ic_connect_excellent)!!}
            }
            var code = countryServerList[i].countryShort!!.toLowerCase()
            if (code=="do"){
                code="dom"
            }

          serverItem.flagRes =  resources.getIdentifier(code,"drawable",packageName)
            myDataSet.add(serverItem)
        }
    }


}
