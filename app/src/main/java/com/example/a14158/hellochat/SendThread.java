package com.example.a14158.hellochat;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by 14158 on 2017/12/18.
 */

class SendThread implements Runnable {
    private Socket socket;
    private String input;
    public SendThread(Socket socket, String input) {
        this.input = input;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Log.d("SendThread","send");
            //socket = new Socket("10.116.32.181",9999);
            socket = new Socket();
            socket.connect(new InetSocketAddress("192.168.137.190",3601),10000);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(input.getBytes());
        } catch (SocketTimeoutException e){
            Log.d("SendThread","服务器连接失败 "+e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d("SendThread",e.getMessage());
        }
    }
}
