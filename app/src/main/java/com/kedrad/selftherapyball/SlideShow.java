/*

    Simple slideshow made using ImageSwitcher and Glide library for loading images with crossfading effect between them

*/

package com.kedrad.selftherapyball;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;


public class SlideShow {
    private ImageSwitcher imageSwitcher;
    private String[] instructionImages;
    private String[] instructionImagesAfter30Seconds;
    private Context context;
    private int period;
    private int delay;

    private int currentImageIndex;
    private String[] currentImageArray;

    private Timer slideshowTimer;

    //Constructor with provided period and delay
    public SlideShow(ImageSwitcher imageSwitcher, String[] instructionImages, String[] instructionImagesAfter30Seconds, Context context, int period, int delay){
        this.imageSwitcher = imageSwitcher;
        this.instructionImages = instructionImages;
        this.instructionImagesAfter30Seconds = instructionImagesAfter30Seconds;
        this.currentImageArray = instructionImages;
        this.context = context;
        this.period = period;
        this.delay = delay;
        this.currentImageIndex = 0;
        loadFirstImage();
    }

    //Constructor with default period and delay
    public SlideShow(ImageSwitcher imageSwitcher, String[] instructionImages, String[] instructionImagesAfter30Seconds, Context context){
        this.imageSwitcher = imageSwitcher;
        this.instructionImages = instructionImages;
        this.instructionImagesAfter30Seconds = instructionImagesAfter30Seconds;
        this.currentImageArray = instructionImages;
        this.context = context;
        this.period = 1000;
        this.delay = 0;
        this.currentImageIndex = 0;
        loadFirstImage();
    }

    private void loadFirstImage(){
        GlideApp.with(context.getApplicationContext())
                .load(context.getApplicationContext().getResources().getIdentifier(currentImageArray[currentImageIndex], "drawable", context.getPackageName()))
                .fitCenter()
                .into((ImageView) imageSwitcher.getNextView());
        imageSwitcher.showNext();

        //Setting image switching animations
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
    }



    public void start(){
        slideshowTimer = new Timer();
        //Set the schedule function
        slideshowTimer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                //Called each time after given period in milliseconds
                currentImageIndex++;

                // If index reaches maximum go backwards
                if(currentImageIndex >= instructionImages.length) {
                    ArrayUtils.reverse(currentImageArray);
                    currentImageIndex = 1;
                }




                ((Activity)context).runOnUiThread(new Runnable() {
                    public void run() {
                        imageSwitcher.getNextView().setVisibility(View.INVISIBLE);

                        if(!((Activity) context).isFinishing())
                            GlideApp.with(context)
                                    .load(context.getResources().getIdentifier(currentImageArray[currentImageIndex], "drawable", context.getPackageName()))
                                    .listener(new RequestListener<Drawable>() {

                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            imageSwitcher.showNext();
                                            return false;
                                        }
                                    })
                                    .fitCenter()
                                    .into((ImageView) imageSwitcher.getNextView());
                        else
                            stop();

                    }

                });
            }

        }, delay, period);

    }

    public void stop(){
        slideshowTimer.cancel();
    }

    public void changeToSecondImageArray(){
        stop();
        if (currentImageArray == instructionImages)
            currentImageArray = instructionImagesAfter30Seconds;
        else
            currentImageArray = instructionImages;

        currentImageIndex = 0;
        start();
    }

}
