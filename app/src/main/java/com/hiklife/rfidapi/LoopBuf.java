package com.hiklife.rfidapi;

public class LoopBuf {

	private static Object lock = new Object();
	
	private byte[] LocalBuffer = new byte[204800];
	
	private int startIndex = 0;
	
	private int endIndex = 0;

    public void Add(byte[] buf)
    {
    	try
    	{
    		synchronized(lock)
        	{
        		if (startIndex <= endIndex)
                {
                    if (((LocalBuffer.length - 1) - endIndex) >= buf.length)
                    {
                    	System.arraycopy(buf, 0, LocalBuffer, endIndex, buf.length);
                        endIndex += buf.length;
                    }
                    else if (startIndex + ((LocalBuffer.length - 1) - endIndex) >= buf.length)
                    {
                        int copyLen = ((LocalBuffer.length - 1) - endIndex + 1);
                        System.arraycopy(buf, 0, LocalBuffer, endIndex, copyLen);
                        System.arraycopy(buf, copyLen, LocalBuffer, 0, buf.length - copyLen);
                        endIndex = buf.length - copyLen;
                    }
                    else
                    {
                    	byte[] newBuf = new byte[LocalBuffer.length + buf.length * 2];
                        System.arraycopy(LocalBuffer, 0, newBuf, 0, LocalBuffer.length); 
                        System.arraycopy(buf, 0, newBuf, endIndex, buf.length); 
                        endIndex += buf.length;
                        LocalBuffer = newBuf;
                    }
                }
                else
                {
                    if ((startIndex - endIndex) >= buf.length)
                    {
                    	System.arraycopy(buf, 0, LocalBuffer, endIndex, buf.length);
                        endIndex += buf.length;
                    } 
                    else
                    {
                    	byte[] newBuf = new byte[LocalBuffer.length + buf.length * 2];
                    	System.arraycopy(LocalBuffer, 0, newBuf, 0, endIndex - 1);
                    	System.arraycopy(buf, 0, newBuf, endIndex, buf.length); 
                        endIndex += buf.length;
                        System.arraycopy(LocalBuffer, startIndex, newBuf, newBuf.length - (LocalBuffer.length - startIndex), LocalBuffer.length - startIndex);
                        startIndex = newBuf.length - (LocalBuffer.length - startIndex);
                        LocalBuffer = newBuf;
                    }
                }
        	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }

    public byte[] GetFullPackBuf()
    {
    	try
    	{
    		synchronized (lock)
            {
        		int findStartIndex = startIndex;
            	int findEndIndex = startIndex;
            	boolean findStart = false;
            	boolean findEnd = false;
            	
                if (startIndex < endIndex)
                {
                    while (findStartIndex < endIndex - 1) // ֻ
                    {
                        if ((LocalBuffer[findStartIndex]& 0x00FF) == 0x5A && (LocalBuffer[findStartIndex + 1]& 0x00FF) == 0x55)
                        {
                            findEndIndex = findStartIndex + 1;
                            findStart = true;
                            break;
                        }

                        findStartIndex++;
                    }

                    if (findStart)
                    {
                        while (findEndIndex < endIndex - 1)
                        {
                            if ((LocalBuffer[findEndIndex]& 0x00FF) == 0x6A && (LocalBuffer[findEndIndex + 1]& 0x00FF) == 0x69)
                            {
                                findEndIndex += 1;
                                findEnd = true;
                                break;
                            }
                            else if ((LocalBuffer[findEndIndex]& 0x00FF) == 0x5A && (LocalBuffer[findEndIndex + 1]& 0x00FF) == 0x55)
                            {
                                findStartIndex = findEndIndex;
                            }

                            findEndIndex++;
                        }

                        if (findEnd)
                        {
                            byte[] tempbuf = new byte[findEndIndex - findStartIndex + 1];
                            System.arraycopy(LocalBuffer, findStartIndex, tempbuf, 0, findEndIndex - findStartIndex + 1);
                            tempbuf = commonFun.Del0x99(tempbuf);
                            
                            if (tempbuf == null)
                            {
                                startIndex = findEndIndex + 1;
                                if (startIndex > endIndex)
                                {
                                    startIndex = endIndex;
                                }
                                
                                return null;
                            }
                            else
                            {
                                startIndex = findEndIndex + 1;
                                if (startIndex > endIndex)
                                {
                                    startIndex = endIndex;
                                }
                                
                                return tempbuf;
                            }
                        }
                        else
                        {
                        	return null;
                        }
                    }
                    else
                    {
                        if ((LocalBuffer[startIndex]& 0x00FF) != 0x5A)
                        {
                            startIndex++;
                        }
                        
                        return null;
                    }
                }
                else if (startIndex > endIndex)
                {
                    byte[] tempBuf = new byte[LocalBuffer.length - startIndex + 1 + endIndex + 1];
                    System.arraycopy(LocalBuffer, startIndex, tempBuf, 0, LocalBuffer.length - startIndex);
                    System.arraycopy(LocalBuffer, 0, tempBuf, LocalBuffer.length - startIndex, endIndex + 1);

                    findStartIndex = 0;
                    while (findStartIndex < tempBuf.length - 2) //
                    {
                        if ((tempBuf[findStartIndex]& 0x00FF) == 0x5A && (tempBuf[findStartIndex + 1]& 0x00FF) == 0x55)
                        {
                            findEndIndex = findStartIndex + 1;
                            findStart = true;
                            break;
                        }

                        findStartIndex++;
                    }

                    if (findStart)
                    {
                        while (findEndIndex < tempBuf.length - 2)
                        {
                            if ((tempBuf[findEndIndex]& 0x00FF) == 0x6A && (tempBuf[findEndIndex + 1]& 0x00FF) == 0x69)
                            {
                                findEndIndex += 1;
                                findEnd = true;
                                break;
                            }
                            else if ((tempBuf[findEndIndex]& 0x00FF) == 0x5A && (tempBuf[findEndIndex + 1]& 0x00FF) == 0x55)
                            {
                                findStartIndex = findEndIndex;
                            }

                            findEndIndex++;
                        }

                        if (findEnd)
                        {
                            byte[] tempcheckbuf = new byte[findEndIndex - findStartIndex + 1];
                            System.arraycopy(tempBuf, findStartIndex, tempcheckbuf, 0, findEndIndex - findStartIndex + 1);
                            tempcheckbuf = commonFun.Del0x99(tempcheckbuf);
                            
                            if (tempcheckbuf == null)
                            {
                                startIndex += (findEndIndex + 1);
                                if (startIndex > LocalBuffer.length)
                                {
                                    startIndex = startIndex - LocalBuffer.length - 1;
                                    if (startIndex > endIndex)
                                    {
                                        startIndex = endIndex;
                                    }
                                }
                                
                                return null;
                            }
                            else
                            {
                                startIndex += (findEndIndex + 1);
                                if (startIndex > LocalBuffer.length)
                                {
                                    startIndex = startIndex - LocalBuffer.length - 1;
                                    if (startIndex > endIndex)
                                    {
                                        startIndex = endIndex;
                                    }
                                }
                                
                                return tempcheckbuf;
                            }
                        }
                        else
                        {
                        	return null;
                        }
                    }
                    else
                    {
                        if ((LocalBuffer[startIndex]& 0x00FF) != 0x5A)
                        {
                            startIndex++;
                            if (startIndex > LocalBuffer.length)
                            {
                                startIndex = 0;
                                if (startIndex > endIndex)
                                {
                                    startIndex = endIndex;
                                }
                            }
                        }

                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }
}


