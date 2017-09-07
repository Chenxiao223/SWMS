package com.hiklife.rfidapi;

public class InventoryTagInfo {
	private String flagid = "";
	public short[] epc;
    public float rssi;
    public String flagtid="";
    public short[] tid;
    
    public String getFlagID()
    {
    	if (flagid != "")
        {
            return flagid;
        }
        else
        {
            if (epc != null)
            {
                for (int i = 0; i < epc.length; i++)
                {
                    flagid += Integer.toHexString(((epc[i] >> 8) & 0x000000FF) | 0xFFFFFF00).substring(6) + Integer.toHexString((epc[i] & 0x000000FF) | 0xFFFFFF00).substring(6);
                }
                
                return flagid;
            }
            else
            {
                return "";
            }
        }
    }

    public String getFlagTID()
    {
        if (flagtid != "")
        {
            return flagtid;
        }
        else
        {
            if (tid != null)
            {
                for (int i = 0; i < tid.length; i++)
                {
                    flagtid += Integer.toHexString(((tid[i] >> 8) & 0x000000FF) | 0xFFFFFF00).substring(6) + Integer.toHexString((tid[i] & 0x000000FF) | 0xFFFFFF00).substring(6);
                }

                return flagtid;
            }
            else
            {
                return "";
            }
        }
    }
}
