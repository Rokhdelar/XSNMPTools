package com.x.model;

import java.io.Serializable;

/**
 * Created by X on 2014-08-19.
 */
public class Cmd implements Serializable
{
    private static final long serialVersionUID=1L;
    public int cmdID;
    public int para1;
    public int para2;
    public String cmdName;
    public static final int GET_SUBSTATIONS=1;
    public static final int GET_COMMROOMS=2;
    public static final int GET_DEVICES=3;
    public static final int GET_DEVICEINFO=4;
    public static final int GET_INTERFACES=5;
    public static final int GET_INTERFACEINFO=6;
    public static final int GET_INTERFACEOPTICALINFO=7;
    public static final int GET_INTERFACETRAFFIC=8;

    public Cmd()
    {}

    public Cmd(int cmdID,int para1,int para2,String cmdName)
    {
        this.cmdID=cmdID;
        this.para1=para1;
        this.para2=para2;
        this.cmdName=cmdName;
    }
}
