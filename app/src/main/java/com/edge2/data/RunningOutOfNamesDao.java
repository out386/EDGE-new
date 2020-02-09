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

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.allevents.models.GroupsModel;
import com.edge2.event.EventCategoryModel;
import com.edge2.eventdetails.models.EventDetailsModel;

import java.util.List;

@Dao
public abstract class RunningOutOfNamesDao {
    @Query("SELECT * FROM Groups WHERE countEventsEdge > 0")
    abstract LiveData<List<GroupsModel>> getGroupsEdge();

    @Query("SELECT * FROM Groups WHERE countEventsIntra > 0")
    abstract LiveData<List<GroupsModel>> getGroupsIntra();

    @Query("SELECT * FROM EventCategories WHERE groupName = :groupName AND isInEdge = 1")
    abstract LiveData<List<EventCategoryModel>> getCategoriesEdge(String groupName);

    @Query("SELECT * FROM EventCategories WHERE groupName = :groupName AND isInIntra = 1")
    abstract LiveData<List<EventCategoryModel>> getCategoriesIntra(String groupName);

    @Query("SELECT * FROM EventDetails WHERE name = :name")
    abstract LiveData<EventDetailsModel> getDetails(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void putDetails(List<EventDetailsModel> items);

    @Query("SELECT * FROM BannerItems")
    abstract LiveData<List<BannerItemsModel>> getBannerItems();

    @Query("SELECT * FROM BannerItems WHERE isMega = 1")
    abstract LiveData<List<BannerItemsModel>> getMegaEvents();

    @Query("SELECT * FROM BannerItems WHERE isMega = 0")
    abstract LiveData<List<BannerItemsModel>> getUpcomingEvents();

    @Transaction
    void putBanner(List<BannerItemsModel> items) {
        removeBanners();
        insertBanner(items);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertBanner(List<BannerItemsModel> items);

    @Query("DELETE FROM BannerItems")
    abstract void removeBanners();
}
