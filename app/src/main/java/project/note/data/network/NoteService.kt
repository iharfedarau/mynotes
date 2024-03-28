package project.note.data.network

import project.note.data.Note
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NoteService {
    @GET("notes")
    suspend fun getNotes(): List<Note>
    @POST("notes")
    suspend fun insert(@Body note: Note)
    @DELETE("notes/{id}")
    suspend fun delete(@Path("id") id: Long)
    @POST("notes")
    suspend fun update(@Body note: Note)

    companion object {
        @Volatile
        private var INSTANCE: NoteService? = null
        //If u are running with emulator use URL as http://10.0.2.2:8080/ instead of localhost
        //Running from mobile app use PC IP address
        private const val BASE_URL = "http://10.0.2.2:1984"
        fun getService(): NoteService {
            return INSTANCE ?: synchronized(this) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()

                val instance =  retrofit.create(NoteService::class.java)
                INSTANCE = instance
                instance
            }
        }
    }
}