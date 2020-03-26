package com.edge2.results;

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

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edge2.BuildConfig;
import com.edge2.data.downloader.FileDownloader;
import com.edge2.data.downloader.FileDownloaderModel;


public class ResultsViewModel extends AndroidViewModel {
    private Application application;
    private MutableLiveData<FileDownloaderModel<ResultsModel>> subscreensData;
    private FileDownloader<ResultsModel> subscreensDownloader;
    private MutableLiveData<FileDownloaderModel<MainScreenModel>> mainScreenData;
    private FileDownloader<MainScreenModel> mainScreenDownloader;

    public ResultsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    LiveData<FileDownloaderModel<ResultsModel>> getSubscreens(String url) {
        if (subscreensData == null) {
            subscreensData = new MutableLiveData<>();
            subscreensDownloader = new FileDownloader<>(application, ResultsModel.class);
        }
        subscreensDownloader.download(subscreensData, url);
        return subscreensData;
    }

    LiveData<FileDownloaderModel<MainScreenModel>> getMainScreen() {
        if (mainScreenData == null) {
            mainScreenData = new MutableLiveData<>();
            mainScreenDownloader = new FileDownloader<>(application, MainScreenModel.class);
        }
        mainScreenDownloader.download(mainScreenData, BuildConfig.URL_RESULT_BASE + "results.json");
        return mainScreenData;
    }
}
