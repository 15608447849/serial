package lora;

import java.io.UnsupportedEncodingException;

/**
 * Created by user on 2017/12/21.
 */
public class TypesUtils {

    //需要使用2字节表示
    public static String numToHex(int b) {
        return String.format("%02x", b).toUpperCase();
    }
    /*
    * 将字符串编码成16进制数字,适用于所有字符（包括中文）
    */
    public static String stringToHex(String str) throws UnsupportedEncodingException {
        final String hexString="0123456789ABCDEF";
        //根据默认编码获取字节数组
        byte[] bytes=str.getBytes("GBK");
        StringBuilder sb=new StringBuilder(bytes.length*2);
        //将字节数组中每个字节拆解成2位16进制整数
        for(int i=0;i<bytes.length;i++)
        {
            sb.append(hexString.charAt((bytes[i]&0xf0)>>4));//取高四位
            sb.append(hexString.charAt((bytes[i]&0x0f)>>0));//取低四位
        }
        return sb.toString();
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
}
