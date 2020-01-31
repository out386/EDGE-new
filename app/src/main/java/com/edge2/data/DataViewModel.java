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

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.allevents.models.GroupsModel;
import com.edge2.event.EventCategoryModel;
import com.edge2.eventdetails.models.EventDetailsModel;
import com.edge2.sponsors.SponsorsModel;

import java.util.List;

public class DataViewModel extends AndroidViewModel {
    private DataRepo dataRepo;

    public DataViewModel(Application app) {
        super(app);
        dataRepo = DataRepo.getInstance(app);
    }

    public LiveData<List<BannerItemsModel>> getBanner() {
        //return dataRepo.loadBanner();
        return dataRepo.getBannerItems();
    }

    public LiveData<List<BannerItemsModel>> getUpcoming() {
        //return dataRepo.loadBanner();
        return dataRepo.getUpcomingEvents();
    }

    public LiveData<List<SponsorsModel>> getSponsors() {
        return dataRepo.getSponsors();
    }

    public LiveData<List<GroupsModel>> getGroups(boolean isIntra) {
        return dataRepo.getGroups(isIntra);
    }

    public LiveData<List<EventCategoryModel>> getCategories(boolean isIntra, String groupName) {
        return dataRepo.getCategories(isIntra, groupName);
    }

    public LiveData<EventDetailsModel> getDetails(String name) {
        return dataRepo.getDetails(name);
    }
}
