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
import androidx.core.widget.NestedScrollView;
import androidx.transition.Transition;

import com.edge2.BaseFragment;
import com.edge2.OnFragmentScrollListener;
import com.edge2.R;
import com.edge2.allevents.EventsFragment;
import com.edge2.html.RulesTagHandler;
import com.edge2.html.ScheduleTagHandler;
import com.edge2.transitions.MoveTransition;
import com.edge2.views.ContactsView;

public class EventDetailsFragment extends BaseFragment {
    public static final String KEY_EVENT_IMAGE = "eventImage";
    public static final String KEY_EVENT_NAME = "eventName";
    public static final String KEY_EVENT_DESC = "eventDesc";

    private OnFragmentScrollListener listener;
    private Context context;
    private OnSharedElementListener sharedElementListener;
    private Transition transition;
    private TextView nameTv;
    private boolean isIntra;

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
        Bundle args = getArguments();
        if (args != null)
            isIntra = args.getBoolean(EventsFragment.KEY_IS_INTRA);
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

        prototype(longDesc, rules, contacts, schedule);

        if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
        }
        sharedElementListener = new OnSharedElementListener(view, dummy, divider, schedule, longDesc,
                rules, contacts);
        transition.addListener(sharedElementListener);

        setupInsets(view, divider, topView, scrollView, contentView);
        setData(shortDescTv, image);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sharedElementListener != null)
            transition.removeListener(sharedElementListener);
    }

    private void setData(TextView shortDescTv, ImageView image) {
        Bundle args = getArguments();
        if (args != null) {
            nameTv.setText(args.getString(KEY_EVENT_NAME));
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
        String sched = "<d>01/20/20</d><t>10:00pm</t><e>some round 1</e><t>12:00pm</t><e>some round 2</e>" +
                "<d>02/20/20</d><t>10:00pm</t><e>some round 3</e>" +
                "<d>03/20/20</d><t>10:00pm - 11:00pm</t><e>some round 4</e>";
        schedule.setText(processSched(sched));
        longDesc.setText("Can you solve the cipher above and make a meaningful sentence out of it? Well if you can then you are made for this event. In this event you need to be calm and gather all your wits to make meaning of most of the questions. Get ready for a cryptic experience!");
        String rule = "<ul><li>Any team can participate in Blitzkrieg consisting of a minimum of 4 participants. These participants can be from same or different institutes.</li><li>Every team must have a name which must be unique.Geekonix reserves the right to reject entries from any Team whose name it deems inappropriate, offensive or conflicting. Organizers must be notified if a team\'s name has been changed.</li></ul><br><he3>Specifications</he3><ul><li>All weapons must have a safety cover on any sharp edges.</li><li>All participants have to build and operate robots at their own risk. Combat robotics is inherently dangerous. There is no amount of regulation that can encompass all the dangers involved. Please take care to not hurt yourself or others when building, testing or competing</li><li>Any kind of activity (repairing, battery handling, pneumatics systems etc.) which may cause damage to the surroundings during the stay of the teams in the competition area should not be carried out without the consent of organizers. Not following this rule may result in disqualification.</li><li>All the resources provided at the time of competition from the organizers.</li><li>Once the robots have entered into the arena, no team member can enter into the arena at any point of time. In case if a fight has to be halted in between and some changes have to be done in the arena or condition on the robot(s), it will be done by organizers only.</li><li>Dimensons and Fabrications : The machine should fit in a box of dimensions 750mm x 750mm x 750mm (l x b x h) at any given point during the match.</li><li>The external device used to control the machine or any external tank is not included in the size constraint.</li><li>The machine should not exceed 50 kg + 10% Tolerance in weight including the weight of pneumatic source/tank. In case of wireless robot, if the battery is on-board then the weight should not exceed 60 kg.</li><li>Power Sources: The machine can be powered electrically only. Use of an IC engine in any form is not allowed.</li><li>Each team must prepare its own power sources. Only 220V volt AC sources will be provided at the arena, but can only be used in the form of DC voltage. The teams have to bring their own battery eliminators.</li><li>The voltage difference between any two points in the machine should not be more than 36V DC at any point of time.</li><li>All connections should be made safe to prevent short circuits and battery fires. Any unsafe circuitry may be asked to be replaced; failure to do so will result in disqualification.</li><li>Use of damaged, non-leak proof batteries may lead to disqualification.</li><li>Change of battery will not be allowed during the match.</li><li>It is suggested to have extra batteries ready and charged up during competition so that on advancing to next level, you don\'t have to wait or suffer due to uncharged battery.</li><li>If a team do not show up on allotted slot, they will be disqualified.</li><li>Mobility : All bots must have clearly visible and controlled mobility mechanism in order to compete.</li><li>Methods of mobility may include: Rolling (wheels, tracks or the whole robot), Walking (linear actuated legs with no rolling or cam operated motion) & Shuffling (rotational cam operated legs).</li><li>Jumping and hopping is not allowed</li><li>Flying (using aerofoil, helium balloons, ornithopters, etc.) is not allowed.</li><li>Any other method of mobility which leads the robot to lose contact with the ground is not allowed.</li><li>Robot control requirements: Both wired and wireless remote controls are allowed in the event.</li><li>All wires coming out of the robot should be bundled as a single unit</li><li>The wires should be properly protected and insulated.</li><li>The wire should be sufficiently long so as to remain slack at all time during the competition.</li><li>In case of wireless remote controls, the remote should have atleast two frequency operations to prevent interference with other team.</li><li>Teams are recommended to attach a pipe to bot in vertical direction through which wires come out. The length of pipe will not be considered in bot dimension.</li><li>Weapon Systems: Robots can have any kind of cutters, flippers, saws, lifting devices,spinning hammers etc. as weapons with following exceptions.</li><ul><li>Any kind of inflammable liquid / liquid based weapons.</li><li>Smoke or dust weapons.</li><li>Flame-based Weapons.</li><li>Any kind of explosive or interionally ignited solid or potentially ignitable solid.</li><li>Nets, tape, glue, or any other entanglement device.</li><li>High power magnets or electromagnets.</li><li>Radio Jamming, Tasers, Tesla coils, or any other high-voltage device.</li><li>Tethered or un-tethered projectiles.</li></ul><li>Spinning weapons which do not come in contact with the arena at any point of time are allowed.</li><li>The robot must use non-inflammable and non-corrosive fluids to power pneumatic and hydraulic devices.</li><li>Maximum pressure in the tank containing pneumatic fluid should not exceed the limit of 10 bars and there should be a provision to check the pressure in the tank.</li><li>All hydraulic liquids are required to be non-corrosive and your device should be leak proof. The maximum pressure in cylinder should not exceed the rated pressure at any point of time.</li><li>Participants must be able to indicate the used pressure with integrated or temporarily fitted pressure gauge.</li><li>You must have a safe way of refilling the system</li><li>All pneumatic components on board a robot must be securely mounted. Care must be taken while mounting the pressure vessel and armour to ensure that if ruptured it will not escape the robot.</li></ul><br><he3>Scoring Rules</he3><br><ul><li>Points will be awarded, once in a round, if the bot reaches opponent's starting point, which can be referred to as safe zone.</li><li>Points of the opponent team will be deducted if their bot is pushed into the danger zone (Remaining corners of the arena apart from safe zone).</li><li>Points will be awarded for pushing the opponent's bot out of the arena.</li><li>Specific scoring will be updated soon.</li></ul>";
        rules.setText(processRule(rule));
        setContacts(contacts);
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