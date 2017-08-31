/**
 * Copyright (c) 2013, HIKLIFE.
 */
package com.hiklife.rfidapi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.minihost.rfidapi.SerialPort;

public class RadioCtrl implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 1761773103175439526L;

    private SerialPort mSerialPort = null;

    private OutputStream mOutputStream = null;

    private InputStream mInputStream = null;

    private LoopBuf recvBuffer = new LoopBuf();

    private boolean isBusy = false;

    private boolean stopInventory = true;

    private boolean isInventory = false;

    private boolean isConnent = false;

    private int invMode = 1;

    private int interval = 200;

    private boolean stopRetrieve = true;

    private RetrievePacketThread recvThread = null;

    private class RetrievePacketThread extends Thread {
        @Override
        public void run() {
            int bytes = 0;
            while (!stopRetrieve) {
                try {
                    if (mSerialPort != null) {
                        synchronized (mInputStream) {
                            bytes = mInputStream.available();
                            if (bytes > 0) {
                                byte[] buf = new byte[bytes];

                                mInputStream.read(buf, 0, bytes);

                                recvBuffer.Add(buf);
                            } else {
                                try {
                                    Thread.sleep(100);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                continue;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<OnMacErrorEventListener> mMacErrorListeners = new ArrayList<OnMacErrorEventListener>();

    public void setMacErrorEventListener(OnMacErrorEventListener e) {
        this.mMacErrorListeners.add(e);
    }

    private void fireMacErrorEventListener(MacErrorEvent event) {
        for (OnMacErrorEventListener listener : this.mMacErrorListeners) {
            listener.RadioMacError(event);
        }
    }

    private List<OnInventoryEventListener> mInventoryListeners = new ArrayList<OnInventoryEventListener>();

    public void setInventoryEventListener(OnInventoryEventListener e) {
        this.mInventoryListeners.add(e);
    }

    private void fireInventoryEventListener(InventoryEvent event) {
        for (OnInventoryEventListener listener : this.mInventoryListeners) {
            listener.RadioInventory(event);
        }
    }

    private InventoryThread invThread = null;

    private class InventoryThread extends Thread {
        @Override
        public void run() {
            boolean stopThread = false;
            int waitCount = 0;
            while (!stopThread) {
                byte[] packet = recvBuffer.GetFullPackBuf();
                if (packet != null) {
                    switch (ByteUtil.getReverseBytesShort(packet, 5)) {
                        case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_BEGIN: {
                            break;
                        }

                        case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_END: {
                            long errorInfo = ByteUtil.getReverseBytesInt(packet, 8);
                            if (invMode == 1) {
                                stopThread = true;
                            } else {
                                if (stopInventory == true) {
                                    stopThread = true;
                                } else {
                                    try {
                                        Thread.sleep(interval);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        if (mSerialPort != null) {
                                            byte[] buffer = new byte[2];
                                            buffer[0] = 0x00;
                                            buffer[1] = 0x00;
                                            buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_INVENTORY_COMMAND);
                                            try {
                                                synchronized (mOutputStream) {
                                                    mOutputStream.write(buffer);
                                                    mOutputStream.flush();
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            errorInfo = 0x0801;
                                        }
                                    } catch (SecurityException e) {
                                        errorInfo = 0x0801;
                                    }
                                }
                            }

                            if (errorInfo != 0) {
                                MacErrorInfo error = new MacErrorInfo();
                                error.macError = errorInfo;
                                fireMacErrorEventListener(new MacErrorEvent(this, error));
                            }

                            break;
                        }

                        case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_18K6C_INVENTORY: {
                            int pktEpc_start = 17;
                            int PktRssi = ByteUtil.getReverseBytesShort(packet, 13);
                            int epcLength = ByteUtil.getReverseBytesShort(packet, 15);

                            InventoryTagInfo inventoryInfo = new InventoryTagInfo();
                            inventoryInfo.epc = new short[epcLength / 2];
                            for (int i = pktEpc_start, j = 0; i < (pktEpc_start + epcLength) && j < inventoryInfo.epc.length; i += 2, j++) {
                                inventoryInfo.epc[j] = ByteUtil.getShort(packet, i);
                            }

                            inventoryInfo.rssi = (float) (PktRssi / 10.0);

                            fireInventoryEventListener(new InventoryEvent(this, inventoryInfo));
                            break;
                        }

                        default:
                            break;
                    }
                } else {
                    if (stopInventory) {
                        waitCount++;
                        if (waitCount > 20) {
                            stopThread = true;
                        }
                    }

                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            isInventory = true;
            isBusy = false;
        }
    }

    private List<TagOperResult> TagMemoryOperate() throws radioFailException {
        TagOperResult tempResult = new TagOperResult();
        List<TagOperResult> resultList = new ArrayList<TagOperResult>();

        int retryCount = 0;
        do {
            byte[] packet = recvBuffer.GetFullPackBuf();
            if (packet != null) {
                retryCount = 0;
                switch (ByteUtil.getReverseBytesShort(packet, 5)) {
                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_BEGIN: {
                        resultList.clear();
                        break;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_END: {
                        long errorInfo = ByteUtil.getReverseBytesInt(packet, 8);
                        if (errorInfo != 0) {
                            MacErrorInfo error = new MacErrorInfo();
                            error.macError = errorInfo;
                            fireMacErrorEventListener(new MacErrorEvent(this, error));
                            throw new radioFailException("Tag Access Fail");
                        }

                        return resultList;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_18K6C_INVENTORY: {
                        int pktEpc_start = 17;
                        int epcLength = ByteUtil.getReverseBytesShort(packet, 15);
                        tempResult.epc = new short[epcLength / 2];
                        for (int i = pktEpc_start, j = 0; i < (pktEpc_start + epcLength) && j < tempResult.epc.length; i += 2, j++) {
                            tempResult.epc[j] = ByteUtil.getShort(packet, i);
                        }

                        break;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_18K6C_TAG_ACCESS: {
                        short errorcode = ByteUtil.getReverseBytesShort(packet, 14);
                        tempResult.backscatterErrorCode = packet[13] == 0x00 ? backscatterError.Ok : (packet[13] == 0x03 ? backscatterError.PCValueNotExist : (packet[13] == 0x04 ? backscatterError.SpecifiedMemoryLocationLocked : (packet[13] == 0x0B ? backscatterError.InsufficientPower : (packet[13] == 0x0F ? backscatterError.NotSupportErrorSpecificCodes : backscatterError.NotSupportErrorSpecificCodes))));
                        switch (errorcode) {
                            case 0x0000: {
                                tempResult.macAccessErrorCode = macAccessError.Ok;
                                break;
                            }

                            case 0x0001: {
                                tempResult.macAccessErrorCode = macAccessError.HandleMismatch;
                                break;
                            }

                            case 0x0002: {
                                tempResult.macAccessErrorCode = macAccessError.CRCErrorOnTagResponse;
                                break;
                            }

                            case 0x0003: {
                                tempResult.macAccessErrorCode = macAccessError.NoTagReply;
                                break;
                            }

                            case 0x0004: {
                                tempResult.macAccessErrorCode = macAccessError.InvalidPassword;
                                break;
                            }

                            case 0x0005: {
                                tempResult.macAccessErrorCode = macAccessError.ZeroKillPassword;
                                break;
                            }

                            case 0x0006: {
                                tempResult.macAccessErrorCode = macAccessError.TagLost;
                                break;
                            }

                            case 0x0007: {
                                tempResult.macAccessErrorCode = macAccessError.CommandFormatError;
                                break;
                            }

                            case 0x0008: {
                                tempResult.macAccessErrorCode = macAccessError.ReadCountInvalid;
                                break;
                            }

                            case 0x0009: {
                                tempResult.macAccessErrorCode = macAccessError.OutOfRetries;
                                break;
                            }

                            default: {
                                tempResult.macAccessErrorCode = macAccessError.OperationFailed;
                                break;
                            }
                        }

                        if (tempResult.backscatterErrorCode != backscatterError.Ok || tempResult.macAccessErrorCode != macAccessError.Ok) {
                            tempResult.result = tagMemoryOpResult.OperationFailed;
                        } else {
                            tempResult.result = tagMemoryOpResult.Ok;
                        }

                        if (IsAlreadyExistsInMemoryOPResults(resultList, tempResult)) {
                            break;
                        }

                        TagOperResult newReadResult = new TagOperResult();
                        newReadResult.epc = tempResult.epc.clone();
                        newReadResult.backscatterErrorCode = tempResult.backscatterErrorCode;
                        newReadResult.macAccessErrorCode = tempResult.macAccessErrorCode;
                        newReadResult.result = tempResult.result;

                        resultList.add(newReadResult);
                        break;
                    }

                    default:
                        break;
                }
            } else {
                retryCount++;

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } while (retryCount < 30);

        return resultList;
    }

    private boolean IsAlreadyExistsInMemoryOPResults(List<TagOperResult> readResults, TagOperResult tag) {
        boolean isAlreadyExists = false;
        for (int i = 0; i < readResults.size(); i++) {
            if (readResults.get(i).getFlagID() == tag.getFlagID()) {
                isAlreadyExists = true;
                if (readResults.get(i).result == tagMemoryOpResult.OperationFailed && tag.result == tagMemoryOpResult.Ok) {
                    readResults.get(i).backscatterErrorCode = backscatterError.Ok;
                    readResults.get(i).macAccessErrorCode = macAccessError.Ok;
                    readResults.get(i).result = tagMemoryOpResult.Ok;
                }

                break;
            }
        }

        return isAlreadyExists;
    }

    private List<ReadResult> TagAccessOperate() throws radioFailException {
        ReadResult tempResult = new ReadResult();
        List<ReadResult> resultList = new ArrayList<ReadResult>();

        int retryCount = 0;
        do {
            byte[] packet = recvBuffer.GetFullPackBuf();
            if (packet != null) {
                retryCount = 0;
                switch (ByteUtil.getReverseBytesShort(packet, 5)) {
                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_BEGIN: {
                        resultList.clear();
                        break;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_END: {
                        long errorInfo = ByteUtil.getReverseBytesInt(packet, 8);
                        if (errorInfo != 0) {
                            MacErrorInfo error = new MacErrorInfo();
                            error.macError = errorInfo;
                            fireMacErrorEventListener(new MacErrorEvent(this, error));
                            throw new radioFailException("Tag Access Fail");
                        }

                        return resultList;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_18K6C_INVENTORY: {
                        int pktEpc_start = 17;
                        int epcLength = ByteUtil.getReverseBytesShort(packet, 15);
                        tempResult.epc = new short[epcLength / 2];
                        for (int i = pktEpc_start, j = 0; i < (pktEpc_start + epcLength) && j < tempResult.epc.length; i += 2, j++) {
                            tempResult.epc[j] = ByteUtil.getShort(packet, i);
                        }

                        break;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_18K6C_TAG_ACCESS: {
                        short errorcode = ByteUtil.getReverseBytesShort(packet, 14);
                        tempResult.backscatterErrorCode = packet[13] == 0x00 ? backscatterError.Ok : (packet[13] == 0x03 ? backscatterError.PCValueNotExist : (packet[13] == 0x04 ? backscatterError.SpecifiedMemoryLocationLocked : (packet[13] == 0x0B ? backscatterError.InsufficientPower : (packet[13] == 0x0F ? backscatterError.NotSupportErrorSpecificCodes : backscatterError.NotSupportErrorSpecificCodes))));
                        switch (errorcode) {
                            case 0x0000: {
                                tempResult.macAccessErrorCode = macAccessError.Ok;
                                break;
                            }

                            case 0x0001: {
                                tempResult.macAccessErrorCode = macAccessError.HandleMismatch;
                                break;
                            }

                            case 0x0002: {
                                tempResult.macAccessErrorCode = macAccessError.CRCErrorOnTagResponse;
                                break;
                            }

                            case 0x0003: {
                                tempResult.macAccessErrorCode = macAccessError.NoTagReply;
                                break;
                            }

                            case 0x0004: {
                                tempResult.macAccessErrorCode = macAccessError.InvalidPassword;
                                break;
                            }

                            case 0x0005: {
                                tempResult.macAccessErrorCode = macAccessError.ZeroKillPassword;
                                break;
                            }

                            case 0x0006: {
                                tempResult.macAccessErrorCode = macAccessError.TagLost;
                                break;
                            }

                            case 0x0007: {
                                tempResult.macAccessErrorCode = macAccessError.CommandFormatError;
                                break;
                            }

                            case 0x0008: {
                                tempResult.macAccessErrorCode = macAccessError.ReadCountInvalid;
                                break;
                            }

                            case 0x0009: {
                                tempResult.macAccessErrorCode = macAccessError.OutOfRetries;
                                break;
                            }

                            default: {
                                tempResult.macAccessErrorCode = macAccessError.OperationFailed;
                                break;
                            }
                        }

                        if (tempResult.backscatterErrorCode != backscatterError.Ok || tempResult.macAccessErrorCode != macAccessError.Ok) {
                            tempResult.result = tagMemoryOpResult.OperationFailed;
                        } else {
                            tempResult.result = tagMemoryOpResult.Ok;
                        }

                        int packLen = ByteUtil.getReverseBytesShort(packet, 2);
                        int readLength = packLen - 0x11;
                        tempResult.readData = new short[readLength / 2];
                        for (int i = 18, j = 0; i < (18 + readLength) && j < tempResult.readData.length; i += 2, j++) {
                            tempResult.readData[j] = ByteUtil.getShort(packet, i);
                        }

                        if (IsAlreadyExistsInAccessResults(resultList, tempResult)) {
                            break;
                        }

                        ReadResult newReadResult = new ReadResult();
                        newReadResult.epc = tempResult.epc.clone();
                        newReadResult.readData = tempResult.readData.clone();
                        newReadResult.backscatterErrorCode = tempResult.backscatterErrorCode;
                        newReadResult.macAccessErrorCode = tempResult.macAccessErrorCode;
                        newReadResult.result = tempResult.result;

                        resultList.add(newReadResult);
                        break;
                    }

                    default:
                        break;
                }
            } else {
                retryCount++;

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } while (retryCount < 30);

        return resultList;
    }

    private boolean IsAlreadyExistsInAccessResults(List<ReadResult> readResults, ReadResult tag) {
        boolean isAlreadyExists = false;
        for (int i = 0; i < readResults.size(); i++) {
            if (readResults.get(i).getFlagID() == tag.getFlagID()) {
                isAlreadyExists = true;
                if (readResults.get(i).result == tagMemoryOpResult.OperationFailed && tag.result == tagMemoryOpResult.Ok) {
                    readResults.get(i).readData = tag.readData.clone();
                    readResults.get(i).backscatterErrorCode = backscatterError.Ok;
                    readResults.get(i).macAccessErrorCode = macAccessError.Ok;
                    readResults.get(i).result = tagMemoryOpResult.Ok;
                }

                break;
            }
        }

        return isAlreadyExists;
    }

    private byte GetPermissionWithMemoryBank(MemoryPermission permission) {
        if (permission == MemoryPermission.Writeable) {
            return 0;
        } else if (permission == MemoryPermission.AlwaysWriteable) {
            return 1;
        } else if (permission == MemoryPermission.SecuredWriteable) {
            return 2;
        } else if (permission == MemoryPermission.AlwaysNotWriteable) {
            return 3;
        } else if (permission == MemoryPermission.NoChange) {
            return 4;
        } else {
            return (byte) 0xFF;
        }
    }

    private byte GetPermissionWithPassword(PasswordPermission permission) {
        if (permission == PasswordPermission.Accessible) {
            return 0;
        } else if (permission == PasswordPermission.AlwaysAccessible) {
            return 1;
        } else if (permission == PasswordPermission.SecuredAccessible) {
            return 2;
        } else if (permission == PasswordPermission.AlwaysNotAccessible) {
            return 3;
        } else if (permission == PasswordPermission.NoChange) {
            return 4;
        } else {
            return (byte) 0xFF;
        }
    }

    public boolean IsConnented() {
        return isConnent;
    }

    public boolean IsInventory() {
        return isInventory;
    }

    private final String GPIO_DEV = "/sys/GPIO/GPIO13/value";
    private final byte[] UP = {'1'};
    private final byte[] DOWN = {'0'};

    private void setUpGpio() throws IOException {
//        FileOutputStream fw = new FileOutputStream(GPIO_DEV);
//        fw.write(UP);
//        fw.close();
        try {
                /* Missing read/write permission, trying to chmod the file */
            Process su;
            su = Runtime.getRuntime().exec("sh");
            String cmd = "echo off > /sys/devices/soc.0/xt_dev.68/xt_gpio_112\n";
            su.getOutputStream().write(cmd.getBytes());
            cmd = "echo 1 > /sys/devices/soc.0/xt_dev.68/xt_dc_in_en\n";
            su.getOutputStream().write(cmd.getBytes());
            cmd = "echo 1 > /sys/devices/soc.0/xt_dev.68/xt_vbat_out_en \n" + "exit\n";
            su.getOutputStream().write(cmd.getBytes());

//             su = Runtime.getRuntime().exec("sh");
//             String cmd = "echo off  > /proc/gpio130_ctl\n";
//             su.getOutputStream().write(cmd.getBytes());
//             cmd = "echo on > /proc/uart3_3v3_en\n";
//             su.getOutputStream().write(cmd.getBytes());
//             cmd = "echo on > /proc/usb_dc_en \n" + "exit\n";
//             su.getOutputStream().write(cmd.getBytes());

//             su = Runtime.getRuntime().exec("sh");
//             String cmd = "echo on > /proc/uart3_3v3_en\n";
//             su.getOutputStream().write(cmd.getBytes());
//             cmd = "echo on > /proc/vbat_en\n";
//             su.getOutputStream().write(cmd.getBytes());
//             cmd = "echo on > /proc/gpio129_ctl\n" + "exit\n";
//             su.getOutputStream().write(cmd.getBytes());

            if ((su.waitFor() != 0)) {
                throw new SecurityException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException();
        }
    }

    private void setDownGpio() throws IOException {
        try {
              /* Missing read/write permission, trying to chmod the file */
            Process su;
            su = Runtime.getRuntime().exec("sh");
            String cmd = "echo 0 > /sys/devices/soc.0/xt_dev.68/xt_vbat_out_en \n";
            su.getOutputStream().write(cmd.getBytes());
            cmd = "echo 0 > /sys/devices/soc.0/xt_dev.68/xt_dc_in_en\n";
            su.getOutputStream().write(cmd.getBytes());
            cmd =  "echo on > /sys/devices/soc.0/xt_dev.68/xt_gpio_112\n"+ "exit\n";
            su.getOutputStream().write(cmd.getBytes());

//              su = Runtime.getRuntime().exec("sh");
//              String cmd = "echo off > /proc/usb_dc_en \n" ;
//              su.getOutputStream().write(cmd.getBytes());
//              cmd = "echo off > /proc/uart3_3v3_en\n";
//              su.getOutputStream().write(cmd.getBytes());
//              cmd = "echo on  > /proc/gpio130_ctl\n" + "exit\n";
//              su.getOutputStream().write(cmd.getBytes());

//              su = Runtime.getRuntime().exec("sh");
//              String cmd = "echo off > /proc/uart3_3v3_en\n" ;
//              su.getOutputStream().write(cmd.getBytes());
//              cmd = "echo off > /proc/gpio129_ctl\n" + "exit\n";
//              su.getOutputStream().write(cmd.getBytes());

            if ((su.waitFor() != 0)) {
                throw new SecurityException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException();
        }
    }

    public ctrlOperateResult ConnectRadio(String serialPort, int baudRate) throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;
        isBusy = true;

        try {
            setUpGpio();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            isSuccess = ctrlOperateResult.ERROR;
            e.printStackTrace();
        } catch (SecurityException e) {
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
            e.printStackTrace();
            isBusy = false;
            return isSuccess;

        }

        try {
            Thread.sleep(400);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (mSerialPort == null) {
                mSerialPort = new SerialPort(new File(serialPort), baudRate, 0);
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
                stopRetrieve = false;
                recvThread = new RetrievePacketThread();
                recvThread.start();
            }

            byte[] buffer = commonFun.MakePacket(null, commonFun.HostPacketTypes.HOST_PACKET_TYPE_GETSN_COMMAND);

            try {
                synchronized (mOutputStream) {
                    mOutputStream.write(buffer);
                    mOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.GetFullPackBuf();

                if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GETSN) {
                    isConnent = true;
                    isSuccess = ctrlOperateResult.OK;
                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 10);
        } catch (SecurityException e) {
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        } catch (IOException e) {
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    public ctrlOperateResult DisconnectRadio() throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        stopRetrieve = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.OK;

        if (mSerialPort != null) {
            synchronized (mOutputStream) {
                synchronized (mInputStream) {
                    if (mSerialPort != null) {
                        mSerialPort.close();
                        mSerialPort = null;
                        mOutputStream = null;
                        mInputStream = null;
                        isConnent = false;
                    }
                }
            }
        }

        if (recvThread != null) {
            recvThread.interrupt();
            recvThread = null;
        }

        try {
            setDownGpio();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            isSuccess = ctrlOperateResult.ERROR;
            e.printStackTrace();
        } catch (SecurityException e) {
            isSuccess = ctrlOperateResult.ERROR;
            e.printStackTrace();
        }

        isBusy = false;
        return isSuccess;
    }

    public ctrlOperateResult StartInventory(int inventoryMode, int intervalTick) throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.OK;
        invMode = inventoryMode;
        interval = intervalTick;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[2];
                buffer[0] = 0x00;
                buffer[1] = (byte) invMode;
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_INVENTORY_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                stopInventory = false;
                isInventory = true;
                invThread = new InventoryThread();
                invThread.start();
            } else {
                isBusy = false;
                isSuccess = ctrlOperateResult.ERROR;
            }
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        return isSuccess;
    }

    public ctrlOperateResult StopInventory() {
        ctrlOperateResult isSuccess = ctrlOperateResult.OK;

        if (isInventory) {
            try {
                stopInventory = true;
                if (mSerialPort != null) {
                    byte[] buffer = commonFun.MakePacket(null, commonFun.HostPacketTypes.HOST_PACKET_TYPE_CANCEL_COMMAND);
                    try {
                        synchronized (mOutputStream) {
                            mOutputStream.write(buffer);
                            mOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    isSuccess = ctrlOperateResult.ERROR;
                }
            } catch (SecurityException e) {
                stopInventory = true;
                isSuccess = ctrlOperateResult.SERIALPORTERROR;
            }
            
            /*int tryCount = 0;
            while(isInventory && tryCount < 10)
            {
            	try
				{
					Thread.sleep(100);
				} catch(Exception e)
				{
					e.printStackTrace();
				}
            	
            	tryCount++;
            }*/
        }

        return isSuccess;
    }

    public antennaPortState GetAntennaPortStatus(int antennaID) throws radioBusyException {
        antennaPortState portState = antennaPortState.UNKNOWN;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[1];
                buffer[0] = (byte) antennaID;
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_ANTENNA_STATUS_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_ANTENNA_STATUS) {
                        if (packet[7] == 0x01) {
                            portState = packet[8] == 0x00 ? antennaPortState.DISABLED : antennaPortState.ENABLED;
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return portState;
    }

    public ctrlOperateResult SetAntennaPortState(int antennaID, antennaPortState portState) throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[2];
                buffer[0] = (byte) antennaID;
                buffer[1] = (byte) (portState == antennaPortState.ENABLED ? 0x01 : 0x00);
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_ANTENNA_STATUS_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_ANTENNA_STATUS) {
                        if (packet[7] == 0x01) {
                            isSuccess = ctrlOperateResult.OK;
                        } else {
                            isSuccess = ctrlOperateResult.ERROR;
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
                isSuccess = ctrlOperateResult.SERIALPORTERROR;
            }
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    public AntennaPortConfiguration GetAntennaPortConfiguration(int antennaID) throws radioBusyException, radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        AntennaPortConfiguration portconfiguration = null;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[1];
                buffer[0] = (byte) antennaID;
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_ANTENNA_PARAM_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_ANTENNA_PARAM) {
                        if (packet[7] == 0x01) {
                            portconfiguration = new AntennaPortConfiguration();
                            portconfiguration.powerLevel = ByteUtil.getReverseBytesInt(packet, 8);
                            portconfiguration.dwellTime = ByteUtil.getReverseBytesInt(packet, 12);
                            portconfiguration.numberInventoryCycles = ByteUtil.getReverseBytesInt(packet, 16);
                        } else {
                            throw new radioFailException("Get antenna configuration failed");
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return portconfiguration;
    }

    public ctrlOperateResult SetAntennaPortConfiguration(int antennaID, AntennaPortConfiguration antennaPortConfiguration) throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[13];
                buffer[0] = (byte) antennaID;
                ByteUtil.putReverseBytesInt(buffer, antennaPortConfiguration.powerLevel, 1);
                ByteUtil.putReverseBytesInt(buffer, antennaPortConfiguration.dwellTime, 5);
                ByteUtil.putReverseBytesInt(buffer, antennaPortConfiguration.numberInventoryCycles, 9);
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_ANTENNA_PARAM_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_ANTENNA_PARAM) {
                        if (packet[7] == 0x01) {
                            isSuccess = ctrlOperateResult.OK;
                        } else {
                            isSuccess = ctrlOperateResult.ERROR;
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
                isSuccess = ctrlOperateResult.SERIALPORTERROR;
            }
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    public int GetCurrentLinkProfile() throws radioBusyException, radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        int profile = -1;

        try {
            if (mSerialPort != null) {
                byte[] buffer = commonFun.MakePacket(null, commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_LINK_PROFILE_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_LINK_PROFILE) {
                        if (packet[7] == 0x01) {
                            profile = packet[8];
                        } else {
                            throw new radioFailException("Get profile index failed");
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return profile;
    }

    public ctrlOperateResult SetCurrentLinkProfile(int profile) throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            if (mSerialPort != null) {
                isBusy = false;
                try {
                    GetAntennaSWR(0, 100);
                } catch (Exception e) {
                }
                isBusy = true;

                byte[] buffer = new byte[1];
                buffer[0] = (byte) profile;
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_LINK_PROFILE_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_LINK_PROFILE) {
                        if (packet[7] == 0x01) {
                            isSuccess = ctrlOperateResult.OK;
                        } else {
                            isSuccess = ctrlOperateResult.ERROR;
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
                isSuccess = ctrlOperateResult.SERIALPORTERROR;
            }
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    public Session GetTagGroupSession() throws radioBusyException, radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        Session session = Session.UNKNOWN;

        try {
            if (mSerialPort != null) {
                byte[] buffer = commonFun.MakePacket(null, commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_GROUP_SESSION_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_GROUP_SESSION) {
                        if (packet[7] == 0x01) {
                            switch (packet[8]) {
                                case 0x00: {
                                    session = Session.S0;
                                    break;
                                }

                                case 0x01: {
                                    session = Session.S1;
                                    break;
                                }

                                case 0x02: {
                                    session = Session.S2;
                                    break;
                                }

                                case 0x03: {
                                    session = Session.S3;
                                    break;
                                }

                                default: {
                                    break;
                                }
                            }
                        } else {
                            throw new radioFailException("Get group session failed");
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return session;
    }

    public ctrlOperateResult SetTagGroupSession(Session session) throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[1];
                if (session == Session.S0) {
                    buffer[0] = (byte) 0x00;
                } else if (session == Session.S1) {
                    buffer[0] = (byte) 0x01;
                } else if (session == Session.S2) {
                    buffer[0] = (byte) 0x02;
                } else if (session == Session.S3) {
                    buffer[0] = (byte) 0x03;
                } else {
                    isBusy = false;
                    return isSuccess;
                }

                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_GROUP_SESSION_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_GROUP_SESSION) {
                        if (packet[7] == 0x01) {
                            isSuccess = ctrlOperateResult.OK;
                        } else {
                            isSuccess = ctrlOperateResult.ERROR;
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
                isSuccess = ctrlOperateResult.SERIALPORTERROR;
            }
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    public SingulationAlgorithmParms GetCurrentSingulationAlgorithm() throws radioBusyException, radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        SingulationAlgorithmParms parm = null;

        try {
            if (mSerialPort != null) {
                byte[] buffer = commonFun.MakePacket(null, commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_SINGULATION_ALGORITHM_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_SINGULATION_ALGORITHM) {
                        if (packet[7] == 0x01) {
                            parm = new SingulationAlgorithmParms();
                            parm.qValue = (int) packet[9];
                            parm.startQValue = (int) packet[9];
                            parm.retryCount = (int) packet[10];
                            parm.toggleTarget = (int) packet[11];
                            parm.repeatUntilNoTags = (int) packet[12];
                            parm.minQValue = (int) packet[13];
                            parm.maxQValue = (int) packet[14];
                            parm.thresholdMultiplier = (int) packet[15];
                            switch (packet[8]) {
                                case 0x00: {
                                    parm.singulationAlgorithmType = SingulationAlgorithm.FIXEDQ;
                                    break;
                                }

                                case 0x01: {
                                    parm.singulationAlgorithmType = SingulationAlgorithm.DYNAMICQ;
                                    break;
                                }

                                default: {
                                    parm.singulationAlgorithmType = SingulationAlgorithm.UNKNOWN;
                                    break;
                                }
                            }
                        } else {
                            throw new radioFailException("Get SingulationAlgorithm failed");
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return parm;
    }

    public ctrlOperateResult SetCurrentSingulationAlgorithm(SingulationAlgorithmParms singulationAlgoithmParms) throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[8];
                if (singulationAlgoithmParms.singulationAlgorithmType == SingulationAlgorithm.FIXEDQ) {
                    buffer[0] = (byte) 0x00;
                } else if (singulationAlgoithmParms.singulationAlgorithmType == SingulationAlgorithm.DYNAMICQ) {
                    buffer[0] = (byte) 0x01;
                } else {
                    isBusy = false;
                    return isSuccess;
                }

                buffer[1] = (byte) (singulationAlgoithmParms.singulationAlgorithmType == SingulationAlgorithm.FIXEDQ ? singulationAlgoithmParms.qValue : singulationAlgoithmParms.startQValue);
                buffer[2] = (byte) singulationAlgoithmParms.retryCount;
                buffer[3] = (byte) singulationAlgoithmParms.toggleTarget;
                buffer[4] = (byte) singulationAlgoithmParms.repeatUntilNoTags;
                buffer[5] = (byte) singulationAlgoithmParms.minQValue;
                buffer[6] = (byte) singulationAlgoithmParms.maxQValue;
                buffer[7] = (byte) singulationAlgoithmParms.thresholdMultiplier;

                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_SINGULATION_ALGORITHM_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_SINGULATION_ALGORITHM) {
                        if (packet[7] == 0x01) {
                            isSuccess = ctrlOperateResult.OK;
                        } else {
                            isSuccess = ctrlOperateResult.ERROR;
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
                isSuccess = ctrlOperateResult.SERIALPORTERROR;
            }
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    public ctrlOperateResult RadioReset() throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            if (mSerialPort != null) {
                byte[] buffer = commonFun.MakePacket(null, commonFun.HostPacketTypes.HOST_PACKET_TYPE_RESET_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_RESET) {
                        if (packet[7] == 0x01) {
                            isSuccess = ctrlOperateResult.OK;
                        } else {
                            isSuccess = ctrlOperateResult.ERROR;
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
                isSuccess = ctrlOperateResult.SERIALPORTERROR;
            }
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    public float GetAntennaSWR(int antennaID, int power) throws radioBusyException, radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        float antennaSWR = 100;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[5];
                buffer[0] = (byte) antennaID;
                ByteUtil.putReverseBytesInt(buffer, power, 1);
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_SWR_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SWR) {
                        if (packet[7] == 0x01) {
                            antennaSWR = (float) (ByteUtil.getReverseBytesInt(packet, 8) / 100.0);
                        } else {
                            throw new radioFailException("Get SWR failed");
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return antennaSWR;
    }

    public void WaveCtrlOff(int antennaID) throws radioBusyException, radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[6];
                ByteUtil.putReverseBytesShort(buffer, (short) 0xF000, 0);
                ByteUtil.putReverseBytesInt(buffer, 0x18, 2);
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_REGISTER_WRITE_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_END) {
                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return;
    }

    public List<TagOperResult> TagInfoWrite(WriteParms writeParms, short[] writeBuf) throws radioBusyException, radioFailException {
        List<TagOperResult> tagOperResults = null;

        if (writeParms.length != writeBuf.length) {
            throw new radioFailException("Write data length error");
        }

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[9 + writeParms.length * 2];
                buffer[0] = (byte) (writeParms.memBank == MemoryBank.Reserved ? 0x00 : (writeParms.memBank == MemoryBank.EPC ? 0x01 : (writeParms.memBank == MemoryBank.TID ? 0x02 : (writeParms.memBank == MemoryBank.USER ? 0x03 : 0x00))));
                ByteUtil.putReverseBytesShort(buffer, writeParms.offset, 1);
                ByteUtil.putReverseBytesShort(buffer, writeParms.length, 3);
                ByteUtil.putReverseBytesInt(buffer, writeParms.accesspassword, 5);
                int bufOffset = 9;

                for (int i = 0; i < writeBuf.length; i++) {
                    ByteUtil.putReverseBytesShort(buffer, writeBuf[i], bufOffset);
                    bufOffset += 2;
                }

                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_WRITE_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    tagOperResults = TagMemoryOperate();
                } catch (radioFailException e) {
                    throw new radioFailException("RFID module write fail");
                }
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return tagOperResults;
    }

    public List<ReadResult> TagInfoRead(ReadParms readParms) throws radioBusyException, radioFailException {
        List<ReadResult> readResults = null;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[11];

                buffer[0] = (byte) (readParms.memBank == MemoryBank.Reserved ? 0x00 : (readParms.memBank == MemoryBank.EPC ? 0x01 : (readParms.memBank == MemoryBank.TID ? 0x02 : (readParms.memBank == MemoryBank.USER ? 0x03 : 0x00))));
                ByteUtil.putReverseBytesShort(buffer, readParms.offset, 1);
                ByteUtil.putReverseBytesShort(buffer, readParms.length, 3);
                ByteUtil.putReverseBytesInt(buffer, readParms.accesspassword, 5);
                buffer[9] = 0;
                buffer[10] = 0;
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_READ_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    readResults = TagAccessOperate();
                } catch (radioFailException e) {
                    throw new radioFailException("RFID module write fail");
                }
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return readResults;
    }

    public List<TagOperResult> TagLock(LockParms lockParms, int accessPassword) throws radioBusyException, radioFailException {
        List<TagOperResult> tagOperResults = null;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[9];
                buffer[0] = GetPermissionWithPassword(lockParms.killPasswordPermission);
                buffer[1] = GetPermissionWithPassword(lockParms.accessPasswordPermission);
                buffer[2] = GetPermissionWithMemoryBank(lockParms.EPCMemoryBankPermissions);
                buffer[3] = GetPermissionWithMemoryBank(lockParms.TIDMemoryBankPermissions);
                buffer[4] = GetPermissionWithMemoryBank(lockParms.USERMemoryBankPermissions);
                ByteUtil.putReverseBytesInt(buffer, accessPassword, 5);
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_LOCK_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    tagOperResults = TagMemoryOperate();
                } catch (radioFailException e) {
                    throw new radioFailException("RFID module tag lock fail");
                }
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return tagOperResults;
    }

    public List<TagOperResult> TagKill(int accessPassword, int killPassword) throws radioBusyException, radioFailException {
        List<TagOperResult> tagOperResults = null;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[8];
                ByteUtil.putReverseBytesInt(buffer, accessPassword, 0);
                ByteUtil.putReverseBytesInt(buffer, killPassword, 4);
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_KILL_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    tagOperResults = TagMemoryOperate();
                } catch (radioFailException e) {
                    throw new radioFailException("RFID module tag lock fail");
                }
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return tagOperResults;
    }

    public List<PasswordResult> GetTagPassword(PasswordType type, int accessPassword) throws radioBusyException, radioFailException {
        List<PasswordResult> passwordValues = null;
        List<ReadResult> readResults = null;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[5];
                buffer[0] = (byte) (type == PasswordType.AccessPassword ? 0x00 : 0x01);
                ByteUtil.putReverseBytesInt(buffer, accessPassword, 1);
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_GET_PASSWORD_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    readResults = TagAccessOperate();
                    if (readResults != null) {
                        passwordValues = new ArrayList<PasswordResult>();
                        for (int i = 0; i < readResults.size(); i++) {
                            PasswordResult newPassword = new PasswordResult();
                            newPassword.epc = readResults.get(i).epc.clone();
                            newPassword.accessPasswordValue = (int) ((readResults.get(i).readData[0] & 0x0000FFFF) << 16) + (readResults.get(i).readData[1] & 0x0000FFFF);
                            newPassword.backscatterErrorCode = readResults.get(i).backscatterErrorCode;
                            newPassword.macAccessErrorCode = readResults.get(i).macAccessErrorCode;
                            newPassword.result = readResults.get(i).result;
                            passwordValues.add(newPassword);
                        }
                    }
                } catch (radioFailException e) {
                    throw new radioFailException("RFID module write fail");
                }
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return passwordValues;
    }

    public List<TagOperResult> ModifyTagPassword(PasswordType type, int accessPassword, int newPassword) throws radioBusyException, radioFailException {
        List<TagOperResult> tagOperResults = null;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            if (mSerialPort != null) {
                byte[] buffer = new byte[9];
                buffer[0] = (byte) (type == PasswordType.AccessPassword ? 0x00 : 0x01);
                ByteUtil.putReverseBytesInt(buffer, accessPassword, 1);
                ByteUtil.putReverseBytesInt(buffer, newPassword, 5);
                buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_SET_PASSWORD_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    tagOperResults = TagMemoryOperate();
                } catch (radioFailException e) {
                    throw new radioFailException("RFID module write fail");
                }
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return tagOperResults;
    }

    public SingulationCriteria Get18K6CPostMatchCriteria() throws radioBusyException, radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        SingulationCriteria criteria = new SingulationCriteria();

        try {
            if (mSerialPort != null) {
                byte[] buffer = commonFun.MakePacket(null, commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_MASK_SETTING_COMMAND);

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_MASK_SETTING) {
                        if (packet[7] == 0x01) {
                            criteria.status = packet[8] == 0x01 ? SingulationCriteriaStatus.Enabled : SingulationCriteriaStatus.Disabled;
                            if (criteria.status == SingulationCriteriaStatus.Enabled) {
                                criteria.match = packet[9] == 0x00 ? matchType.Inverse : matchType.Regular;
                                criteria.offset = ByteUtil.getReverseBytesInt(packet, 10);
                                criteria.count = ByteUtil.getReverseBytesInt(packet, 14);
                                for (int i = 0; i < criteria.mask.length; i++) {
                                    criteria.mask[i] = packet[18 + i];
                                }
                            }
                        } else {
                            throw new radioFailException("Get SWR failed");
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return criteria;
    }


    public ctrlOperateResult Set18K6CPostMatchCriteria(SingulationCriteria criteria) throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            if (mSerialPort != null) {
                byte[] buffer = null;
                if (criteria.status == SingulationCriteriaStatus.Enabled) {
                    buffer = new byte[71];
                    buffer[0] = (byte) (criteria.match == matchType.Inverse ? 0x00 : 0x01);
                    ByteUtil.putReverseBytesInt(buffer, criteria.offset, 1);
                    ByteUtil.putReverseBytesInt(buffer, criteria.count, 5);
                    System.arraycopy(criteria.mask, 0, buffer, 9, criteria.mask.length);

                    buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_MASK_ENABLE_COMMAND);
                } else {
                    buffer = commonFun.MakePacket(buffer, commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_MASK_DISABLE_COMMAND);
                }

                try {
                    synchronized (mOutputStream) {
                        mOutputStream.write(buffer);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int retryCount = 0;
                do {
                    byte[] packet = recvBuffer.GetFullPackBuf();
                    if (packet != null && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_MASK) {
                        if (packet[7] == 0x01) {
                            isSuccess = ctrlOperateResult.OK;
                        } else {
                            isSuccess = ctrlOperateResult.ERROR;
                        }

                        break;
                    } else {
                        retryCount++;
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (retryCount < 10);
            } else {
                isBusy = false;
                isSuccess = ctrlOperateResult.SERIALPORTERROR;
            }
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }
}
