package com.edge2.eventdetails;

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
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.transition.Transition;

import com.edge2.BaseFragment;
import com.edge2.OnFragmentScrollListener;
import com.edge2.R;
import com.edge2.data.DataViewModel;
import com.edge2.html.RulesTagHandler;
import com.edge2.html.ScheduleTagHandler;
import com.edge2.transitions.MoveTransition;
import com.edge2.views.ContactsView;

import java.util.List;

public class EventDetailsFragment extends BaseFragment {
    public static final String KEY_EVENT_IMAGE = "eventImage";
    public static final String KEY_EVENT_NAME = "eventName";
    public static final String KEY_EVENT_DESC = "eventDesc";

    private OnFragmentScrollListener listener;
    private Context context;
    private OnSharedElementListener sharedElementListener;
    private Transition transition;
    private TextView nameTv;
    private String eventName;

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
        View rootView = inflater.inflate(R.layout.fragment_event_details, container, false);
        nameTv = rootView.findViewById(R.id.eventdetails_name);

        transition = new MoveTransition(nameTv);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View divider = view.findViewById(R.id.divider);
        View topView = view.findViewById(R.id.eventdetails_top);
        View contentView = view.findViewById(R.id.eventdetails_content);
        NestedScrollView scrollView = view.findViewById(R.id.scroll_view);
        ImageView image = view.findViewById(R.id.eventdetails_icon);
        View dummy = view.findViewById(R.id.eventdetails_dummy_bg);
        TextView shortDescTv = view.findViewById(R.id.eventdetails_short_desc);
        TextView longDesc = view.findViewById(R.id.eventdetails_long_desc);
        TextView schedule = view.findViewById(R.id.eventdetails_schedule);
        TextView rules = view.findViewById(R.id.eventdetails_rules);
        LinearLayout contacts = view.findViewById(R.id.eventdetails_contacts);

        if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
        }
        sharedElementListener = new OnSharedElementListener(view, dummy, divider, schedule, longDesc,
                rules, contacts);
        transition.addListener(sharedElementListener);

        setupInsets(view, divider, topView, scrollView, contentView);
        setHeaderData(shortDescTv, image);

        prototype(longDesc, rules, contacts, schedule);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sharedElementListener != null)
            transition.removeListener(sharedElementListener);
    }

    private void setHeaderData(TextView shortDescTv, ImageView image) {
        Bundle args = getArguments();
        if (args != null) {
            eventName = args.getString(KEY_EVENT_NAME);
            nameTv.setText(eventName);
            shortDescTv.setText(args.getString(KEY_EVENT_DESC));
            image.setImageResource(args.getInt(KEY_EVENT_IMAGE));
        }
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

    private void prototype(TextView longDesc, TextView rules, LinearLayout contacts, TextView schedule) {
        DataViewModel viewModel = ViewModelProviders.of(this)
                .get(DataViewModel.class);
        viewModel.getDetails(eventName).observe(this, item -> {
            schedule.setText(processSched(item.getSchedule()));
            longDesc.setText(item.getLongDesc());
            rules.setText(processRule(item.getRules()));
            setContacts(contacts, item.getContacts());
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void setContacts(LinearLayout contactsHolder, List<Pair<String, Long>> contacts) {
        // Why bother with List/RecyclerViews for 2-4 items?
        for (int i = 0; i < contacts.size(); i++) {
            Pair<String, Long> contact = contacts.get(i);
            ContactsView c = new ContactsView(context, contact.first, contact.second,
                    i < contacts.size() - 1);
            contactsHolder.addView(c);
        }
    }

    private Spanned processRule(String rule) {
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

    private class OnSharedElementListener implements Transition.TransitionListener {
        private View dummy;
        private View[] allViews;
        private Animation[] anims;

        OnSharedElementListener(View root, View dummy, View divider, TextView schedule, TextView longDesc,
                                TextView rules, View contacts) {
            int animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            this.dummy = dummy;
            View divider1 = root.findViewById(R.id.divider1);
            View divider2 = root.findViewById(R.id.divider2);
            View divider3 = root.findViewById(R.id.divider3);
            View overviewHeader = root.findViewById(R.id.eventdetails_overview_header);
            View scheduleHeader = root.findViewById(R.id.eventdetails_schedule_header);
            View rulesHeader = root.findViewById(R.id.eventdetails_rules_header);
            View contactsHeader = root.findViewById(R.id.eventdetails_contacts_header);
            allViews = new View[]{divider, overviewHeader, longDesc, divider1, scheduleHeader,
                    schedule, divider2, rulesHeader, rules, divider3, contactsHeader, contacts};

            anims = new Animation[4];
            for (int i = 0; i < 4; i++) {
                anims[i] = AnimationUtils.loadAnimation(divider.getContext(),
                        R.anim.view_fall_down);
                anims[i].setStartOffset(i * 100);
                anims[i].setDuration(animTime);
            }
        }

        @Override
        public void onTransitionStart(@NonNull Transition transition) {
            for (View view : allViews)
                view.setVisibility(View.GONE);
        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {
            dummy.setVisibility(View.GONE);

            for (int i = 0; i < allViews.length; i++) {
                allViews[i].setVisibility(View.VISIBLE);
                allViews[i].startAnimation(anims[i / 3]);
            }
        }

        @Override
        public void onTransitionCancel(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionPause(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionResume(@NonNull Transition transition) {

        }
    }
}