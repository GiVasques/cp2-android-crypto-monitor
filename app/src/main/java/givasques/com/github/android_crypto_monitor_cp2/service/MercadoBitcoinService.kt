package givasques.com.github.android_crypto_monitor_cp2.service

import givasques.com.github.android_crypto_monitor_cp2.model.TickerResponse
import retrofit2.Response
import retrofit2.http.GET

interface MercadoBitcoinService {
    @GET("api/BTC/ticker/")
    suspend fun getTicker(): Response<TickerResponse>
}