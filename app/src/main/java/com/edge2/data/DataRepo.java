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

import androidx.lifecycle.MutableLiveData;

import com.edge2.allevents.models.BannerItemsModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

class DataRepo {
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

}
