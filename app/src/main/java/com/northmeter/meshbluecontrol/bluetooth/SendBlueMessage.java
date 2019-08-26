package com.northmeter.meshbluecontrol.bluetooth;


import com.jmesh.blebase.base.BleManager;
import com.jmesh.blebase.state.BleDevice;
import com.northmeter.meshbluecontrol.I.ISendBlueMessage;
import com.northmeter.meshbluecontrol.I.I_ShowBlueSend;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools.BluetoothConnectionClient;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

/**
 * Created by dyd
 * 2017/5/18
 */
public class SendBlueMessage implements ISendBlueMessage {
    private BluetoothConnectionClient mConnectionClient;

    private I_ShowBlueSend showMessage;
    public SendBlueMessage(I_ShowBlueSend showMessage){
        this.showMessage = showMessage;
    }
//    @Override
//    public void sendBlueMessage(String para) {
//        mConnectionClient = BlueTooth_ConnectHelper.getInstance().getConnectionClient();
//        boolean connected = BlueTooth_ConnectHelper.getInstance().isBooleanConnected();
//        if(mConnectionClient==null||connected == false){
//            showMessage.showBlueSendMsg(1);
//        }else{
//            final String input = para;
//            new Thread(){
//                public void run(){
//                    mConnectionClient.write(4, Udp_Help.strtoByteArray(input));
//                }
//            }.start();
//            showMessage.showBlueSendMsg(0);
//        }
//    }
    @Override
    public void sendBlueMessage(String para) {
        BleConnect_InstanceHelper bleConnect = BleConnect_InstanceHelper.getInstance();
        BleDevice bleDevice = BleManager.getInstance().getConnectedDeviceByMac(bleConnect.getMacStr());
        if (bleDevice == null) {
            showMessage.showBlueSendMsg(1);
            return;
        }else{
            bleConnect.send(subPackage(para));
            showMessage.showBlueSendMsg(0);
        }
    }

    /**
     * 数据分包
     * */
    public static String subPackage(String data){
        int length = Integer.valueOf(Udp_Help.getLength_1(data),16);
        return Integer.toHexString(length+1)+"05"+data;
    }

}
