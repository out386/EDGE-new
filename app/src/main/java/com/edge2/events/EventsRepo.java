package com.edge2.events;

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

import com.edge2.utils.Logger;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

class EventsRepo {
    private String collection;
    private FirebaseFirestore db;
    private MutableLiveData<List<EventNameModel>> eventNamesData;

    EventsRepo(String collection) {
        this.collection = collection;
        db = FirebaseFirestore.getInstance();
    }

    MutableLiveData<List<EventNameModel>> loadBanner() {
        Logger.log("EventsRepo", "loadBanner");

        if (eventNamesData == null)
            eventNamesData = new MutableLiveData<>();

        db.collection(collection)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<EventNameModel> results = new ArrayList<>();
                    for (QueryDocumentSnapshot document : snapshot) {
                        EventNameModel model = new EventNameModel(document);
                        results.add(model);
                    }
                    eventNamesData.setValue(results);
                });

        return eventNamesData;
    }

}
