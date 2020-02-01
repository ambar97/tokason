package com.pratamatechnocraft.tokason.BluetoothPrinter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Martin Forejt on 28.06.2017.
 * forejt.martin97@gmail.com
 */

public class BluetoothPrinter {

    public static final int ALIGN_CENTER = 100;
    public static final int ALIGN_RIGHT = 101;
    public static final int ALIGN_LEFT = 102;

    private static final byte[] NEW_LINE = {10};
    private static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b, 'a', 0x01};
    private static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b, 'a', 0x02};
    private static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b, 'a', 0x00};

    private BluetoothDevice printer;
    private BluetoothSocket btSocket = null;
    private OutputStream btOutputStream = null;
    public BluetoothPrinter(BluetoothDevice printer) {
        this.printer = printer;
    }

    public void connectPrinter(final PrinterConnectListener listener) {
        new ConnectTask(new ConnectTask.BtConnectListener() {
            @Override
            public void onConnected(BluetoothSocket socket) {
                btSocket = socket;
                try {
                    btOutputStream = socket.getOutputStream();

                    listener.onConnected();
                } catch (IOException e) {
                    listener.onFailed();
                }
            }

            @Override
            public void onFailed() {
                listener.onFailed();
            }
        }).execute(printer);
    }

    public boolean isConnected() {
        return btSocket != null && btSocket.isConnected();
    }

    public void finish() {
        if (btSocket != null) {
            try {
                btOutputStream.close();
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btSocket = null;
        }
    }

    private static class ConnectTask extends AsyncTask<BluetoothDevice, Void, BluetoothSocket> {
        private BtConnectListener listener;

        private ConnectTask(BtConnectListener listener) {
            this.listener = listener;
        }

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
            BluetoothDevice device = bluetoothDevices[0];
            UUID uuid = device.getUuids()[0].getUuid();
            BluetoothSocket socket = null;
            boolean connected = true;

            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
            }
            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class})
                            .invoke(device, 1);
                    socket.connect();
                } catch (Exception e2) {
                    connected = false;
                }
            }

            return connected ? socket : null;
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            if (listener != null) {
                if (bluetoothSocket != null) listener.onConnected(bluetoothSocket);
                else listener.onFailed();
            }
        }

        private interface BtConnectListener {
            void onConnected(BluetoothSocket socket);

            void onFailed();
        }
    }

    public interface PrinterConnectListener {
        void onConnected();

        void onFailed();
    }

    public void printHead(String nama, String alamat, String noTelp){
        printCustom(nama,2,1);
        printCustom(alamat,0,1);
//        printCustom("Meyediakan makanan dan minuman ringan di area lingkungan jurusan TI",0,1);
        printCustom(noTelp,0,1);
        printNewLine();
    }

    public boolean printBatas(String ukuranKertas) {
        if (ukuranKertas.equals("58")){
            return printText("================================");
        }else if(ukuranKertas.equals("80")){
            return printText("================================================");
        }else{
            return printText("============================================================================================");
        }
    }

    public boolean printBatasDua(String ukuranKertas) {
        if (ukuranKertas.equals("58")){
            return printText("Sobek ------------------- disini");
        }else if(ukuranKertas.equals("80")){
            return printText("Sobek ----------------------------------- disini");
        }else{
            return printText("Sobek ------------------------------------------------------------------------------- disini");
        }


    }


    public void setAlign(int alignType) {
        byte[] d;
        switch (alignType) {
            case ALIGN_CENTER:
                d = ESC_ALIGN_CENTER;
                break;
            case ALIGN_LEFT:
                d = ESC_ALIGN_LEFT;
                break;
            case ALIGN_RIGHT:
                d = ESC_ALIGN_RIGHT;
                break;
            default:
                d = ESC_ALIGN_LEFT;
                break;
        }

        try {
            btOutputStream.write(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //print custom
    public void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    btOutputStream.write(cc);
                    break;
                case 1:
                    btOutputStream.write(bb);
                    break;
                case 2:
                    btOutputStream.write(bb2);
                    break;
                case 3:
                    btOutputStream.write(bb3);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    btOutputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    btOutputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    btOutputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
                case 3:
                    break;
            }
            btOutputStream.write(msg.getBytes());
            btOutputStream.write(PrinterCommands.LF);
            btOutputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print unicode
    public void printUnicode(){
        try {
            btOutputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //print new line
    public void printNewLine() {
        try {
            btOutputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print text
    public boolean printText(String msg) {
        try {
            // Print normal text
            btOutputStream.write(msg.getBytes());
            btOutputStream.write(PrinterCommands.ESC_CANCEL_BOLD);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    //print byte[]
    public void printText(byte[] msg) {
        try {
            // Print normal text
            btOutputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String leftRightAlign(String str1, String str2, String ukuranKertas) {
        int maxJmlKarakter;
        if (ukuranKertas.equals("58")){
            maxJmlKarakter=32;
        }else if(ukuranKertas.equals("80")){
            maxJmlKarakter=48;
        }else{
            maxJmlKarakter=92;
        }
        String ans = str1 +str2;
        /*if (str1.length()>31){
            String[] split = str1.split("(?!^)");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(split[i]);
                if (i != split.length - 1) {
                    sb.append(" ");
                }
            }
            String str1Satu = sb.toString();
            for (int i = 17; i < str1.length(); i++) {
                sb.append(split[i]);
                if (i != split.length - 1) {
                    sb.append(" ");
                }
            }
            String str1Dua = sb.toString();
            str1= str1Satu+"\n"+str1Dua;
            Log.d("", "STR1SATU: "+str1Satu);
            Log.d("", "STR1DUA: "+str1Dua);
            Log.d("", "STR1: "+str1);
            int n = (32 - (str1Dua.length() + str2.length()));
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
        }else{*/
            if(ans.length() < maxJmlKarakter-1){
                int n = (maxJmlKarakter - (str1.length() + str2.length()));
                ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
            }
        /*}*/
        return ans;
    }


    public String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ c.get(Calendar.MONTH) +"/"+ c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        return dateTime;
    }

    public BluetoothSocket getSocket() {
        return btSocket;
    }

    public BluetoothDevice getDevice() {
        return printer;
    }
}