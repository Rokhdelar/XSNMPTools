package com.x.model;

import java.io.Serializable;

/**
 * Created by X on 2014-08-20.
 */
public class CommRoom implements Serializable
{
    private static final long serialVersionUID=1L;
    public int commRoomID;
    public String commRoomName;
    public int subID;
    public String commRoomAddress;
    public String commRoomContact;
    public String commRoomPhone;
    public String commRoomMemo;

    public CommRoom()
    {

    }

    public CommRoom(int commRoomID,String commRoomName,int subID,String commRoomAddress,String commRoomContact,String commRoomPhone,String commRoomMemo)
    {
        this.commRoomID=commRoomID;
        this.commRoomName=commRoomName;
        this.subID=subID;
        this.commRoomAddress=commRoomAddress;
        this.commRoomContact=commRoomContact;
        this.commRoomPhone=commRoomPhone;
        this.commRoomMemo=commRoomMemo;
    }

    public String toString()
    {
        return commRoomName;
    }
}
