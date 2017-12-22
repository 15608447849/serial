import lora.SerialComm;

import java.util.Arrays;

import static lora.TypesUtils.stringToHex;

/**
 * Created by user on 2017/12/21.
 */
public class test {
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 方法一：
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexFun1(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for(byte b : bytes) { // 使用除与取余进行转换
            if(b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }

            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }

        return new String(buf);
    }
    public static String bytesToHexFun2(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for(byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
        }

        return new String(buf);
    }
    /**
     * 方法三：
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexFun3(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for(byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }

        return buf.toString();
    }
    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }
    public static void main(String[] args) throws Exception {
//        //C4E3BAC3
//        String s = "你";
//            byte[] b = s.getBytes("GBK");
//        System.out.println(Arrays.toString(b));
////        System.out.println(Arrays.toString(intToByteArray(i)));
////        System.out.println(bytesToHexFun1(intToByteArray(i)));
////        System.out.println(bytesToHexFun2(intToByteArray(i)));
//        System.out.println(bytesToHexFun3(b));
//        System.out.println(stringToHex("你"));
//
//        System.out.println(Arrays.toString(toBytes("C4E3")));
//
//
//        String s1 = "你你你你你你你你你你你你你你你你你你你你你你你你";
//        System.out.println(s1.getBytes("gbk").length);
//        System.out.println(Arrays.toString(intToByteArray(48)));

//            SerialComm serialComm = SerialComm.get();
//            boolean f = serialComm.setComPortName("COM12");
//            if (f){
//                System.out.println("设置串口,ok");
//                f = serialComm.open();
//                if (f){
//                    System.out.println("打开串口,ok");
//                    f = serialComm.sendMessage("255.255.255.255","111111111111111111111111");
//                    if (f){
//                        System.out.println("发送消息,ok");
//                        serialComm.close();//关闭串口
//                    }
//                    serialComm.close();
//                }
//            }


    }
}
