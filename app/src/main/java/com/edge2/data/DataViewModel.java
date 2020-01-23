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

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.allevents.models.GroupsModel;
import com.edge2.event.EventCategoryModel;
import com.edge2.eventdetails.models.EventDetailsModel;

import java.util.List;

public class DataViewModel extends AndroidViewModel {
    private Context context;

    public DataViewModel(Application app) {
        super(app);
        context = app.getApplicationContext();
    }

    public LiveData<List<BannerItemsModel>> getBanner() {
        return DataRepo.getInstance().loadBanner();
    }

    public LiveData<List<GroupsModel>> getGroups(boolean isIntra) {
        RunningOutOfNamesDao dao = AppDatabase.getDatabase(context).getDao();
        if (isIntra)
            return dao.getGroupsIntra();
        else
            return dao.getGroupsEdge();
    }

    public LiveData<List<EventCategoryModel>> getCategories(boolean isIntra, String groupName) {
        RunningOutOfNamesDao dao = AppDatabase.getDatabase(context).getDao();
        if (isIntra)
            return dao.getCategoriesIntra(groupName);
        else
            return dao.getCategoriesEdge(groupName);
    }

    public LiveData<EventDetailsModel> getDetails(String name) {
        RunningOutOfNamesDao dao = AppDatabase.getDatabase(context).getDao();
        return dao.getDetails(name);
    }
}
