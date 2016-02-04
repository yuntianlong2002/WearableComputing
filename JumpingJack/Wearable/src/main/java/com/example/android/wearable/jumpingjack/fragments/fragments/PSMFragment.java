/*
 * Copyright (C) 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wearable.jumpingjack.fragments.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.android.wearable.jumpingjack.EMAAlert;
import com.example.android.wearable.jumpingjack.PSM;
import com.example.android.wearable.jumpingjack.PSMConfirmActivity;
import com.example.android.wearable.jumpingjack.PSMScheduler;
import com.example.android.wearable.jumpingjack.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple fragment for conducting PSM
 */
public class PSMFragment extends Fragment {

    private CircledImageView mPicture;
    private SeekBar mSeekBar;
    private OnSeekBarChangeListener mSeekBarListener;

    private int mSeqId = 0;
    private List<Integer> mSeq = new ArrayList<>();
    private ArrayList<String> mImgPathList = new ArrayList<String>();
    private ArrayList<Drawable> mImgList = new ArrayList<Drawable>();
    private int mSelectedPic = -1;
    private boolean mNeedAlert = true;

    //private Button mButtonMore;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.PSM_layout,container,false );

        initGridSeq();
        preload();

        mPicture = (CircledImageView) view.findViewById(R.id.imageView);
        mPicture.setImageDrawable(mImgList.get(8));
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
        mSeekBar.setMax(16);
        mSeekBar.setProgress(8);
        mSeekBarListener = new OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //update image when dragging
                mPicture.setImageDrawable(mImgList.get(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSelectedPic = seekBar.getProgress();
            }
        };


        mPicture.setOnClickListener(new CircledImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                EMAAlert.getAlertObject().cancel();
                Log.e("SELECTED", mSelectedPic + "");

                Intent intent = new Intent(getActivity(), PSMConfirmActivity.class);
                intent.putExtra(PSMConfirmActivity.MSG_SELECTED_IMG_ID, mSelectedPic);

                startActivityForResult(intent, 0);
            }

        });


//        loadimages==preload?
        view.post(new Runnable() {
            @Override
            public void run() {
                // code you want to run when view is visible for the first time
                // preload?
                preload();
//              loadImages(mSeq.get(mSeqId));
            }
        });


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PSMScheduler.setSchedule(getActivity());

        if(mNeedAlert) {
            EMAAlert.getAlertObject().startAlert(getActivity());
            mNeedAlert = false;
        }

    }

    @Override
    public void onResume (){
        super.onResume();
    }


    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(resultCode == getActivity().RESULT_OK && data.getBooleanExtra(PSMConfirmActivity.MSG_PSM_IS_CONFIRMED,false)) {
            savePSMResponse(mSelectedPic);
            getActivity().finish();
        }

        mSelectedPic = -1;
    }

    private void savePSMResponse(int psmSlotId) {
        if(psmSlotId == -1) {
            return;
        }

        String resp = System.currentTimeMillis() / 1000 + "," + psmSlotId + "\n";

        File psmFile = new File(Environment.getExternalStorageDirectory(), "stress_meter_resp.csv");;
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = new FileOutputStream(psmFile, true);
            fileOutputStream.write(resp.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initGridSeq() {
        mSeq.add(1);
        mSeq.add(2);
        mSeq.add(3);

        Collections.shuffle(mSeq, new Random(System.currentTimeMillis()));

    }


    //preload the image set
    private void preload(){
        mImgList.clear();
        mSeqId = (int) (Math.random() * (mSeq.size()));
        int id = mSeq.get(mSeqId);
        int[] grid = PSM.getGridById(id);

        for(int i = 0; i< grid.length; i++) {
            Drawable d = getResources().getDrawable(grid[i]);
            mImgList.add(d);
            mImgPathList.add(String.valueOf(i));
        }

    }


//    private void loadImages(int gridId) {
//    }




}
