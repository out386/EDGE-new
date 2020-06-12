package com.edge2.registration;

import com.edge2.registration.models.RegisterResp;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RegistrationInterface {
    @Multipart
    @POST("users/register")
    Call<RegisterResp> register(@Part("name") String name, @Part("email") String email,
                                @Part("stream") String stream, @Part("year") String year,
                                @Part("instituteName") String instituteName,
                                @Part("contact") String contact, @Part("password") String password,
                                @Part("verified")
                                /*@Part MultipartBody.Part file*/);
};
