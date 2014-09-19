package com.x.model;

import org.snmp4j.smi.Variable;

import java.io.Serializable;

/**
 * Created by X on 2014-08-28.
 */
public class DeviceInterface implements Serializable{
    private static final long serialVersionUID=1L;
    public Variable ifIndex;
    public Variable ifDescr;
    public Variable ifAdminStatus;
    public Variable ifOperStatus;
    public Variable ifLastChange;
    public Variable ifInOctets;
    public Variable ifInDiscards;
    public Variable ifOutOctets;
    public Variable ifOutDiscards;

    public DeviceInterface(){
    }

    public String toString(){
        return String.valueOf(ifDescr);
    }
}
