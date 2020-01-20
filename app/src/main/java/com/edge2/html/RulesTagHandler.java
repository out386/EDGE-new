package com.edge2.html;

/*
 * Taken from Chad Bingham's answer here: https://stackoverflow.com/a/28661338 and adapted
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.res.ResourcesCompat;

import org.xml.sax.XMLReader;

public class RulesTagHandler implements Html.TagHandler {
    private int textSize;
    private int textColour;
    private Typeface typeface;

    public RulesTagHandler(Context context, int styleRes) {
        setAttrs(context, styleRes);
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.equals("he3")) {
            if (opening) {
                start(output, new SizeTag());
                start(output, new ColourTag());
                start(output, new FontTag());
            } else {
                end(output, ColourTag.class, new ForegroundColorSpan(textColour));
                end(output, SizeTag.class, new AbsoluteSizeSpan(textSize));
                end(output, FontTag.class, new CustomTypefaceSpan(typeface));
            }
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
    private void end(Editable text, Class kind, Object repl) {
        Object obj = getLast(text, kind);
        if (obj != null) {
            int where = text.getSpanStart(obj);
            text.removeSpan(obj);
            int len = text.length();
            if (where != len) {
                text.setSpan(repl, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    @SuppressWarnings("ResourceType")
    private void setAttrs(Context context, int styleRes) {
        int[] attrs = {android.R.attr.textSize, android.R.attr.textColor, android.R.attr.fontFamily};
        TypedArray ta = context.obtainStyledAttributes(styleRes, attrs);
        textSize = ta.getDimensionPixelSize(0, 35);
        textColour = ta.getColor(1, 0xFF555555);
        int fontId = ta.getResourceId(2, -1);
        typeface = ResourcesCompat.getFont(context, fontId);
        ta.recycle();
    }

    private static class SizeTag {
    }

    private static class ColourTag {
    }

    private static class FontTag {
    }
}