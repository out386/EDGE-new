package com.edge2.data.downloader;

/*
 * Copyright (C) 2020 Ritayan Chakraborty <ritayanout@gmail.com>
 *
 * This file is part of EDGE-new
 *
 * EDGE-new is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EDGE-new is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EDGE-new.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import android.app.Application;
import android.os.SystemClock;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class FileDownloader<T> {
    private static final int MAX_AGE = 1860000; // 31 mins
    private static RequestQueue volleyQueue;

    private boolean isInProgress;
    private StringRequest request;
    private String url;
    private Class<T> typeClass;

    public FileDownloader(Application application, Class<T> typeClass) {
        if (volleyQueue == null) {
            volleyQueue = Volley.newRequestQueue(application);
        }
        this.typeClass = typeClass;
    }

    /**
     * Downloads a file. Relies on the Cache-Control header for caching. Requests made when another
     * request for the same URL is already in progress is ignored. If a new URL is requested, the
     * previous request (if in progress) is cancelled first.
     *
     * @param liveData Results are set to this LiveData object
     * @param url      Duh
     */
    public void download(MutableLiveData<FileDownloaderModel<T>> liveData, String url) {
        if (isInProgress) {
            // If a request is already in progress for this url, ignore this call
            if (url.equals(this.url)) {
                return;
            }
            request.cancel();
        } else {
            if (url.equals(this.url)) {
                FileDownloaderModel lastModel = liveData.getValue();
                if (lastModel != null
                        && (SystemClock.elapsedRealtime() - lastModel.getTimestamp()) <= MAX_AGE) {
                    // If the URL hasn't changed, and the last result wasn't an error, ignore this call
                    if (!lastModel.isError())
                        return;
                }
            }
        }

        isInProgress = true;
        this.url = url;

        request = new StringRequest(Request.Method.GET, url,
                response -> {
                    liveData.setValue(new FileDownloaderModel<>(getFromString(response), false, url));
                    isInProgress = false;
                },
                error -> {
                    liveData.setValue(new FileDownloaderModel<>(null, true, url)); // Don't care what the error is
                    isInProgress = false;
                });

        request.setRetryPolicy(
                new DefaultRetryPolicy(15000, 0, 1f));

        volleyQueue.add(request);
    }

    private T getFromString(String string) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<T> jsonAdapter = moshi.adapter(typeClass);

        try {
            return jsonAdapter.fromJson(string);
        } catch (IOException ex) {
            return null;
        }
    }

    public String getUrl() {
        return url;
    }

}
