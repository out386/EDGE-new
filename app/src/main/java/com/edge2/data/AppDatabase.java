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

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.edge2.allevents.models.GroupsModel;

@Database(entities = {GroupsModel.class}, version = 1)
abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;

    static AppDatabase getDatabase(Context context) {
        if (appDatabase == null)
            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "events.db")
                    .createFromAsset("db/events.db")
                    .fallbackToDestructiveMigration()
                    .build();
        return appDatabase;
    }

    abstract RunningOutOfNamesDao getDao();
}
