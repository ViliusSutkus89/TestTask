package com.viliussutkus89.codenet.tt

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


data class Person(
    val name: String,
    val email: String,
    val address: String,
    val thumbnail: String
)

@JsonClass(generateAdapter = true)
data class NetworkPersonName(
    val title: String,
    val first: String,
    val last: String
)

@JsonClass(generateAdapter = true)
data class NetworkPersonLocation(
    val city: String,
    val country: String
)

@JsonClass(generateAdapter = true)
data class NetworkPersonPicture(
    val thumbnail: String
)

@JsonClass(generateAdapter = true)
data class NetworkPerson(
    val name: NetworkPersonName,
    val email: String,
    val location: NetworkPersonLocation,
    val picture: NetworkPersonPicture
)

@JsonClass(generateAdapter = true)
data class NetworkResults(val results: List<NetworkPerson>) {
    fun asPersons(): List<Person> {
        return results.map {
            Person(
                name = "${it.name.title} ${it.name.first} ${it.name.last}",
                email = it.email,
                address = "${it.location.city}, ${it.location.country}",
                thumbnail = it.picture.thumbnail
            )
        }
    }
}

interface RandomPersonService {
    @GET("/api")
    suspend fun getNetworkResults(): NetworkResults
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl("https://randomuser.me")
    .build()


object PersonsNetwork {
    val retrofitService: RandomPersonService by lazy {
        retrofit.create(RandomPersonService::class.java)
    }
}
