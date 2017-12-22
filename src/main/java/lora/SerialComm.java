package lora;

import gnu.io.*;
import jdk.internal.dynalink.support.TypeUtilities;

import java.awt.font.FontRenderContext;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static lora.TypesUtils.numToHex;
import static lora.TypesUtils.stringToHex;
import static lora.TypesUtils.toBytes;

/**
 * Created by user on 2017/12/21.
 */
public class SerialComm implements SerialCommIts {



    private class Params{
        final static String pattern = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        String commName;
        SerialPort serialPort;
    }


    private final Params params = new Params();

    private void check(String fname) throws Exception{
        InputStream in;
        FileOutputStream fos;
        File file = new File("C:\\Windows\\System32\\"+fname);
        if (!file.exists()){
            in = this.getClass().getClassLoader().getResourceAsStream(fname);
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len;
            while((len=in.read(b))!=-1){
                fos.write(b, 0, len);
            }
            in.close();
            fos.close();
        }
    }

    private SerialComm(){
        try {
            check("rxtxParallel.dll");
            check("rxtxSerial.dll");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    private static class Holder{
        private static final SerialComm INSTANDT = new SerialComm();
    }

    public static final SerialComm get(){
        return Holder.INSTANDT;
    }

    /**
     * 查找所有可用端口
     * @return 可用端口名称列表
     */
    private final ArrayList<String> findPort() {
        //获得当前所有可用串口
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();

        ArrayList<String> portNameList = new ArrayList<>();

        //将可用串口名添加到List并返回该List
        CommPortIdentifier it;
        while (portList.hasMoreElements()) {
            it = portList.nextElement();
            portNameList.add(it.getName());
        }

        return portNameList;

    }




    @Override
    public boolean setComPortName(String commName) {

        ArrayList<String> portList = findPort();
        System.out.println(portList);
        if (portList.contains(commName)){
            params.commName = commName;
            return true;
        }
        return false;
    }

    @Override
    public boolean open() {
        try {
            if (params.serialPort!=null) close();
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(params.commName);
            CommPort commPort = portIdentifier.open(params.commName, 0);
            SerialPort serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            params.serialPort = serialPort;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean sendMessage(String address, String message) {

        try {
            //判断地址格式   xx.xx.xx.xx
            Pattern r = Pattern.compile(params.pattern);
            Matcher m = r.matcher(address);
            if (!m.find( )) {
                throw new IllegalArgumentException("the address " + address+" format is error.");
            }
            //判断字符串长度 小于 24
            if (message.length()>24){
                throw new IllegalArgumentException("the message length " + message.length() +" > 24 .");
            }
            //String -> byte[]
            int[] addArray = new int[4];
            String[] addStrs = address.split("\\.");
            for (int i = 0;i<4;i++){
                addArray[i] = Integer.parseInt(addStrs[i]);
            }
            /*协议
            *  1b   1b  4b  1b  1b  10b     48b 1b
            *  5A   00  00~FF(*4) E1 3A 00(*10) 00~FF(*48) CRC(前面所有字节求和取第八位)
            * */
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("5A");
            sBuilder.append("00");
            for (int num:addArray){
                sBuilder.append(numToHex(num));
            }
            sBuilder.append("E1");
            sBuilder.append("3A");
            sBuilder.append("000000000A00000B0000");
            String messageHex = stringToHex(message);
            sBuilder.append(messageHex);
            if (messageHex.length()<96){
                for (int i=messageHex.length();i<96;i++){
                    sBuilder.append("0");
                }
            }
                //计算校验和  取所有内容总和,取低8位
            int number = 0;
            char[] arr = new char[2];
            for (int i = 0;i<sBuilder.length();i+=2){
                arr[0] = sBuilder.charAt(i);
                arr[1] = sBuilder.charAt(i+1);
                number+=Integer.parseInt(new String(arr),16);
            }
            number = number&0xFF;
            sBuilder.append(numToHex(number));

            System.out.println("发送消息:");
            for (int i=0;i<sBuilder.length();i+=2){
                System.out.print(sBuilder.charAt(i)+""+sBuilder.charAt(i+1)+" ");
            }
            System.out.println();
            byte[] bytes = toBytes(sBuilder.toString());
            System.out.println(Arrays.toString(bytes));
            write(bytes);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void write(byte[] bytes) throws Exception {
        if (params.serialPort==null) return;
        OutputStream out = null;

        try {
            out = params.serialPort.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();
            out=null;
        }  finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }
    }


    /**
     * 关闭串口
     */
    @Override
    public void close() {

        if (params.serialPort != null) {
            params.serialPort.close();
            params.serialPort = null;
        }
    }



}
