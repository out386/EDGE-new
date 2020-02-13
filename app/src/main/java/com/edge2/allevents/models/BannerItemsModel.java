package com.edge2.allevents.models;

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

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "BannerItems")
public class BannerItemsModel implements Parcelable {
    @PrimaryKey
    @NonNull
    public String name;
    public String imgName;
    public String imgUrl;
    @Ignore
    @Nullable
    private Uri imageUri;
    public String icName;
    public String icUrl;
    @Ignore
    @Nullable
    private Uri iconUri;
    public String sched;
    public String desc;
    /**
     * If false, this item doesn't appear in Upcoming, just in banner. If false and no image is
     * specified, this item will not appear anywhere at all.
     */
    @ColumnInfo(name = "isUp")
    public boolean isUpcoming;
    public String cN1;
    public long cNo1;
    public String cN2;
    public long cNo2;
    public String cN3;
    public long cNo3;
    public String cN4;
    public long cNo4;
    @Ignore
    private List<Pair<String, Long>> contacts;
    /**
     * Expected format: <name1>,<imgName1>,<name2>,<imgName2>,... Image names use the template
     * string {@link #GUEST_URL_TEMPLATE}
     */
    @ColumnInfo(name = "pG")
    public String prevGuests;
    /**
     * Expected format: <name1>,<imgName1>,<name2>,<imgName2>,... Image names use the template
     * string {@link #GUEST_URL_TEMPLATE}
     */
    @ColumnInfo(name = "iG")
    public String intendedGuests;

    /**
     * Expected format: <heading (optionally 0-length)>,<imgName1>,<imgName2>,... Image names use
     * the template string {@link #ADDITIONAL_URL_TEMPLATE}
     */
    @ColumnInfo(name = "addImg")
    public String additionalImages;

    @Ignore
    public static final String GUEST_URL_TEMPLATE =
            "https://firebasestorage.googleapis.com/v0/b/edge-new-a7306.appspot.com/o/banner_items%%2Fguests%%2F%s?alt=media";

    @Ignore
    public static final String ADDITIONAL_URL_TEMPLATE =
            "https://firebasestorage.googleapis.com/v0/b/edge-new-a7306.appspot.com/o/banner_items%%2Fadditional%%2F%s?alt=media";

    private BannerItemsModel() {
    }

    // TODO: Switch to NoSQL, like Realm
    /*
     * When I wrote this thing, I never expected so many fields, with so many of them needing to be
     * lists. At this point, working around the list limitation is getting riddiculous. Not to
     * mention that the JSON used for updates are getting big, because of unused fields.
     */
    public BannerItemsModel(@NonNull String name, String imgName, String imgUrl, String icName,
                            String icUrl, String sched, String desc, boolean isUpcoming, String cN1,
                            long cNo1, String cN2, long cNo2, String cN3, long cNo3, String cN4,
                            long cNo4, String prevGuests, String intendedGuests,
                            String additionalImages) {
        this.name = name;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
        this.icName = icName;
        this.icUrl = icUrl;
        setUri(this);
        this.sched = sched;
        this.desc = desc;
        this.isUpcoming = isUpcoming;
        this.cN1 = cN1;
        this.cNo1 = cNo1;
        this.cN2 = cN2;
        this.cNo2 = cNo2;
        this.cN3 = cN3;
        this.cNo3 = cNo3;
        this.cN4 = cN4;
        this.cNo4 = cNo4;
        setContacts(this);
        this.prevGuests = prevGuests;
        this.intendedGuests = intendedGuests;
        this.additionalImages = additionalImages;
    }

    protected BannerItemsModel(Parcel in) {
        //noinspection ConstantConditions
        name = in.readString();
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        iconUri = in.readParcelable(Uri.class.getClassLoader());
        sched = in.readString();
        desc = in.readString();
        isUpcoming = in.readByte() != 0;
        cN1 = in.readString();
        cNo1 = in.readLong();
        cN2 = in.readString();
        cNo2 = in.readLong();
        cN3 = in.readString();
        cNo3 = in.readLong();
        cN4 = in.readString();
        cNo4 = in.readLong();
        setContacts(this);
        prevGuests = in.readString();
        intendedGuests = in.readString();
        additionalImages = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(imageUri, flags);
        dest.writeParcelable(iconUri, flags);
        dest.writeString(sched);
        dest.writeString(desc);
        dest.writeByte((byte) (isUpcoming ? 1 : 0));
        dest.writeString(cN1);
        dest.writeLong(cNo1);
        dest.writeString(cN2);
        dest.writeLong(cNo2);
        dest.writeString(cN3);
        dest.writeLong(cNo3);
        dest.writeString(cN4);
        dest.writeLong(cNo4);
        dest.writeString(prevGuests);
        dest.writeString(intendedGuests);
        dest.writeString(additionalImages);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BannerItemsModel> CREATOR = new Creator<BannerItemsModel>() {
        @Override
        public BannerItemsModel createFromParcel(Parcel in) {
            return new BannerItemsModel(in);
        }

        @Override
        public BannerItemsModel[] newArray(int size) {
            return new BannerItemsModel[size];
        }
    };

    private static void setUri(BannerItemsModel item) {
        if (item.imgName != null && !item.imgName.isEmpty()) {
            item.imageUri = Uri.parse("android.resource://com.edge2/drawable/" + item.imgName);
        } else if (item.imgUrl != null && !item.imgUrl.isEmpty()) {
            item.imageUri = Uri.parse(item.imgUrl);
        }

        if (item.icName != null && !item.icName.isEmpty()) {
            item.iconUri = Uri.parse("android.resource://com.edge2/drawable/" + item.icName);
        } else if (item.icUrl != null && !item.icUrl.isEmpty()) {
            item.iconUri = Uri.parse(item.icUrl);
        }
    }

    private static void setContacts(BannerItemsModel item) {
        item.contacts = new ArrayList<>(4);
        if (item.cN1 != null && !item.cN1.isEmpty()) {
            item.contacts.add(new Pair<>(item.cN1, item.cNo1));
        }
        if (item.cN2 != null && !item.cN2.isEmpty()) {
            item.contacts.add(new Pair<>(item.cN2, item.cNo2));
        }
        if (item.cN3 != null && !item.cN3.isEmpty()) {
            item.contacts.add(new Pair<>(item.cN3, item.cNo3));
        }
        if (item.cN4 != null && !item.cN4.isEmpty()) {
            item.contacts.add(new Pair<>(item.cN4, item.cNo4));
        }
    }

    @Nullable
    public Uri getImageUri() {
        return imageUri;
    }

    @Nullable
    public Uri getIconUri() {
        return iconUri;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getSched() {
        return sched;
    }

    public String getDesc() {
        return desc;
    }

    public boolean getUpcoming() {
        return isUpcoming;
    }

    public List<Pair<String, Long>> getContacts() {
        return contacts;
    }

    //TODO: Make getPrevGuests, getIntendedGuests, and getAdditionalImages all return deserialized data
    public String getPrevGuests() {
        return prevGuests;
    }

    public String getIntendedGuests() {
        return intendedGuests;
    }

    public String getAdditionalImages() {
        return additionalImages;
    }

    // TODO: USE GSON!
    // Never expected this tiny bit of deserialization to become not so tiny.
    public static BannerItemsModel getFromJson(JSONObject ob) throws JSONException {
        BannerItemsModel item = new BannerItemsModel();
        item.name = ob.getString("name");

        // Yes, the generated JSON I'm using can actually have a "null" string
        if (item.name == null || item.name.isEmpty() || item.name.equals("null"))
            return null;

        item.imgName = ob.getString("imgName");
        if (item.imgName == null || item.imgName.isEmpty() || item.imgName.equals("null"))
            item.imgName = null;
        item.imgUrl = ob.getString("imgUrl");
        if (item.imgUrl == null || item.imgUrl.isEmpty() || item.imgUrl.equals("null"))
            item.imgUrl = null;

        item.icName = ob.getString("icName");
        if (item.icName == null || item.icName.isEmpty() || item.icName.equals("null"))
            item.icName = null;
        item.icUrl = ob.getString("icUrl");
        if (item.icUrl == null || item.icUrl.isEmpty() || item.icUrl.equals("null"))
            item.icUrl = null;

        item.sched = ob.getString("sched");
        if (item.sched == null || item.sched.isEmpty() || item.sched.equals("null"))
            item.sched = null;
        item.desc = ob.getString("desc");
        if (item.desc == null || item.desc.isEmpty() || item.desc.equals("null"))
            item.desc = null;

        try {
            item.isUpcoming = ob.getInt("isUp") == 1;
        } catch (JSONException e) {
            try {
                item.isUpcoming = Integer.parseInt(ob.getString("isUp")) == 1;
            } catch (NumberFormatException | JSONException ignored) {
            }
        }

        item.cN1 = ob.getString("cN1");
        if (item.cN1 == null || item.cN1.isEmpty() || item.cN1.equals("null"))
            item.cN1 = null;
        item.cN2 = ob.getString("cN2");
        if (item.cN2 == null || item.cN2.isEmpty() || item.cN2.equals("null"))
            item.cN2 = null;
        item.cN3 = ob.getString("cN3");
        if (item.cN3 == null || item.cN3.isEmpty() || item.cN3.equals("null"))
            item.cN3 = null;
        item.cN4 = ob.getString("cN4");
        if (item.cN4 == null || item.cN4.isEmpty() || item.cN4.equals("null"))
            item.cN4 = null;
        try {
            item.cNo1 = ob.getLong("cNo1");
            item.cNo2 = ob.getLong("cNo2");
            item.cNo3 = ob.getLong("cNo3");
            item.cNo4 = ob.getLong("cNo4");
        } catch (JSONException e) {
            try {
                item.cNo1 = Long.parseLong(ob.getString("cNo1"));
                item.cNo2 = Long.parseLong(ob.getString("cNo2"));
                item.cNo3 = Long.parseLong(ob.getString("cNo3"));
                item.cNo4 = Long.parseLong(ob.getString("cNo4"));
            } catch (NumberFormatException | JSONException ignored) {
            }
        }
        item.prevGuests = ob.getString("pG");
        if (item.prevGuests == null || item.prevGuests.isEmpty() || item.prevGuests.equals("null"))
            item.prevGuests = null;

        item.intendedGuests = ob.getString("iG");
        if (item.intendedGuests == null || item.intendedGuests.isEmpty() || item.intendedGuests.equals("null"))
            item.intendedGuests = null;

        item.additionalImages = ob.getString("addImg");
        if (item.additionalImages == null || item.additionalImages.isEmpty() || item.additionalImages.equals("null"))
            item.additionalImages = null;

        setUri(item);
        setContacts(item);
        return item;
    }

}
