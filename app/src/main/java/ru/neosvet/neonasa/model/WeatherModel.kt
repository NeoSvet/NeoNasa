package ru.neosvet.neonasa.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.neosvet.neonasa.BuildConfig
import ru.neosvet.neonasa.repository.*
import java.lang.StringBuilder
import kotlin.collections.ArrayList


enum class TypeWeather(val index: Int) {
    CME(0), GST(1), IPS(2), FLR(3), SEP(4), MPC(5), RBE(6), HSS(7), WSA(8)
}

class WeatherModel : ViewModel() {
    private val state: MutableLiveData<WeatherState> = MutableLiveData()
    private val retrofitImpl = NasaRetrofit()

    fun getState() = state

    fun requestWeather(type: TypeWeather) {
        state.value = WeatherState.Loading
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            PhotoState.Error(Throwable("You need API key"))
        } else {
            when (type) {
                TypeWeather.CME ->
                    retrofitImpl.getRetrofit().getWeatherCME(apiKey).enqueue(cmeResponse)
                TypeWeather.GST ->
                    retrofitImpl.getRetrofit().getWeatherGST(apiKey).enqueue(gstResponse)
                TypeWeather.IPS ->
                    retrofitImpl.getRetrofit().getWeatherIPS(apiKey).enqueue(ipsResponse)
                TypeWeather.FLR ->
                    retrofitImpl.getRetrofit().getWeatherFLR(apiKey).enqueue(flrResponse)
                TypeWeather.SEP ->
                    retrofitImpl.getRetrofit().getWeatherSEP(apiKey).enqueue(sepResponse)
                TypeWeather.MPC ->
                    retrofitImpl.getRetrofit().getWeatherMPC(apiKey).enqueue(mpcResponse)
                TypeWeather.RBE ->
                    retrofitImpl.getRetrofit().getWeatherRBE(apiKey).enqueue(rbeResponse)
                TypeWeather.HSS ->
                    retrofitImpl.getRetrofit().getWeatherHSS(apiKey).enqueue(hssResponse)
                TypeWeather.WSA ->
                    retrofitImpl.getRetrofit().getWeatherWSA(apiKey).enqueue(wsaResponse)
            }
        }
    }

    private fun onEmpty() {
        state.value = WeatherState.Error(Throwable("Response is empty"))
    }

    private fun onError(message: String?) {
        if (message.isNullOrEmpty()) {
            state.value = WeatherState.Error(Throwable("Unidentified error"))
        } else {
            state.value = WeatherState.Error(Throwable(message))
        }
    }

    private val cmeResponse = object : Callback<List<WeatherCMEResponse>> {
        override fun onResponse(
            call: Call<List<WeatherCMEResponse>>,
            rawResponse: Response<List<WeatherCMEResponse>>
        ) {
            if (rawResponse.isSuccessful) {
                val response = rawResponse.body()
                if (response == null || response.size == 0) {
                    onEmpty()
                } else {
                    state.value = WeatherState.Success(responseToList(response[0]))
                }
            } else {
                onError(rawResponse.message())
            }
        }

        override fun onFailure(call: Call<List<WeatherCMEResponse>>, error: Throwable) {
            state.value = WeatherState.Error(error)
        }
    }

    private fun itemsToList(
        link: String?,
        linkedEvents: List<LinkedEvent>?,
        instruments: List<Instrument>?
    ): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        link?.let {
            list.add(Pair("link", it))
        }
        val s = StringBuilder()
        linkedEvents?.forEach {
            s.append(", ")
            s.append(it.activityID)
        }
        if (s.length > 2) {
            s.delete(0, 2)
            list.add(Pair("linkedEvents", s.toString()))
            s.clear()
        }
        instruments?.forEach {
            s.append(", ")
            s.append(it.displayName)
        }
        if (s.length > 2) {
            s.delete(0, 2)
            list.add(Pair("instruments", s.toString()))
            s.clear()
        }
        return list
    }

    private fun responseToList(response: WeatherCMEResponse): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        list.add(Pair("ID", response.activityID))
        list.add(Pair("catalog", response.catalog))
        list.add(Pair("startTime", response.startTime))
        response.note?.let {
            list.add(Pair("note", it))
        }
        list.add(Pair("sourceLocation", response.sourceLocation))
        list.add(Pair("activeRegionNum", response.activeRegionNum.toString()))
        list.addAll(itemsToList(response.link, response.linkedEvents, response.instruments))
        return list
    }

    private val gstResponse = object : Callback<List<WeatherGSTResponse>> {
        override fun onResponse(
            call: Call<List<WeatherGSTResponse>>,
            rawResponse: Response<List<WeatherGSTResponse>>
        ) {
            if (rawResponse.isSuccessful) {
                val response = rawResponse.body()
                if (response == null || response.size == 0) {
                    onEmpty()
                } else {
                    state.value = WeatherState.Success(responseToList(response[0]))
                }
            } else {
                onError(rawResponse.message())
            }
        }

        override fun onFailure(call: Call<List<WeatherGSTResponse>>, error: Throwable) {
            state.value = WeatherState.Error(error)
        }
    }

    private fun responseToList(response: WeatherGSTResponse): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        list.add(Pair("ID", response.gstID))
        list.add(Pair("startTime", response.startTime))
        list.addAll(itemsToList(response.link, response.linkedEvents, null))
        return list
    }

    private val ipsResponse = object : Callback<List<WeatherIPSResponse>> {
        override fun onResponse(
            call: Call<List<WeatherIPSResponse>>,
            rawResponse: Response<List<WeatherIPSResponse>>
        ) {
            if (rawResponse.isSuccessful) {
                val response = rawResponse.body()
                if (response == null || response.size == 0) {
                    onEmpty()
                } else {
                    state.value = WeatherState.Success(responseToList(response[0]))
                }
            } else {
                onError(rawResponse.message())
            }
        }

        override fun onFailure(call: Call<List<WeatherIPSResponse>>, error: Throwable) {
            state.value = WeatherState.Error(error)
        }
    }

    private fun responseToList(response: WeatherIPSResponse): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        list.add(Pair("ID", response.activityID))
        list.add(Pair("catalog", response.catalog))
        list.add(Pair("location", response.location))
        list.addAll(itemsToList(response.link, null, response.instruments))
        return list
    }

    private val flrResponse = object : Callback<List<WeatherFLRResponse>> {
        override fun onResponse(
            call: Call<List<WeatherFLRResponse>>,
            rawResponse: Response<List<WeatherFLRResponse>>
        ) {
            if (rawResponse.isSuccessful) {
                val response = rawResponse.body()
                if (response == null || response.size == 0) {
                    onEmpty()
                } else {
                    state.value = WeatherState.Success(responseToList(response[0]))
                }
            } else {
                onError(rawResponse.message())
            }
        }

        override fun onFailure(call: Call<List<WeatherFLRResponse>>, error: Throwable) {
            state.value = WeatherState.Error(error)
        }
    }

    private fun responseToList(response: WeatherFLRResponse): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        list.add(Pair("ID", response.flrID))
        list.add(Pair("startTime", response.beginTime))
        list.add(Pair("endTime", response.endTime))
        list.add(Pair("activeRegionNum", response.activeRegionNum.toString()))
        list.add(Pair("sourceLocation", response.sourceLocation))
        list.addAll(itemsToList(response.link, response.linkedEvents, response.instruments))
        return list
    }

    private val sepResponse = object : Callback<List<WeatherSEPResponse>> {
        override fun onResponse(
            call: Call<List<WeatherSEPResponse>>,
            rawResponse: Response<List<WeatherSEPResponse>>
        ) {
            if (rawResponse.isSuccessful) {
                val response = rawResponse.body()
                if (response == null || response.size == 0) {
                    onEmpty()
                } else {
                    state.value = WeatherState.Success(responseToList(response[0]))
                }
            } else {
                onError(rawResponse.message())
            }
        }

        override fun onFailure(call: Call<List<WeatherSEPResponse>>, error: Throwable) {
            state.value = WeatherState.Error(error)
        }
    }

    private fun responseToList(response: WeatherSEPResponse): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        list.add(Pair("ID", response.sepID))
        list.add(Pair("eventTime", response.eventTime))
        list.addAll(itemsToList(response.link, response.linkedEvents, null))
        return list
    }

    private val mpcResponse = object : Callback<List<WeatherMPCResponse>> {
        override fun onResponse(
            call: Call<List<WeatherMPCResponse>>,
            rawResponse: Response<List<WeatherMPCResponse>>
        ) {
            if (rawResponse.isSuccessful) {
                val response = rawResponse.body()
                if (response == null || response.size == 0) {
                    onEmpty()
                } else {
                    state.value = WeatherState.Success(responseToList(response[0]))
                }
            } else {
                onError(rawResponse.message())
            }
        }

        override fun onFailure(call: Call<List<WeatherMPCResponse>>, error: Throwable) {
            state.value = WeatherState.Error(error)
        }
    }

    private fun responseToList(response: WeatherMPCResponse): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        list.add(Pair("ID", response.mpcID))
        list.add(Pair("eventTime", response.eventTime))
        list.addAll(itemsToList(response.link, response.linkedEvents, response.instruments))
        return list
    }

    private val rbeResponse = object : Callback<List<WeatherRBEResponse>> {
        override fun onResponse(
            call: Call<List<WeatherRBEResponse>>,
            rawResponse: Response<List<WeatherRBEResponse>>
        ) {
            if (rawResponse.isSuccessful) {
                val response = rawResponse.body()
                if (response == null || response.size == 0) {
                    onEmpty()
                } else {
                    state.value = WeatherState.Success(responseToList(response[0]))
                }
            } else {
                onError(rawResponse.message())
            }
        }

        override fun onFailure(call: Call<List<WeatherRBEResponse>>, error: Throwable) {
            state.value = WeatherState.Error(error)
        }
    }

    private fun responseToList(response: WeatherRBEResponse): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        list.add(Pair("ID", response.rbeID))
        list.add(Pair("eventTime", response.eventTime))
        list.addAll(itemsToList(response.link, response.linkedEvents, response.instruments))
        return list
    }

    private val hssResponse = object : Callback<List<WeatherHSSResponse>> {
        override fun onResponse(
            call: Call<List<WeatherHSSResponse>>,
            rawResponse: Response<List<WeatherHSSResponse>>
        ) {
            if (rawResponse.isSuccessful) {
                val response = rawResponse.body()
                if (response == null || response.size == 0) {
                    onEmpty()
                } else {
                    state.value = WeatherState.Success(responseToList(response[0]))
                }
            } else {
                onError(rawResponse.message())
            }
        }

        override fun onFailure(call: Call<List<WeatherHSSResponse>>, error: Throwable) {
            state.value = WeatherState.Error(error)
        }
    }

    private fun responseToList(response: WeatherHSSResponse): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        list.add(Pair("ID", response.hssID))
        list.add(Pair("eventTime", response.eventTime))
        list.addAll(itemsToList(response.link, response.linkedEvents, response.instruments))
        return list
    }

    private val wsaResponse = object : Callback<List<WeatherWSAResponse>> {
        override fun onResponse(
            call: Call<List<WeatherWSAResponse>>,
            rawResponse: Response<List<WeatherWSAResponse>>
        ) {
            if (rawResponse.isSuccessful) {
                val response = rawResponse.body()
                if (response == null || response.size == 0) {
                    onEmpty()
                } else {
                    state.value = WeatherState.Success(responseToList(response[0]))
                }
            } else {
                onError(rawResponse.message())
            }
        }

        override fun onFailure(call: Call<List<WeatherWSAResponse>>, error: Throwable) {
            state.value = WeatherState.Error(error)
        }
    }

    private fun responseToList(response: WeatherWSAResponse): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        list.add(Pair("ID", response.simulationID))
        list.add(Pair("modelCompletionTime", response.modelCompletionTime))
        response.estimatedShockArrivalTime?.let {
            list.add(Pair("estimatedShockArrivalTime", it))
        }
        response.estimatedDuration?.let {
            list.add(Pair("estimatedDuration", it))
        }
        response.link?.let {
            list.add(Pair("link", it))
        }
        response.rminRe?.let {
            list.add(Pair("rmin_re", it))
        }
        response.kp18?.let {
            list.add(Pair("kp_18", it))
        }
        response.kp90?.let {
            list.add(Pair("kp_90", it))
        }
        response.kp135?.let {
            list.add(Pair("kp_135", it))
        }
        response.kp180?.let {
            list.add(Pair("kp_180", it))
        }
        list.add(Pair("isEarthGB", response.isEarthGB.toString()))
        return list
    }

}