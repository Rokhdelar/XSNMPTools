package com.x.model;

import java.io.Serializable;

/**
 * Created by X on 2014-08-26.
 */
public class WanDevice implements Serializable{
    private static final long serialVersionUID=1L;
    public int deviceID;
    public String deviceName;
    public String deviceIP;
    public String deviceSNMPRO;
    public String deviceSNMPRW;
    public String deviceType;
    public String deviceModel;
    public String deviceBRAS;
    public String deviceBRASPORT;
    public String deviceInternetVLAN;
    public int commroomID;
    public String deviceMemo;

    public WanDevice(){}

    public String toString(){
        return deviceName+"-"+deviceIP;
    }
}
