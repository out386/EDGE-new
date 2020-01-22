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
import androidx.room.Query;

import com.edge2.allevents.models.GroupsModel;
import com.edge2.event.EventCategoryModel;
import com.edge2.eventdetails.models.EventDetailsModel;

import java.util.List;

@Dao
public interface RunningOutOfNamesDao {
    @Query("SELECT * FROM Groups WHERE countEventsEdge > 0")
    LiveData<List<GroupsModel>> getGroupsEdge();

    @Query("SELECT * FROM Groups WHERE countEventsIntra > 0")
    LiveData<List<GroupsModel>> getGroupsIntra();

    @Query("SELECT * FROM EventCategories WHERE groupName = :groupName AND isInEdge = 1")
    LiveData<List<EventCategoryModel>> getCategoriesEdge(String groupName);

    @Query("SELECT * FROM EventCategories WHERE groupName = :groupName AND isInIntra = 1")
    LiveData<List<EventCategoryModel>> getCategoriesIntra(String groupName);

    @Query("SELECT * FROM EventDetails WHERE name = :name")
    LiveData<EventDetailsModel> getDetails(String name);
}
