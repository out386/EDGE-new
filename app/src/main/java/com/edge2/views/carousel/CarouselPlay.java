package com.edge2.views.carousel;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.edge2.R;
import com.edge2.events.EventNameModel;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CarouselPlay extends RelativeLayout {

    private RecyclerView recycler;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;
    private HeroModel heroModel;
    private RelativeLayout heroLayout;
    private ImageView heroImage;
    private ImageView heroBg;
    private View heroBgGradient;
    private TextView heroCaption;

    public CarouselPlay(Context context, HeroModel heroModel) {
        super(context);
        this.heroModel = heroModel;
        setupViews(context);
    }

    private void setupViews(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.carousel_play, this, true);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        recycler = findViewById(R.id.carousel_recycler);
        heroLayout = findViewById(R.id.carousel_hero_layout);
        heroImage = findViewById(R.id.carousel_hero_image);
        heroBg = findViewById(R.id.carousel_hero_bg);
        heroBgGradient = findViewById(R.id.carousel_hero_bg_gradient);
        heroCaption = findViewById(R.id.carousel_hero_caption);

        setupRecycler(context);
        setupHero(context);
    }

    private void setupHero(Context context) {
        Glide.with(context)
                .load(heroModel.getIcon())
                .into(heroImage);
        Glide.with(context)
                .load(heroModel.getBackgroundImg())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        heroBgGradient.setBackgroundResource(R.drawable.dark_gradient);
                        return false;
                    }
                })
                .into(heroBg);

        heroCaption.setText(heroModel.getName());

        recycler.addOnScrollListener(new HeroRecyclerListener(
                context.getResources().getDimensionPixelSize(R.dimen.carousel_hero_width)));
    }

    private void setupRecycler(Context context) {
        layoutManager = new LinearLayoutManager(
                context, RecyclerView.HORIZONTAL, false);
        recycler.setLayoutManager(layoutManager);

        new GravitySnapHelper(Gravity.START)
                .attachToRecyclerView(recycler);


        // Used to limit the fling speed
        recycler.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {

                if (Math.abs(velocityX) > 4000) {
                    velocityX = 4000 * (int) Math.signum((double) velocityX);
                    recycler.fling(velocityX, velocityY);
                    return true;
                }

                return false;
            }
        });
    }

    public void addItems(List<EventNameModel> items, EventsAdapter.OnItemClickListener listener) {
        recyclerAdapter = new EventsAdapter(items, listener);
        recycler.setAdapter(recyclerAdapter);
    }

    /**
     * Changes the alpha of {@link #heroLayout} when the RecyclerView's children are scrolled. By
     * the time the hero item is no longer visible, its alpha will have been set to 0.
     */
    class HeroRecyclerListener extends RecyclerView.OnScrollListener {
        private int heroWidthPx;
        private boolean listening = true;

        HeroRecyclerListener(int heroWidthPx) {
            this.heroWidthPx = heroWidthPx;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int firstItem = layoutManager.findFirstCompletelyVisibleItemPosition();
            if (firstItem <= 1
                    && layoutManager.findFirstVisibleItemPosition() == 0) {
                listening = true;
                int offset = recyclerView.computeHorizontalScrollOffset();
                setHeroAlpha(offset);
                translateBg(offset);
            } else {
                if (listening) {
                    setHeroAlpha(heroWidthPx);
                    translateBg(heroWidthPx);
                    listening = false;
                }
            }
        }

        private void setHeroAlpha(int recyclerOffset) {
            float target = 1 - ((float) recyclerOffset / heroWidthPx * 3);
            target = target > 1 ? 1 : target < 0 ? 0 : target;

            heroLayout.setAlpha(target);
        }

        private void translateBg(int recyclerOffset) {
            float target = -1 * (float) recyclerOffset / 6;
            heroBg.setTranslationX(target);
        }

    }
}
