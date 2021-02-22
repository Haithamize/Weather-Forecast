package com.haithamghanem.weatherwizard.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


class Connectivity() {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        if(!isOnline()){
//            throw IOException() //hna lw el user msh fate7 net hyrmi exception
//        }
//        return chain.proceed(chain.request())
//    }

    companion object {

        //lazm yb2a 3ndna instance of context 3shan ageb mnha system service 3shan a3rf en el net enabled wala la2
        // w 3shan ab2a fl safe zone h5li el context eli gayli ayan kan hwa a a7awelo application context

         fun isOnline(context: Context): Boolean {
            val appContext = context.applicationContext
            val connectivityManager =
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            capabilities.also {
                if (it != null) {
                    if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                        return true
                    else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true
                    }
                }
            }
            return false
        }
    }
}