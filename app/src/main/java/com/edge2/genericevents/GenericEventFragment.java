package com.edge2.genericevents;

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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import androidx.core.widget.NestedScrollView;
import androidx.navigation.fragment.NavHostFragment;
import androidx.transition.Transition;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.edge2.BaseFragment;
import com.edge2.OnFragmentScrollListener;
import com.edge2.R;
import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.html.RulesTagHandler;
import com.edge2.html.ScheduleTagHandler;
import com.edge2.views.ContactsView;

import jp.wasabeef.blurry.Blurry;

public class GenericEventFragment extends BaseFragment {
    public static final String KEY_EVENT_MODEL = "gEventModel";

    private OnFragmentScrollListener listener;
    private Context context;
    private Transition transition;
    private TextView nameTv;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentScrollListener) context;
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postponeEnterTransition();
        View rootView = inflater.inflate(R.layout.fragment_generic_event, container, false);
        nameTv = rootView.findViewById(R.id.genericevent_name);
        return rootView;
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
        LinearLayout contacts = view.findViewById(R.id.genericevent_contacts);

        setupInsets(view, divider, topView, scrollView, contentView);
        setData(image, imageBlur, longDesc, schedule, contacts);

        new AnimationHolder(view, divider, schedule, longDesc,
                contacts).animateViews();
    }

    private void setData(ImageView image, ImageView imageBlur, TextView longDesc, TextView sched,
                         LinearLayout contacts) {
        Bundle args = getArguments();
        if (args != null) {
            BannerItemsModel item = (BannerItemsModel) args.getSerializable(KEY_EVENT_MODEL);
            if (item == null) {
                NavHostFragment.findNavController(this).popBackStack();
                return;
            }
            nameTv.setText(item.getName());
            Glide.with(context)
                    .load(item.getImg())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model,
                                                   Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource,
                                                       String model, Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {

                            int filter = getResources()
                                    .getColor(R.color.windowBackgroundTransparent, null);
                            Blurry.with(context)
                                    .color(filter)
                                    .from(drawableToBitmap(resource))
                                    .into(imageBlur);
                            return false;
                        }
                    })
                    .into(image);
            sched.setText(processSched(item.getSched()));
            longDesc.setText(processDesc(item.getDesc()));
            setContacts(contacts);
        }
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

    private void setContacts(LinearLayout contacts) {
        // Why bother with List/RecyclerViews for 2-4 items?
        ContactsView c1 = new ContactsView(context, "Some Name", 9123456789L, true);
        ContactsView c2 = new ContactsView(context, "Some Name", 8901234567L, true);
        ContactsView c3 = new ContactsView(context, "Yet Another Name", 9234567890L, false);
        contacts.addView(c1);
        contacts.addView(c2);
        contacts.addView(c3);
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

        AnimationHolder(View root, View divider, TextView schedule,
                        TextView longDesc, View contacts) {
            int animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            View divider1 = root.findViewById(R.id.divider1);
            View divider2 = root.findViewById(R.id.divider2);
            View overviewHeader = root.findViewById(R.id.genericevent_overview_header);
            View scheduleHeader = root.findViewById(R.id.genericevent_schedule_header);
            View contactsHeader = root.findViewById(R.id.genericevent_contacts_header);
            allViews = new View[]{divider, overviewHeader, longDesc, divider1, scheduleHeader,
                    schedule, divider2, contactsHeader, contacts};

            anims = new Animation[3];
            for (int i = 0; i < 3; i++) {
                anims[i] = AnimationUtils.loadAnimation(divider.getContext(),
                        R.anim.view_fall_down);
                anims[i].setStartOffset((i + 1) * 100);
                anims[i].setDuration(animTime);
            }
        }

        void animateViews() {
            for (int i = 0; i < allViews.length; i++) {
                allViews[i].setVisibility(View.VISIBLE);
                allViews[i].startAnimation(anims[i / 3]);
            }
        }
    }
}