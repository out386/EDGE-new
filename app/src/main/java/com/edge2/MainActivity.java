package com.edge2;

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

import android.os.Bundle;

import com.edge2.events.EventsFragment;
import com.edge2.utils.Logger;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private String currentFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            item -> {

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        if (currentFragment == null
                                || !currentFragment.equals(EventsFragment.TAG)) {
                            currentFragment = EventsFragment.TAG;
                            Fragment fragment = new EventsFragment();
                            switchFragment(fragment, EventsFragment.TAG, false);
                        }
                        break;
                }
                return true;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        currentFragment = EventsFragment.TAG;
        Fragment fragment = new EventsFragment();
        switchFragment(fragment, EventsFragment.TAG, false);
    }

    private void switchFragment(Fragment fragment, String tag, boolean addToBackstack) {
        Logger.log("MainActivity", "switchFragment: Switching to: " + tag);
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, fragment);

        if (addToBackstack)
            transaction.addToBackStack(tag);
        transaction.commit();

    }

}
