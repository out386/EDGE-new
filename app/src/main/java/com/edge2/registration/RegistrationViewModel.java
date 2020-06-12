package com.edge2.registration;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edge2.registration.models.RegisterResp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationViewModel extends AndroidViewModel {
    private static RegistrationInterface retrofitReg;
    private Context context;

    public RegistrationViewModel(Application application) {
        super(application);
        this.context = application;

        retrofitReg = new Retrofit.Builder()
                .baseUrl("https://register.edg.co.in")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RegistrationInterface.class);
    }

    LiveData<RegisterResp> register(String name, String email, String stream, String year,
                                    String instituteName, String contact, String password) {

        MutableLiveData<RegisterResp> liveData = new MutableLiveData<>();
/*
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file", fileName, RequestBody.create(MediaType.parse("image/*"), file));*/

        Log.i("blah", "register: ");
        retrofitReg.register(name, email, stream, year, instituteName, contact, password)
                .enqueue(new Callback<RegisterResp>() {
                    @Override
                    public void onResponse(@NotNull Call<RegisterResp> call,
                                           @NotNull Response<RegisterResp> response) {
                        try {
                            Log.i("blah", "onResponse: " + response.errorBody().string());
                        } catch (IOException e)  {
                            Log.i("blah", "onResponse: urgh");
                        }
                        if (response.isSuccessful())
                            liveData.setValue(response.body());
                        liveData.setValue(null);
                    }

                    @Override
                    public void onFailure(@NotNull Call<RegisterResp> call, @NotNull Throwable t) {
                        Log.e("blah", "onFailure: ", t);
                        liveData.setValue(null);
                    }
                });

        return liveData;
    }
}
