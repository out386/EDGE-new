package com.edge2.data;

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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.allevents.models.GroupsModel;
import com.edge2.event.EventCategoryModel;
import com.edge2.eventdetails.models.EventDetailsModel;

@Database(entities = {GroupsModel.class, EventCategoryModel.class, EventDetailsModel.class,
        BannerItemsModel.class}, exportSchema = false, version = AppDatabase.DB_VERSION)
abstract class AppDatabase extends RoomDatabase {
    /**
     * Make sure that the online DB version and this always stays in sync.
     */
    static final int DB_VERSION = 18;
    private static final String KEY_DB_VERSION = "offlineDbVersion";

    private static AppDatabase appDatabase;

    static synchronized AppDatabase getDatabase(Context context) {
        if (appDatabase == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int dbVersion = prefs.getInt(KEY_DB_VERSION, 0);
            boolean createFromAsset = dbVersion < DB_VERSION;

            RoomDatabase.Builder<AppDatabase> builder =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                            "events.db");

            if (createFromAsset) {
                builder.createFromAsset("db/events.db");
                prefs.edit()
                        .putInt(KEY_DB_VERSION, DB_VERSION)
                        .apply();
            }

            appDatabase = builder.fallbackToDestructiveMigration().build();
        }
        return appDatabase;
    }

    abstract RunningOutOfNamesDao getDao();
}
