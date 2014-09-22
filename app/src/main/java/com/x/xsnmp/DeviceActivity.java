package com.x.xsnmp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


public class DeviceActivity extends Activity {
    private Spinner subSpinner,comSpinner,deviceSpinner,interSpinner;
    private TextView deviceInfoTextView;
    public Handler mMainHandler;
    private NetThread netThread;
    private ProgressDialog progressDialog;
    private String serverIP="61.128.177.2";
    private int serverPort=8081;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        subSpinner=(Spinner)findViewById(R.id.spinnerSubstation);
        comSpinner=(Spinner)findViewById(R.id.spinnerCommRooms);
        deviceSpinner=(Spinner)findViewById(R.id.spinnerDevices);
        interSpinner=(Spinner)findViewById(R.id.spinnerInterfaces);
        deviceInfoTextView=(TextView)findViewById(R.id.textViewDeviceInfo);

        progressDialog=new ProgressDialog(DeviceActivity.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("正在载入数据，请稍候...");

        progressDialog.show();

        getSubStations();

        mMainHandler=new Handler()
        {
            //根据网络通信线程传送来的信息，更新UI。
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case Cmd.GET_SUBSTATIONS:
                        ArrayAdapter<SubStation> arrayAdapter=new ArrayAdapter<SubStation>(getApplicationContext(),android.R.layout.simple_spinner_item,(List<SubStation>)msg.obj);
                        subSpinner.setAdapter(arrayAdapter);
                        subSpinner.setPrompt("请选择设备所属的支局");
                        subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                SubStation subStation = (SubStation) parent.getSelectedItem();
                                progressDialog.show();
                                getCommRooms(subStation.subID);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                    case Cmd.GET_COMMROOMS:
                        progressDialog.show();
                        ArrayAdapter<CommRoom> commRoomArrayAdapter=new ArrayAdapter<CommRoom>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,(List<CommRoom>)msg.obj);
                        comSpinner.setAdapter(commRoomArrayAdapter);
                        comSpinner.setPrompt("请选择设备所属的机房");
                        comSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        break;
                    case Cmd.GET_DEVICES:
                        progressDialog.show();
                        ArrayAdapter<WanDevice> wanDeviceArrayAdapter=new ArrayAdapter<WanDevice>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,(List<WanDevice>)msg.obj);
                        deviceSpinner.setAdapter(wanDeviceArrayAdapter);
                        deviceSpinner.setPrompt("请选择要查看的设备");
                        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                WanDevice wanDevice=(WanDevice)parent.getSelectedItem();
//                                netThread.mChildHandler.obtainMessage(Cmd.GET_DEVICEINFO,wanDevice.deviceID,-1).sendToTarget();
 //                               netThread.mChildHandler.obtainMessage(Cmd.GET_DEVICEINTERFACES,wanDevice.deviceID,-1).sendToTarget();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        progressDialog.dismiss();
                        break;
                    case Cmd.GET_DEVICEINFO:
                        progressDialog.show();
                        deviceInfoTextView.setText(msg.obj.toString());
                        progressDialog.dismiss();
                        break;
                    case Cmd.GET_DEVICEINTERFACES:
                        progressDialog.show();
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
                        progressDialog.dismiss();
                        break;
                }

            }
        };

//        netThread=new NetThread(mMainHandler);
//        netThread.start();


    }

    private void getCommRooms(final int subID){
        new Thread(){
            public void run(){
                Socket socket;
                ObjectInputStream objectInputStream;
                ObjectOutputStream objectOutputStream;
                List<CommRoom> commRooms;
                try{
                    socket=new Socket(serverIP,serverPort);
                    objectInputStream=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    objectOutputStream=new ObjectOutputStream(socket.getOutputStream());

                    Cmd cmd=new Cmd(Cmd.GET_COMMROOMS,subID,"getCommRooms");

                    objectOutputStream.writeObject(cmd);
                    objectOutputStream.writeObject(null);
                    commRooms=(List<CommRoom>)objectInputStream.readObject();
                    mMainHandler.obtainMessage(Cmd.GET_COMMROOMS,commRooms).sendToTarget();
                    objectInputStream.close();
                    objectOutputStream.close();
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }.start();
    }

    private void getDevices(final int commRoomID){
        new Thread(){
            public void run(){
                Socket socket;
                ObjectInputStream objectInputStream;
                ObjectOutputStream objectOutputStream;
                List<WanDevice> wanDevices;
                try{
                    socket=new Socket(serverIP,serverPort);
                    objectInputStream=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    objectOutputStream=new ObjectOutputStream(socket.getOutputStream());

                    Cmd cmd=new Cmd(Cmd.GET_DEVICES,commRoomID,"getDevices");

                    objectOutputStream.writeObject(cmd);
                    objectOutputStream.writeObject(null);
                    wanDevices=(List<WanDevice>)objectInputStream.readObject();

                    mMainHandler.obtainMessage(Cmd.GET_DEVICES,wanDevices).sendToTarget();
                    objectInputStream.close();
                    objectOutputStream.close();
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    progressDialog.dismiss();
                }
            }
        }.start();
    }


    private void getSubStations(){
       new Thread(){
           public void run(){
               Socket socket;
               ObjectOutputStream objectOutputStream;
               ObjectInputStream objectInputStream;
               List<SubStation> subStations;
               try{
                   socket=new Socket(serverIP,serverPort);
                   objectInputStream=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                   objectOutputStream=new ObjectOutputStream(socket.getOutputStream());

                   Cmd cmd=new Cmd(Cmd.GET_SUBSTATIONS,-1,"getSubStation");

                   objectOutputStream.writeObject(cmd);
                   objectOutputStream.writeObject(null);
                   subStations=(List<SubStation>)objectInputStream.readObject();

                   mMainHandler.obtainMessage(Cmd.GET_SUBSTATIONS,subStations).sendToTarget();
                   objectOutputStream.close();
                   objectInputStream.close();
                   socket.close();
               }catch (Exception e){
                   e.printStackTrace();
               }finally {
                   progressDialog.dismiss();
               }
           }
       }.start();
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
