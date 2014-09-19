package com.x.xsnmp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.x.model.CommRoom;
import com.x.model.SubStation;

import java.util.ArrayList;
import java.util.List;


public class deviceinfo_Activity extends Activity {
    public ProgressDialog progressDialog;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviceinfo);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //progressDialog=ProgressDialog.show(this, "Loading...", "正在载入数据...");


        new Thread(new Runnable() {
            @Override
            public void run() {

                getSubStations();
            }
        }).start();
    }



    private void getSubStations()
    {
        List<SubStation> subStations=new ArrayList<SubStation>();



        ArrayAdapter<SubStation> arrayAdapter=new ArrayAdapter<SubStation>(getApplicationContext(),android.R.layout.simple_spinner_item,subStations);
        Spinner subSpinner=(Spinner)findViewById(R.id.spinnerSubstation);
        subSpinner.setAdapter(arrayAdapter);
        subSpinner.setPrompt("请选择设备所属支局");
        subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SubStation subStation=(SubStation)parent.getSelectedItem();
                progressDialog.show();
                getCommRooms(subStation.subID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        progressDialog.dismiss();

    }


    private void getCommRooms(int subID)
    {
        List<CommRoom> commRooms=new ArrayList<CommRoom>();


        ArrayAdapter<CommRoom> arrayAdapter=new ArrayAdapter<CommRoom>(getApplicationContext(),android.R.layout.simple_spinner_item,commRooms);
        Spinner commRoomSpinner=(Spinner)findViewById(R.id.spinnerCommRooms);
        commRoomSpinner.setAdapter(arrayAdapter);
        commRoomSpinner.setPrompt("请选择您要查看的设备所属机房");

        commRoomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CommRoom commRoom=(CommRoom)parent.getSelectedItem();
                progressDialog.show();
                getDevices(commRoom.commRoomID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        progressDialog.dismiss();
    }

    private void getDevices(int commRoomID)
    {

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.device_realinfo_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
