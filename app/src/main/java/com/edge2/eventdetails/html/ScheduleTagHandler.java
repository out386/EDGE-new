package com.edge2.eventdetails.html;

/*
 * Taken from Chad Bingham's answer here: https://stackoverflow.com/a/28661338 and adapted
 */

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.xml.sax.XMLReader;

/**
 * Tags processed by this TagHandler:
 * <code><d></code>: Dates
 * <code><t></code>: Times
 * <code><e></code>: Event
 */
public class ScheduleTagHandler implements Html.TagHandler {
    private int dateColour;
    private int timeColour;

    public ScheduleTagHandler(Context context, int dateColourRes, int timeColourRes) {
        dateColour = context.getColor(dateColourRes);
        timeColour = context.getColor(timeColourRes);
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        switch (tag) {
            case "d":
                if (opening) {
                    start(output, new ColourTag());
                    start(output, new BoldTag());
                } else {
                    end(output, ColourTag.class, new ForegroundColorSpan(dateColour), 1);
                    end(output, BoldTag.class, new StyleSpan(Typeface.BOLD), -1);
                }
                break;
            case "t":
                if (opening) {
                    start(output, new ColourTag());
                } else {
                    end(output, ColourTag.class, new ForegroundColorSpan(timeColour), 0);
                }
                break;
            case "e":
                if (opening) {
                    start(output, new ItalicsTag());
                } else {
                    end(output, ItalicsTag.class, new StyleSpan(Typeface.ITALIC), -1);
                }
                break;
        }
    }

    // This method was copied from android.text.Html
    private <T> T getLast(Spanned text, Class<T> kind) {
        /*
         * This knows that the last returned object from getSpans()
         * will be the most recently added.
         */
        T[] objs = text.getSpans(0, text.length(), kind);

        if (objs.length == 0) {
            return null;
        } else {
            return objs[objs.length - 1];
        }
    }

    // This method was copied from android.text.Html
    private void start(Editable text, Object mark) {
        int len = text.length();
        text.setSpan(mark, len, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    // This method was copied from android.text.Html
    private void end(Editable text, Class kind, Object repl, int type) {
        Object obj = getLast(text, kind);
        if (obj != null) {
            int where = text.getSpanStart(obj);
            text.removeSpan(obj);
            int len = text.length();

            // time
            if (type == 0) {
                String insertString = "\n\t\t— ";
                text.insert(where, insertString);
                len += insertString.length();
                where += insertString.length() - 2;
                text.insert(len, ": ");
                len += 1;
            } else if (type == 1) { // Date
                if (where > 0) {
                    text.insert(where++, "\n");
                    len++;
                }
            }

            if (where != len) {
                text.setSpan(repl, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static class ColourTag {
    }

    private static class ItalicsTag {
    }

    private static class BoldTag {
    }
}