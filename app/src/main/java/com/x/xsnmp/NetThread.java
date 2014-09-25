package com.x.xsnmp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

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

/**
 * Created by X on 2014-08-22.
 */
public class NetThread extends Thread
{
    public Handler mChildHandler;
    public Handler mMainHandler;
    private Socket socket;
    ObjectInputStream objectInputStream=null;
    ObjectOutputStream objectOutputStream=null;
    public NetThread(Handler mMainHandler)    {
        super();
        this.mMainHandler=mMainHandler;
    }



    public void run()
    {
        Looper.prepare();

        if(socket==null)
        {
            try
            {
                socket=new Socket("61.128.177.2",8081);
                objectInputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getSubStations();

        mChildHandler=new Handler()
        {
          public void handleMessage(Message msg)
          {
              //根据消息类型，进行网络通信。
              switch (msg.what)
              {
                  case Cmd.GET_SUBSTATIONS:
                      Log.d("TAG", "getSubStation");
                      getSubStations();
                      break;
                  case Cmd.GET_COMMROOMS://GetCommRooms,msg.arg1是subID
                      getCommRooms(msg.arg1);
                      break;
                  case Cmd.GET_DEVICES://GetDevices,msg.arg1是commID；
                      getDevices(msg.arg1);
                      break;
                  case Cmd.GET_DEVICEINFO://getDeviceInfo,msg.arg1是deviceID；
                      getDeviceInfo(msg.arg1);
                      break;
                  case Cmd.GET_INTERFACES: //getDeviceInterfaces,arg1是DeviceID；
                      getDeviceInterfaces(msg.arg1);

              }
          }
        };
        Looper.loop();
    }

    private void getDeviceInterfaces(int deviceID) {
        List<DeviceInterface> deviceInterfaces=new ArrayList<DeviceInterface>();
        Cmd cmd=new Cmd(Cmd.GET_INTERFACES,deviceID,-1,"getDeviceInterfaces");
        try{
            objectOutputStream.writeObject(cmd);
            objectOutputStream.flush();
            try{
                deviceInterfaces=(List<DeviceInterface>)objectInputStream.readObject();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        mMainHandler.obtainMessage(Cmd.GET_INTERFACES,deviceInterfaces).sendToTarget();
    }

    private void getDeviceInfo(int deviceID) {
        String deviceInfo=new String();
        Cmd cmd=new Cmd(Cmd.GET_DEVICEINFO,deviceID,-1,"getDeviceInfo");
        try{
            objectOutputStream.writeObject(cmd);
            objectOutputStream.flush();

            try{
                deviceInfo=(String)objectInputStream.readObject();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }catch ( IOException e){
            e.printStackTrace();
        }
        mMainHandler.obtainMessage(Cmd.GET_DEVICEINFO,deviceInfo).sendToTarget();
    }

    private void getSubStations() {
        //获取SubStation信息，并发送消息到主线程。
        List<SubStation> subStations=new ArrayList<SubStation>();

        Cmd cmd=new Cmd(Cmd.GET_SUBSTATIONS,-1,-1,"getSubStations");
        try {
            objectOutputStream.writeObject(cmd);
            objectOutputStream.flush();

            try {
                subStations=(List<SubStation>)objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMainHandler.obtainMessage(1, subStations).sendToTarget();

    }

    private void getCommRooms(int subID) {
        List<CommRoom> commRooms=new ArrayList<CommRoom>();

        Cmd cmd=new Cmd(Cmd.GET_COMMROOMS,subID,-1,"getCommRooms");
        try{
            objectOutputStream.writeObject(cmd);
            objectOutputStream.flush();
            try{
                commRooms=(List<CommRoom>)objectInputStream.readObject();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        mMainHandler.obtainMessage(Cmd.GET_COMMROOMS,commRooms).sendToTarget();
    }

    private void getDevices(int commID) {
        List<WanDevice> wanDevices=new ArrayList<WanDevice>();

        Cmd cmd=new Cmd(Cmd.GET_DEVICES,commID,-1,"getDevices");
        try{
            objectOutputStream.writeObject(cmd);
            objectOutputStream.flush();
            try{
                wanDevices=(List<WanDevice>)objectInputStream.readObject();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        mMainHandler.obtainMessage(Cmd.GET_DEVICES,wanDevices).sendToTarget();
    }

    public void onDestroy() {

    }
}
