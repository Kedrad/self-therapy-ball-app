package com.kedrad.selftherapyball;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.paolorotolo.appintro.ISlidePolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.CheckBox;


public class IntroThirdSlide extends Fragment implements ISlidePolicy {

    @BindView(R.id.checkbox_accept) CheckBox checkBoxAccept;

    public IntroThirdSlide() {
        // Required empty public constructor
    }


    public static IntroThirdSlide newInstance() {
        return new IntroThirdSlide();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_third_slide, container, false);
        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



    @Override
    public boolean isPolicyRespected() {

        return checkBoxAccept.isChecked();// If user should be allowed to leave this slide
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        // User illegally requested next slide
        //Show a message on the bottom of the screen
        //Show a message on the bottom of the screen
        Snackbar.make(checkBoxAccept, R.string.intro_checkbox_not_checked_text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
