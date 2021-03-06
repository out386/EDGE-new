package com.edge2.genericevents;

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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.edge2.BaseFragment;
import com.edge2.OnFragmentScrollListener;
import com.edge2.R;
import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.html.RulesTagHandler;
import com.edge2.html.ScheduleTagHandler;
import com.edge2.views.ContactsView;
import com.edge2.views.people.PeopleView;

import java.util.List;

import jp.wasabeef.blurry.Blurry;
import pl.droidsonroids.gif.GifDrawable;

import static android.view.View.GONE;

public class GenericEventFragment extends BaseFragment {
    public static final String KEY_BANNER_ITEM = "gBannerItem";

    private OnFragmentScrollListener listener;
    private Context context;
    private TextView nameTv;
    private LinearLayout additionalImgView;
    private LinearLayout contactsView;
    private LinearLayout prevGuestsView;
    private LinearLayout intendedGuestsView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentScrollListener) context;
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        context = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postponeEnterTransition();
        View rootView = inflater.inflate(R.layout.fragment_generic_event, container, false);
        nameTv = rootView.findViewById(R.id.genericevent_name);
        additionalImgView = rootView.findViewById(R.id.genericevent_additional_imgs);
        contactsView = rootView.findViewById(R.id.genericevent_contacts);
        prevGuestsView = rootView.findViewById(R.id.genericevent_pguest);
        intendedGuestsView = rootView.findViewById(R.id.genericevent_iguest);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        nameTv = null;
        contactsView.removeAllViews();
        contactsView = null;
        additionalImgView.removeAllViews();
        additionalImgView = null;
        prevGuestsView.removeAllViews();
        prevGuestsView = null;
        intendedGuestsView.removeAllViews();
        intendedGuestsView = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View divider = view.findViewById(R.id.divider);
        View topView = view.findViewById(R.id.genericevent_top);
        View contentView = view.findViewById(R.id.genericevent_content);
        NestedScrollView scrollView = view.findViewById(R.id.scroll_view);
        ImageView image = view.findViewById(R.id.genericevent_image);
        ImageView imageBlur = view.findViewById(R.id.genericevent_image_blur);
        TextView longDesc = view.findViewById(R.id.genericevent_long_desc);
        TextView schedule = view.findViewById(R.id.genericevent_schedule);
        TextView additionalHeader = view.findViewById(R.id.genericevent_additional_imgs_header);

        setupInsets(view, divider, topView, scrollView, contentView);
        BannerItemsModel model = setData(image, imageBlur, longDesc, schedule, additionalHeader);

        if (model != null) {
            String addImgs = model.getAdditionalImages();
            boolean additionalHasHeader = false;
            if (addImgs != null && !addImgs.isEmpty()) {
                // If getAdditionalImages() starts with a ",", it means that the header field is empty
                // Yeah yeah, I know CSV sucks for this. Code is getting more and more brittle now.
                additionalHasHeader = addImgs.indexOf(",") != 0;
            }
            new AnimationHolder(view, divider, schedule, longDesc, model, additionalHasHeader,
                    additionalHeader)
                    .animateViews();
        }
    }

    private BannerItemsModel setData(ImageView image, ImageView imageBlur, TextView longDesc,
                                     TextView sched, TextView additionalHeader) {
        Bundle args = getArguments();
        if (args == null) {
            NavHostFragment.findNavController(this).popBackStack();
            return null;
        }
        Parcelable parcelable = args.getParcelable(KEY_BANNER_ITEM);
        if (!(parcelable instanceof BannerItemsModel)) {
            NavHostFragment.findNavController(this).popBackStack();
            return null; // Panic. RUN!
        }
        BannerItemsModel item = (BannerItemsModel) parcelable;
        if (item.getName().isEmpty()) {
            NavHostFragment.findNavController(this).popBackStack();
            return null;
        }
        nameTv.setText(item.getName());
        if (item.getImageUri() != null) {
            Glide.with(context)
                    .load(item.getImageUri())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .listener(new BlurImageListener(imageBlur))
                    .into(image);
        } else {
            Bitmap bitmap = drawableToBitmap(
                    getResources().getDrawable(R.drawable.generic_banner, null));
            image.setImageResource(R.drawable.generic_banner);
            Blurry.with(context)
                    .color(getResources().getColor(R.color.windowBackgroundTransparent, null))
                    .from(bitmap)
                    .into(imageBlur);
        }
        String schedule = item.getSched();
        if (schedule != null && !schedule.isEmpty())
            sched.setText(processSched(schedule));
        longDesc.setText(processDesc(item.getDesc()));
        setContacts(item.getContacts());

        String addImgs = item.getAdditionalImages();
        if (addImgs != null && !addImgs.isEmpty())
            setAdditionalImgs(additionalHeader, addImgs);

        String pGuest = item.getPrevGuests();
        String iGuest = item.getIntendedGuests();
        if (pGuest != null && !pGuest.isEmpty())
            setPrevGuests(pGuest);
        if (iGuest != null && !iGuest.isEmpty())
            setIntendedGuests(iGuest);
        return item;
    }

    // By André: https://stackoverflow.com/a/10600736
    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // Single color bitmap will be created of 1x1 pixel
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void setupInsets(View v, View divider, View topView, NestedScrollView scrollView,
                             View content) {
        // Hide the toolbar
        listener.onListScrolled(0, Integer.MAX_VALUE);
        setupWindowInsets(v, content, topView, true, false,
                (l, t, r, b) -> {
                    startPostponedEnterTransition();
                    int dividerHeight = ((RelativeLayout.LayoutParams)
                            divider.getLayoutParams()).topMargin + divider.getHeight();
                    topView.post(() ->
                            setupScrollListener(scrollView,
                                    topView.getHeight() + dividerHeight));
                });
    }

    private void setContacts(List<Pair<String, Long>> contacts) {
        // Why bother with List/RecyclerViews for 2-4 items?
        for (int i = 0; i < contacts.size(); i++) {
            Pair<String, Long> contact = contacts.get(i);
            if (contact.second == null)
                continue;
            ContactsView c = new ContactsView(context, contact.first, contact.second,
                    i != contacts.size() - 1);
            contactsView.addView(c);
        }
    }

    /**
     * Expected format of additionalImgs: <name (optional 0-length)>,<imgUrl1>,<imgUrl2>,...
     */
    private void setAdditionalImgs(TextView header, String additionalImgs) {
        String[] imgs = additionalImgs.split(",");
        if (!imgs[0].isEmpty())
            header.setText(imgs[0]);
        else
            header.setVisibility(GONE);

        GifDrawable placeholder = GifDrawable.createFromResource(getResources(), R.drawable.loading);
        for (int i = 1; i < imgs.length; i++) {
            String imgUrl = String.format(BannerItemsModel.ADDITIONAL_URL_TEMPLATE, imgs[i]);
            ImageView imageView = new AdditionalImageView(context);
            additionalImgView.addView(imageView);
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(imageView);
        }
    }

    /**
     * Expected format of prefGuests: <name1>,<imgUrl1>,<name2>,<imgUrl2>
     */
    private void setPrevGuests(String prevGuests) {
        String[] guests = prevGuests.split(",");
        for (int i = 0; i < guests.length; i += 2) {
            String imgUrl = String.format(BannerItemsModel.GUEST_URL_TEMPLATE, guests[i + 1]);
            PeopleView peopleView = new PeopleView(context, guests[i], imgUrl);
            prevGuestsView.addView(peopleView);
        }
    }

    /**
     * Expected format of intendedGuests: <name1>,<imgUrl1>,<name2>,<imgUrl2>
     */
    private void setIntendedGuests(String intendedGuests) {
        String[] guests = intendedGuests.split(",");
        for (int i = 0; i < guests.length; i += 2) {
            String imgUrl = String.format(BannerItemsModel.GUEST_URL_TEMPLATE, guests[i + 1]);
            PeopleView peopleView = new PeopleView(context, guests[i], imgUrl);
            intendedGuestsView.addView(peopleView);
        }
    }

    private Spanned processDesc(String rule) {
        Html.TagHandler tagHandler = new RulesTagHandler(requireContext(), R.style.TextHeaderMid);
        return HtmlCompat.fromHtml(rule, HtmlCompat.FROM_HTML_MODE_COMPACT, null, tagHandler);
    }

    private Spanned processSched(String schedule) {
        // Needed as TagHandlers break if the first characters are a tag to be handled
        schedule = "<dummy>" + schedule;
        Html.TagHandler tagHandler = new ScheduleTagHandler(
                requireContext(), R.color.eventDetailsSchedDate, R.color.eventDetailsSchedTime);
        return HtmlCompat.fromHtml(schedule, HtmlCompat.FROM_HTML_MODE_COMPACT, null,
                tagHandler);
    }

    private class AnimationHolder {
        private View[] allViews;
        private Animation[] anims;
        private BannerItemsModel item;
        private boolean shouldHideAdditionalHeader;
        private View addImgHeader;

        AnimationHolder(View root, View divider, TextView schedule, TextView longDesc,
                        BannerItemsModel item, boolean shouldHideAdditionalHeader,
                        View addImgHeader) {
            this.item = item;
            this.shouldHideAdditionalHeader = shouldHideAdditionalHeader;
            this.addImgHeader = addImgHeader;
            int animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            View divider1 = root.findViewById(R.id.divider1);
            View divider2 = root.findViewById(R.id.divider2);
            View divider3 = root.findViewById(R.id.divider3);
            View divider4 = root.findViewById(R.id.divider4);
            View divider5 = root.findViewById(R.id.divider5);
            View overviewHeader = root.findViewById(R.id.genericevent_overview_header);
            View scheduleHeader = root.findViewById(R.id.genericevent_schedule_header);
            View contactsHeader = root.findViewById(R.id.genericevent_contacts_header);
            View pguestsHeader = root.findViewById(R.id.genericevent_pguest_header);
            View iguestsHeader = root.findViewById(R.id.genericevent_iguest_header);
            allViews = new View[]{
                    divider, overviewHeader, longDesc,
                    divider1, scheduleHeader, schedule,
                    divider2, pguestsHeader, prevGuestsView,
                    divider3, iguestsHeader, intendedGuestsView,
                    divider4, addImgHeader, additionalImgView,
                    divider5, contactsHeader, contactsView};

            anims = new Animation[allViews.length];
            for (int i = 0; i < anims.length; i++) {
                anims[i] = AnimationUtils.loadAnimation(divider.getContext(),
                        R.anim.view_fall_down);
                anims[i].setStartOffset((i + 1) * 100);
                anims[i].setDuration(animTime);
            }
        }

        void animateViews() {
            for (int i = 0; i < allViews.length; i++) {
                int group = i / 3;
                if ((group != 1 || !(item.getSched() == null || item.getSched().isEmpty()))
                        && (group != 2 || !(item.getPrevGuests() == null || item.getPrevGuests().isEmpty()))
                        && (group != 3 || !(item.getIntendedGuests() == null || item.getIntendedGuests().isEmpty()))
                        && (group != 4 || !(item.getAdditionalImages() == null || item.additionalImages.isEmpty())
                        || i == 13 && !shouldHideAdditionalHeader)
                        && (group != 5 || !(item.getContacts() == null || item.getContacts().size() == 0)))
                    allViews[i].setVisibility(View.VISIBLE);
                else
                    allViews[i].setVisibility(GONE);
                allViews[i].startAnimation(anims[group]);
            }
        }
    }

    class BlurImageListener implements RequestListener<Drawable> {
        private ImageView imageBlur;

        BlurImageListener(ImageView imageBlur) {
            this.imageBlur = imageBlur;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                       DataSource dataSource, boolean isFirstResource) {

            int filter = getResources()
                    .getColor(R.color.windowBackgroundTransparent, null);
            Blurry.with(context)
                    .color(filter)
                    .from(drawableToBitmap(resource))
                    .into(imageBlur);
            return false;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                    Target<Drawable> target, boolean isFirstResource) {
            return false;
        }
    }
}