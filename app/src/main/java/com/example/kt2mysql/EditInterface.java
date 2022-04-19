package com.example.kt2mysql;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface EditInterface {
    String EDITURL = "http://192.168.1.19:8888/kt2mysql/";
    @FormUrlEncoded
    @POST("simpleedit.php")
    Call<String> getUserEdit(
            @Field("name") String name,
            @Field("username") String uname,
            @Field("password") String password
    );

}