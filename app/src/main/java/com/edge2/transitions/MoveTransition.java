package com.edge2.transitions;

import android.widget.TextView;

import androidx.transition.ChangeBounds;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionSet;

public class MoveTransition extends TransitionSet {
    public MoveTransition(TextView v1) {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform());
        if (v1 != null)
            addTransition(new TextResize().addTarget(v1));
    }
}
