package com.x.model;
import java.io.Serializable;

/**
 * Created by X on 2014-08-12.
 */
public class SubStation implements Serializable {
    private static final long serialVersionUID=1L;
    public int subID;
    public String subName;
    public String subLeader;
    public String subPhone;
    public String subMemo;

    public SubStation() {
    }

    public SubStation(int subID, String subName, String subLeader, String subPhone, String subMemo) {
        this.subID = subID;
        this.subName = subName;
        this.subLeader = subLeader;
        this.subPhone = subPhone;
        this.subMemo = subMemo;
    }

    public String toString()
    {
        return subName;
    }
}