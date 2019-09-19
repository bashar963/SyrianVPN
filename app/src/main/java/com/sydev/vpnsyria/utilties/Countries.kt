package com.sydev.vpnsyria.utilties

import java.util.*

class Countries {

    companion object{
        fun getCountries():Map<String,String>{

            val countries = HashMap<String, String>()

            val isoCountries = Locale.getISOCountries()
            for (country in isoCountries) {
                val locale = Locale("", country)
                val iso = locale.isO3Country
                val code = locale.country
                val name = locale.displayCountry

                if ( !iso.isNullOrEmpty() && !code.isNullOrEmpty() &&  !name.isNullOrEmpty()) {
                    countries[code] = name
                }
            }

            return countries
        }
    }
}