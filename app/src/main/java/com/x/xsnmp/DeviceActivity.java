package com.x.xsnmp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.x.model.Cmd;
import com.x.model.CommRoom;
import com.x.model.DeviceInterface;
import com.x.model.SubStation;
import com.x.model.WanDevice;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class DeviceActivity extends Activity {
    private Spinner subSpinner,comSpinner,deviceSpinner,interSpinner;
    private TextView deviceInfoTextView;
    public Handler mMainHandler;
    private NetThread netThread;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        subSpinner=(Spinner)findViewById(R.id.spinnerSubstation);
        comSpinner=(Spinner)findViewById(R.id.spinnerCommRooms);
        deviceSpinner=(Spinner)findViewById(R.id.spinnerDevices);
        interSpinner=(Spinner)findViewById(R.id.spinnerInterfaces);
        deviceInfoTextView=(TextView)findViewById(R.id.textViewDeviceInfo);


        mMainHandler=new Handler()
        {
            //根据网络通信线程传送来的信息，更新UI。
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case Cmd.GET_SUBSTATIONS:
                        progressDialog=ProgressDialog.show(getApplicationContext(),"Loading...","正在加载数据，请等待...");
                        ArrayAdapter<SubStation> arrayAdapter=new ArrayAdapter<SubStation>(getApplicationContext(),android.R.layout.simple_spinner_item,(List<SubStation>)msg.obj);
                        subSpinner.setAdapter(arrayAdapter);
                        subSpinner.setPrompt("请选择设备所属的支局");
                        subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                SubStation subStation = (SubStation) parent.getSelectedItem();
                                netThread.mChildHandler.obtainMessage(Cmd.GET_COMMROOMS, subStation.subID, -1).sendToTarget();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        progressDialog.dismiss();
                        break;
                    case Cmd.GET_COMMROOMS:
                        progressDialog=ProgressDialog.show(getApplicationContext(),"Loading...","正在加载数据，请等待...");
                        ArrayAdapter<CommRoom> commRoomArrayAdapter=new ArrayAdapter<CommRoom>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,(List<CommRoom>)msg.obj);
                        comSpinner.setAdapter(commRoomArrayAdapter);
                        comSpinner.setPrompt("请选择设备所属的机房");
                        comSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                CommRoom commRoom=(CommRoom)parent.getSelectedItem();
                                netThread.mChildHandler.obtainMessage(3,commRoom.commRoomID,-1).sendToTarget();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        progressDialog.dismiss();
                        break;
                    case Cmd.GET_DEVICES:
                        progressDialog=ProgressDialog.show(getApplicationContext(),"Loading...","正在加载数据，请等待...");
                        ArrayAdapter<WanDevice> wanDeviceArrayAdapter=new ArrayAdapter<WanDevice>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,(List<WanDevice>)msg.obj);
                        deviceSpinner.setAdapter(wanDeviceArrayAdapter);
                        deviceSpinner.setPrompt("请选择要查看的设备");
                        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                WanDevice wanDevice=(WanDevice)parent.getSelectedItem();
                                netThread.mChildHandler.obtainMessage(Cmd.GET_DEVICEINFO,wanDevice.deviceID,-1).sendToTarget();
                                netThread.mChildHandler.obtainMessage(Cmd.GET_DEVICEINTERFACES,wanDevice.deviceID,-1).sendToTarget();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        progressDialog.dismiss();
                        break;
                    case Cmd.GET_DEVICEINFO:
                        progressDialog=ProgressDialog.show(getApplicationContext(),"Loading...","正在加载数据，请等待...");
                        deviceInfoTextView.setText(msg.obj.toString());
                        progressDialog.dismiss();
                        break;
                    case Cmd.GET_DEVICEINTERFACES:
                        progressDialog=ProgressDialog.show(getApplicationContext(),"Loading...","正在加载数据，请等待...");
                        ArrayAdapter<DeviceInterface> deviceInterfaceArrayAdapter=new ArrayAdapter<DeviceInterface>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,(List<DeviceInterface>)msg.obj);
                        interSpinner.setAdapter(deviceInterfaceArrayAdapter);
                        interSpinner.setPrompt("请选择要查看的端口");
                        interSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        //progressDialog.dismiss();
                        break;
                }

            }
        };

        netThread=new NetThread(mMainHandler);
        netThread.start();

        Cmd cmd=new Cmd(Cmd.GET_SUBSTATIONS,0,"getSubStations");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
