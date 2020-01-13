package com.edge2.event;

import androidx.transition.ChangeBounds;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionSet;

import androidx.transition.ChangeImageTransform;

public class EventTransition extends TransitionSet {
    public EventTransition() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform())
                .addTransition(new ChangeImageTransform());
    }
}
