package com.app.novia.core.data.source.remote

import android.util.Log
import com.app.novia.core.domain.model.ChatEntity
import com.app.novia.core.domain.model.NewsResponse
import com.app.novia.core.domain.model.UserResponseEntity
import com.app.novia.core.utils.EspressoIdlingResource
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteDataSource(private val apiService: ApiService) {
    suspend fun sendChat(message: JsonObject?): Flow<ApiResponse<ChatEntity>> {
        Log.d("SEENDCHAT", message.toString())
        EspressoIdlingResource.increment()
        return flow {
            try {
                val response = apiService.sendChat(message)
                lateinit var data: ChatEntity

                if (response.message?.isNotEmpty() == true) {
                    response.senderIsBot = true
                    data = response
                    emit(ApiResponse.Success(data))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
            }
            EspressoIdlingResource.decrement()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addUser(user: JsonObject?): Flow<ApiResponse<UserResponseEntity>> {
        EspressoIdlingResource.increment()
        return flow {
            try {
                val response = apiService.addUser(user)
                if (response.statusCode == 204) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Error("Gagal menambahkan pengguna ke database"))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
            }
            EspressoIdlingResource.decrement()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getNews(): Flow<ApiResponse<NewsResponse>> {
        EspressoIdlingResource.increment()
        return flow {
            try {
                val response = apiService.getNews()
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
            }
            EspressoIdlingResource.decrement()
        }.flowOn(Dispatchers.IO)
    }
}