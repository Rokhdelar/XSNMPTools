package com.x.model;

import java.io.Serializable;

/**
 * Created by X on 2014-08-19.
 */
public class Cmd implements Serializable
{
    private static final long serialVersionUID=1L;
    public int cmdID;
    public int para;
    public String cmdName;
    public static final int GET_SUBSTATIONS=1;
    public static final int GET_COMMROOMS=2;
    public static final int GET_DEVICES=3;
    public static final int GET_DEVICEINFO=4;
    public static final int GET_DEVICEINTERFACES=5;

    public Cmd()
    {}

    public Cmd(int cmdID,int para,String cmdName)
    {
        this.cmdID=cmdID;
        this.para=para;
        this.cmdName=cmdName;
    }
}
