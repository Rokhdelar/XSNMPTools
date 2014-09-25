package com.x.xsnmp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.x.model.Cmd;
import com.x.model.CommRoom;
import com.x.model.DeviceInterface;
import com.x.model.SubStation;
import com.x.model.WanDevice;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class DeviceActivity extends Activity {
    private Spinner subSpinner,comSpinner,deviceSpinner,interSpinner;
    private TextView deviceInfoTextView;
    private Button btnGetDeviceInterfaces,btnGetInterfaceInfo,btnGetInterfaceOpticalInfo,btnGetInterfaceTraffic;
    private int currentDeviceID;
    public Handler mMainHandler;
    private NetThread netThread;
    private ProgressDialog progressDialog;
    private String serverIP="61.128.177.2";
    private int serverPort=8081;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        subSpinner=(Spinner)findViewById(R.id.spinnerSubstation);
        comSpinner=(Spinner)findViewById(R.id.spinnerCommRooms);
        deviceSpinner=(Spinner)findViewById(R.id.spinnerDevices);
        interSpinner=(Spinner)findViewById(R.id.spinnerInterfaces);
        deviceInfoTextView=(TextView)findViewById(R.id.textViewDeviceInfo);
        btnGetDeviceInterfaces=(Button)findViewById(R.id.btnGetDeviceInterfaces);
        btnGetInterfaceInfo=(Button)findViewById(R.id.btnGetInterfaceInfo);
        btnGetInterfaceOpticalInfo=(Button)findViewById(R.id.btnGetInterfaceOpticalInfo);
        btnGetInterfaceTraffic=(Button)findViewById(R.id.btnGetInterfaceTraffic);

        btnGetDeviceInterfaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WanDevice wanDevice=(WanDevice)deviceSpinner.getSelectedItem();
                progressDialog.show();
                getInterfaces((wanDevice.deviceID));
            }
        });

        btnGetInterfaceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                WanDevice wanDevice=(WanDevice)deviceSpinner.getSelectedItem();
                DeviceInterface deviceInterface=(DeviceInterface)interSpinner.getSelectedItem();
                getInterfaceInfo(wanDevice.deviceID,deviceInterface.ifIndex.toInt());
            }
        });

        btnGetInterfaceOpticalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                WanDevice wanDevice=(WanDevice)deviceSpinner.getSelectedItem();
                DeviceInterface deviceInterface=(DeviceInterface)interSpinner.getSelectedItem();
                getInterfaceOpticalInfo(wanDevice.deviceID, deviceInterface.ifIndex.toInt());
            }
        });

        progressDialog=new ProgressDialog(DeviceActivity.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("正在载入数据，请稍候...");
        progressDialog.setCancelable(false);

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
                                progressDialog.show();
                                //getInterfaces(wanDevice.deviceID);
                                currentDeviceID=wanDevice.deviceID;
                                getDeviceInfo(wanDevice.deviceID);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        progressDialog.dismiss();
                        break;
                    case Cmd.GET_DEVICEINFO:
                        progressDialog.show();
                        List<String> deviceInfo=(List<String>)msg.obj;
                        if (deviceInfo.size()>1){
                            deviceInfoTextView.setTextColor(Resources.getSystem().getColor(android.R.color.holo_blue_bright));
                            deviceInfoTextView.setText("");
                            deviceInfoTextView.append("设备主机名称：\t"+deviceInfo.get(0)+"\n");
                            deviceInfoTextView.append("设备运行时间：\t"+deviceInfo.get(1)+"\n");
                        }else{
                            deviceInfoTextView.setTextColor(Color.RED);
                            deviceInfoTextView.setText(deviceInfo.get(0));
                        }

                        break;
                    case Cmd.GET_INTERFACES:
                        //progressDialog.show();
                        ArrayAdapter<DeviceInterface> deviceInterfaceArrayAdapter=new ArrayAdapter<DeviceInterface>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,(List<DeviceInterface>)msg.obj);
                        interSpinner.setAdapter(deviceInterfaceArrayAdapter);
                        interSpinner.setPrompt("请选择要查看的端口");
                        interSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                btnGetInterfaceInfo.setEnabled(true);
                                btnGetInterfaceOpticalInfo.setEnabled(true);
                                btnGetInterfaceTraffic.setEnabled(true);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        progressDialog.dismiss();
                        break;

                    case Cmd.GET_INTERFACEINFO:
                        DeviceInterface deviceInterface=(DeviceInterface)msg.obj;
                        String[] interfaceStrs=new String[8];
                        interfaceStrs[0]="端口描述：\t"+deviceInterface.ifDescr.toString();
                        interfaceStrs[1]="管理状态：\t"+((deviceInterface.ifAdminStatus.toInt()==1)?"UP":"DOWN");
                        interfaceStrs[2]="当前状态：\t"+((deviceInterface.ifOperStatus.toInt()==1)?"UP":"DOWN");
                        interfaceStrs[3]="状态改变时间：\t"+deviceInterface.ifLastChange.toString();
                        interfaceStrs[4]="入流量计数：\t"+deviceInterface.ifInOctets.toString();
                        interfaceStrs[5]="出流量计数：\t"+deviceInterface.ifOutOctets.toString();
                        interfaceStrs[6]="入丢弃计数：\t"+deviceInterface.ifInDiscards.toString();
                        interfaceStrs[7]="出丢弃计数：\t"+deviceInterface.ifOutDiscards.toString();
                        AlertDialog.Builder interfaceInfoDialog=new AlertDialog.Builder(DeviceActivity.this);
                        interfaceInfoDialog.setTitle("接口信息...");
                        interfaceInfoDialog.setIcon(R.drawable.ic_chat);
                        interfaceInfoDialog.setItems(interfaceStrs, null);
                        interfaceInfoDialog.setNegativeButton(" 关 闭 ",null);
                        interfaceInfoDialog.show();

                        break;
                    case Cmd.GET_INTERFACEOPTICALINFO:
                        List<String> interfaceOpticalInfo=(List<String>)msg.obj;
                        AlertDialog.Builder interfaceOpticalInfoDialog=new AlertDialog.Builder(DeviceActivity.this);
                        if(interfaceOpticalInfo.size()==1){
                            interfaceOpticalInfoDialog.setTitle("光功率测试失败...");
                            interfaceOpticalInfoDialog.setIcon(android.R.drawable.ic_dialog_alert);
                            interfaceOpticalInfoDialog.setMessage(interfaceOpticalInfo.toString());
                            interfaceOpticalInfoDialog.setNegativeButton(" 关 闭 ",null);
                        }else{
                            interfaceOpticalInfoDialog.setTitle("接口光功率情况...");
                            interfaceOpticalInfoDialog.setIcon(R.drawable.ic_chat);
                            interfaceOpticalInfoDialog.setItems(interfaceOpticalInfo.toArray(new String[interfaceOpticalInfo.size()]),null);
                            interfaceOpticalInfoDialog.setNegativeButton(" 关 闭 ",null);
                        }
                        interfaceOpticalInfoDialog.show();
                        break;

                }

            }
        };
    }

    private void getInterfaceOpticalInfo(final int deviceID,final int ifIndex){
        new Thread(){
            public void run(){
                Socket socket;
                ObjectInputStream objectInputStream;
                ObjectOutputStream objectOutputStream;
                List<String> interfaceOpticalInfo;
                try {
                    socket=new Socket(serverIP,serverPort);
                    objectInputStream=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    objectOutputStream=new ObjectOutputStream(socket.getOutputStream());

                    Cmd cmd=new Cmd(Cmd.GET_INTERFACEOPTICALINFO,deviceID,ifIndex,"getInterfaceInfo");
                    objectOutputStream.writeObject(cmd);
                    objectOutputStream.writeObject(null);
                    objectOutputStream.flush();

                    interfaceOpticalInfo=(List<String>)objectInputStream.readObject();
                    mMainHandler.obtainMessage(Cmd.GET_INTERFACEOPTICALINFO,interfaceOpticalInfo).sendToTarget();

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

    private void  getInterfaceInfo(final int deviceID, final int ifIndex){
        new Thread(){
            public void run(){
                Socket socket;
                ObjectInputStream objectInputStream;
                ObjectOutputStream objectOutputStream;
                DeviceInterface deviceInterface;
                try {
                    socket=new Socket(serverIP,serverPort);
                    objectInputStream=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    objectOutputStream=new ObjectOutputStream(socket.getOutputStream());

                    Cmd cmd=new Cmd(Cmd.GET_INTERFACEINFO,deviceID,ifIndex,"getInterfaceInfo");
                    objectOutputStream.writeObject(cmd);
                    objectOutputStream.writeObject(null);
                    objectOutputStream.flush();

                    deviceInterface=(DeviceInterface)objectInputStream.readObject();
                    mMainHandler.obtainMessage(Cmd.GET_INTERFACEINFO,deviceInterface).sendToTarget();

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

    private  void getDeviceInfo(final int deviceID){
        new Thread(){
            public void run(){
                Socket socket;
                ObjectOutputStream objectOutputStream;
                ObjectInputStream objectInputStream;
                List<String> deviceInfo;
                try{
                    socket=new Socket(serverIP,serverPort);
                    objectInputStream=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    objectOutputStream=new ObjectOutputStream(socket.getOutputStream());

                    Cmd cmd=new Cmd(Cmd.GET_DEVICEINFO,deviceID,-1,"getDeviceInfo");
                    objectOutputStream.writeObject(cmd);
                    objectOutputStream.writeObject(null);
                    objectOutputStream.flush();
                    deviceInfo=(List<String>)objectInputStream.readObject();
                    mMainHandler.obtainMessage(Cmd.GET_DEVICEINFO,deviceInfo).sendToTarget();
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

    private void getInterfaces(final int deviceID){
        new Thread(){
            public void run(){
                Socket socket;
                ObjectInputStream objectInputStream;
                ObjectOutputStream objectOutputStream;
                List<DeviceInterface> deviceInterfaces;
                try{
                    socket=new Socket(serverIP,serverPort);
                    objectInputStream=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    objectOutputStream=new ObjectOutputStream(socket.getOutputStream());

                    Cmd cmd=new Cmd(Cmd.GET_INTERFACES,deviceID,-1,"getDeviceInterfaces");
                    objectOutputStream.writeObject(cmd);
                    objectOutputStream.writeObject(null);
                    objectOutputStream.flush();
                    deviceInterfaces=(List<DeviceInterface>)objectInputStream.readObject();
                    mMainHandler.obtainMessage(Cmd.GET_INTERFACES,deviceInterfaces).sendToTarget();
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

                    Cmd cmd=new Cmd(Cmd.GET_COMMROOMS,subID,-1,"getCommRooms");

                    objectOutputStream.writeObject(cmd);
                    objectOutputStream.writeObject(null);
                    objectOutputStream.flush();
                    commRooms=(List<CommRoom>)objectInputStream.readObject();
                    mMainHandler.obtainMessage(Cmd.GET_COMMROOMS,commRooms).sendToTarget();
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

                    Cmd cmd=new Cmd(Cmd.GET_DEVICES,commRoomID,-1,"getDevices");

                    objectOutputStream.writeObject(cmd);
                    objectOutputStream.writeObject(null);
                    objectOutputStream.flush();
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

                   Cmd cmd=new Cmd(Cmd.GET_SUBSTATIONS,-1,-1,"getSubStation");

                   objectOutputStream.writeObject(cmd);
                   objectOutputStream.writeObject(null);
                   objectOutputStream.flush();
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
        switch (id){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
