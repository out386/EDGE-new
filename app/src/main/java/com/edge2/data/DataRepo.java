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
import android.os.SystemClock;
import android.preference.PreferenceManager;

import androidx.lifecycle.LiveData;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.allevents.models.GroupsModel;
import com.edge2.event.EventCategoryModel;
import com.edge2.eventdetails.models.EventDetailsModel;
import com.edge2.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataRepo {
    private static final String KEY_DETAILS_DB_VERSION = "detailsDbVersion";
    private static final String KEY_BANNER_DB_VERSION = "bannerDbVersion";
    private static final long UPDATE_INTERVAL = 1800000; // 30 minutes
    private static DataRepo repo;

    private boolean isUpdating;
    // 0 = downloading, -1 = failed, 1 = succeeded
    private int detailsCode;
    private int bannerCode;
    private long lastUpdateTime;
    private RunningOutOfNamesDao dao;
    private Context context;

    private DataRepo(Context context) {
        this.context = context.getApplicationContext();
        dao = AppDatabase.getDatabase(this.context).getDao();
    }

    public static DataRepo getInstance(Context context) {
        if (repo == null) {
            repo = new DataRepo(context);
        }
        return repo;
    }

    LiveData<List<BannerItemsModel>> getBannerItems() {
        return dao.getBannerItems();
    }

    LiveData<EventDetailsModel> getDetails(String name) {
        return dao.getDetails(name);
    }

    LiveData<List<EventCategoryModel>> getCategories(boolean isIntra,
                                                     String groupName) {
        if (isIntra)
            return dao.getCategoriesIntra(groupName);
        else
            return dao.getCategoriesEdge(groupName);
    }

    LiveData<List<GroupsModel>> getGroups(boolean isIntra) {
        if (isIntra)
            return dao.getGroupsIntra();
        else
            return dao.getGroupsEdge();
    }

    /**
     * Check if an update to event details is available. If yes, download it and update the offline
     * db. Warning: db requests that are already in-flight will get outdated data. It's safe to call
     * this method multiple times. It only checks once every {@link #UPDATE_INTERVAL} seconds. Don't
     * use a JobService or similar for updates, as that'll use up server bandwidth. It's better to
     * serve some users slightly old data than have updates go down for the month.
     */
    public void updateDb() {
        if (isUpdating || SystemClock.uptimeMillis() - lastUpdateTime < UPDATE_INTERVAL)
            return;
        isUpdating = true;
        bannerCode = detailsCode = 0;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        RequestQueue queue = Volley.newRequestQueue(context);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        updateDetails(prefs, queue, executor, code -> {
            if (code == -1) {
                detailsCode = -1;
                if (bannerCode != 0) {  // Both downloads are done
                    isUpdating = false;
                    lastUpdateTime = 0;  // This update failed, so allow retries
                }   // Else let the listener for the other download handle this
            } else {
                detailsCode = 1;
                if (bannerCode == 1) {  // Both downloads completed successfully
                    lastUpdateTime = SystemClock.uptimeMillis();
                    isUpdating = false;
                } else if (bannerCode == -1) {  // The other download failed, so allow retries
                    isUpdating = false;
                    lastUpdateTime = 0;
                }   // Else let the listener for the other download handle this
            }
        });

        updateBanner(prefs, queue, executor, code -> {
            if (code == -1) {
                bannerCode = -1;
                if (detailsCode != 0) {  // Both downloads are done
                    isUpdating = false;
                    lastUpdateTime = 0;  // This update failed, so allow retries
                }   // Else let the listener for the other download handle this
            } else {
                bannerCode = 1;
                if (detailsCode == 1) {  // Both downloads completed successfully
                    lastUpdateTime = SystemClock.uptimeMillis();
                    isUpdating = false;
                } else if (detailsCode == -1) {  // The other download failed, so allow retries
                    isUpdating = false;
                    lastUpdateTime = 0;
                }   // Else let the listener for the other download handle this
            }
        });
    }

    //TODO: Fix the race condition with lastUpdateTime in case just one of banner or details update fail

    private void updateDetails(SharedPreferences prefs, RequestQueue queue, ExecutorService executor,
                               OnDownloadCompleteListener completeListener) {
        int currentVersion = updateDetailsVersionInCache(prefs);

        getDetailsRemoteDbVersion(queue, availableVersion -> {
            if (availableVersion < 0) {
                Logger.log("DataRepo", "updateDb: Failed to get new version number");
                completeListener.onDownloadComplete(-1);
                return;
            }
            if (availableVersion > currentVersion) {
                Logger.log("DataRepo", "updateDb: update found: " + availableVersion);
                downloadUpdateDetails(queue, items -> {
                    if (items == null) {
                        Logger.log("DataRepo", "updateDb: Failed to fetch update");
                        completeListener.onDownloadComplete(-1);
                    } else {
                        prefs.edit()
                                .putInt(KEY_DETAILS_DB_VERSION, availableVersion)
                                .apply();
                        Logger.log("DataRepo", "updateDb: Items: " + items.size());
                        if (items.size() > 0)
                            updateOfflineDetailsDb(executor, items);
                        completeListener.onDownloadComplete(1);
                    }
                });
            } else {
                completeListener.onDownloadComplete(1);
            }
        });
    }

    private void updateBanner(SharedPreferences prefs, RequestQueue queue, ExecutorService executor,
                              OnDownloadCompleteListener completeListener) {
        int currentVersion = prefs.getInt(KEY_BANNER_DB_VERSION, 0);

        getBannerRemoteDbVersion(queue, availableVersion -> {
            if (availableVersion < 0) {
                Logger.log("DataRepo", "updateDb: Failed to get new banner version number");
                completeListener.onDownloadComplete(-1);
                return;
            }
            if (availableVersion > currentVersion) {
                Logger.log("DataRepo", "updateDb: banner update found: " + availableVersion);
                downloadUpdateBanner(queue, items -> {
                    if (items == null) {
                        Logger.log("DataRepo", "updateDb: Failed to fetch banner update");
                        completeListener.onDownloadComplete(-1);
                    } else {
                        prefs.edit()
                                .putInt(KEY_BANNER_DB_VERSION, availableVersion)
                                .apply();
                        Logger.log("DataRepo", "updateDb: Banner items: " + items.size());
                        if (items.size() > 0)
                            updateOfflineBannerDb(executor, items);
                        completeListener.onDownloadComplete(1);
                    }
                });
            } else {
                completeListener.onDownloadComplete(1);
            }
        });
    }

    private void downloadUpdateDetails(RequestQueue queue, OnDetailsVersionFetchedListener listener) {
        String url = "https://edge-new-a7306.firebaseapp.com/EventDetails.json";
        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    List<EventDetailsModel> items = processDetailsJson(response);
                    if (items == null) {
                        listener.onVersionFetched(null);
                    } else {
                        listener.onVersionFetched(items);
                    }
                },
                error -> listener.onVersionFetched(null));

        req.setShouldCache(false).setRetryPolicy(
                new DefaultRetryPolicy(15000, 0, 1f));
        queue.add(req);
    }

    private void downloadUpdateBanner(RequestQueue queue, OnBannerVersionFetchedListener listener) {
        String url = "https://edge-new-a7306.web.app/BannerItems.json";
        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    List<BannerItemsModel> items = processBannerJson(response);
                    if (items == null) {
                        listener.onVersionFetched(null);
                    } else {
                        listener.onVersionFetched(items);
                    }
                },
                error -> listener.onVersionFetched(null));

        req.setShouldCache(false).setRetryPolicy(
                new DefaultRetryPolicy(15000, 0, 1f));
        queue.add(req);
    }

    /**
     * If {@link AppDatabase#DB_VERSION} is greater than the value in sharedprefs, update
     * sharedprefs, since the corresponding database version has already been included as a
     * prepackaged db for Room, and we don't need to download updates.
     */
    private int updateDetailsVersionInCache(SharedPreferences prefs) {
        int dbVersion = prefs.getInt(KEY_DETAILS_DB_VERSION, AppDatabase.DB_VERSION);
        if (dbVersion < AppDatabase.DB_VERSION) {
            dbVersion = AppDatabase.DB_VERSION;
            prefs.edit()
                    .putInt(KEY_DETAILS_DB_VERSION, AppDatabase.DB_VERSION)
                    .apply();
        }
        return dbVersion;
    }

    /**
     * Checks to see if a new database update is available
     */
    private void getDetailsRemoteDbVersion(RequestQueue queue, OnVersionAvailableListener listener) {
        String url = "https://edge-new-a7306.firebaseapp.com/details_db_version.txt";

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

        req.setShouldCache(false).setRetryPolicy(
                new DefaultRetryPolicy(15000, 0, 1f));
        queue.add(req);
    }

    private void getBannerRemoteDbVersion(RequestQueue queue, OnVersionAvailableListener listener) {
        String url = "https://edge-new-a7306.firebaseapp.com/banner_db_version.txt";

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

        req.setShouldCache(false).setRetryPolicy(
                new DefaultRetryPolicy(15000, 0, 1f));
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

    private List<BannerItemsModel> processBannerJson(String res) {
        try {
            JSONArray a = new JSONArray(res);
            List<BannerItemsModel> items = new LinkedList<>();
            for (int i = 0; i < a.length(); i++) {
                JSONObject item = a.getJSONObject(i);
                BannerItemsModel model = BannerItemsModel.getFromJson(item);
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

    private void updateOfflineDetailsDb(ExecutorService executor, List<EventDetailsModel> items) {
        executor.submit(() -> dao.putDetails(items));
    }

    private void updateOfflineBannerDb(ExecutorService executor, List<BannerItemsModel> items) {
        executor.submit(() -> dao.putBanner(items));
    }

    private interface OnVersionAvailableListener {
        void onVersionAvailable(int version);
    }

    private interface OnDetailsVersionFetchedListener {
        void onVersionFetched(List<EventDetailsModel> items);
    }

    private interface OnBannerVersionFetchedListener {
        void onVersionFetched(List<BannerItemsModel> items);
    }

    private interface OnDownloadCompleteListener {
        void onDownloadComplete(int successCode);
    }
}
