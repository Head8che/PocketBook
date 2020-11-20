package com.example.pocketbook.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
        @Headers(
                {
                        "Content-Type:application/json",
                        "Authorization:key=AAAA2RApn0g:APA91bEiXbjNLJr-sIl5q8B0wPq3cgB-iA0SpSHdYuEXwkzgakfOfDUt6e7dIR75TqWevQy0WiOGJsBRIi4AQGLFNy0AF0zd6sXDitqnimki9tFWvbI30Sdf9IBafc1uEIRJJA_JlC2S"
                }
        )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}
