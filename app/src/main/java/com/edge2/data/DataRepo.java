package com.edge2.data;

/*
 * Copyright (C) 2019 Ritayan Chakraborty <ritayanout@gmail.com>
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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.eventdetails.models.EventDetailsModel;
import com.edge2.utils.Logger;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DataRepo {
    private static final String KEY_DB_VERSION = "dbVersion";
    private FirebaseFirestore db;
    private MutableLiveData<List<BannerItemsModel>> eventNamesData;

    DataRepo() {
        db = FirebaseFirestore.getInstance();
    }

    MutableLiveData<List<BannerItemsModel>> loadBanner() {
        if (eventNamesData == null)
            eventNamesData = new MutableLiveData<>();

        db.collection("banner")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<BannerItemsModel> results = new ArrayList<>();
                    for (QueryDocumentSnapshot document : snapshot) {
                        BannerItemsModel model = new BannerItemsModel(document);
                        results.add(model);
                    }
                    eventNamesData.setValue(results);
                });

        return eventNamesData;
    }

    // TODO: Might be a good idea to hold all requests that are made while this is updating.
    void updateDb(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int currentVersion = updateVersionInCache(prefs);
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        getRemoteDbVersion(queue, availableVersion -> {
            if (availableVersion < 0) {
                Logger.log("DataRepo", "updateDb: Failed to get new version number");
                return;
            }
            if (availableVersion > currentVersion) {
                Logger.log("DataRepo", "updateDb: update found: " + availableVersion);
                downloadUpdate(queue, items -> {
                    if (items == null) {
                        Logger.log("DataRepo", "updateDb: Failed to fetch update");
                    } else {
                        prefs.edit()
                                .putInt(KEY_DB_VERSION, availableVersion)
                                .apply();
                        Logger.log("DataRepo", "updateDb: Items: " + items.size());
                        updateOfflineDb(context, items);
                    }
                });
            }
        });
    }

    private void downloadUpdate(RequestQueue queue, OnVersionFetchedListener listener) {
        String url = "https://edge-new-a7306.firebaseapp.com/EventDetails.json";
        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    List<EventDetailsModel> items = processDetailsJson(response);
                    if (items == null || items.size() == 0) {
                        listener.onVersionFetched(null);
                    } else {
                        listener.onVersionFetched(items);
                    }
                },
                error -> listener.onVersionFetched(null));

        req.setShouldCache(false);
        queue.add(req);
    }

    /**
     * If {@link AppDatabase#DB_VERSION} is greater than the value in sharedprefs, update
     * sharedprefs, since the corresponding database version has already been included as a
     * prepackaged db for Room, and we don't need to download updates.
     */
    private int updateVersionInCache(SharedPreferences prefs) {
        int dbVersion = prefs.getInt(KEY_DB_VERSION, AppDatabase.DB_VERSION);
        if (dbVersion < AppDatabase.DB_VERSION) {
            dbVersion = AppDatabase.DB_VERSION;
            prefs.edit()
                    .putInt(KEY_DB_VERSION, AppDatabase.DB_VERSION)
                    .apply();
        }
        return dbVersion;
    }

    /**
     * Checks to see if a new database update is available
     */
    private void getRemoteDbVersion(RequestQueue queue, OnVersionAvailableListener listener) {
        String url = "https://edge-new-a7306.firebaseapp.com/db_version.txt";

        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    int ver;
                    response = response.trim();
                    try {
                        ver = Integer.parseInt(response);
                    } catch (NumberFormatException e) {
                        ver = -1;
                    }
                    listener.onVersionAvailable(ver);
                },
                error -> listener.onVersionAvailable(-1));

        req.setShouldCache(false);
        queue.add(req);
    }

    private List<EventDetailsModel> processDetailsJson(String res) {
        try {
            JSONArray a = new JSONArray(res);
            List<EventDetailsModel> items = new LinkedList<>();
            for (int i = 0; i < a.length(); i++) {
                JSONObject item = a.getJSONObject(i);
                EventDetailsModel model = EventDetailsModel.getFromJson(item);
                if (model != null) {
                    items.add(model);
                }
            }
            return items;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateOfflineDb(Context context, List<EventDetailsModel> items) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> AppDatabase.getDatabase(context).getDao().putDetails(items));
    }

    private interface OnVersionAvailableListener {
        void onVersionAvailable(int version);
    }

    private interface OnVersionFetchedListener {
        void onVersionFetched(List<EventDetailsModel> items);
    }
}
