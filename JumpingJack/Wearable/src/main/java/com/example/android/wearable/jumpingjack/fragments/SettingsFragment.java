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

package com.example.android.wearable.jumpingjack.fragments;

import com.example.android.wearable.jumpingjack.MainActivity;
import com.example.android.wearable.jumpingjack.R;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * A simple fragment that shows a button to reset the counter
 */
public class SettingsFragment extends Fragment {

    private ListView listView;
    CSVAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_layout, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        //Create Adapter. The second parameter is required by ArrayAdapter
        //which our Adapter extends. In this example though it is unused,
        //so we'll pass it a "dummy" value of -1.
        mAdapter = new CSVAdapter(getActivity().getApplicationContext(), -1);

        //attach our Adapter to the ListView. This will populate all of the rows.
        listView.setAdapter(mAdapter);


        List<PointValue> values = new ArrayList<PointValue>();
        int i = 0;
        //values.add(new PointValue(0, 2));
        //values.add(new PointValue(1, 4));
        //values.add(new PointValue(2, 3));
        //values.add(new PointValue(3, 4));


        File mRawAccFile = new File(Environment.getExternalStorageDirectory(), "stress.csv");

        try {
            // Get input stream and Buffered Reader for our data file.
            // = ctx.getAssets().open("states.csv");
            InputStream is = new FileInputStream(mRawAccFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            //Read each line
            while ((line = reader.readLine()) != null) {

                //Split to separate the name from the capital
                String[] RowData = line.split(",");
                values.add(new PointValue(i, Integer.parseInt(RowData[1])));
                i++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        LineChartView chart = (LineChartView) view.findViewById(R.id.chart);

        chart.setLineChartData(data);

        return view;
    }

}
