package lora;

/**
 * Created by user on 2017/12/21.
 */
public interface SerialCommIts {
    //设置串口
    boolean setComPortName(String commName);
    //打开
    boolean open();
    //想指定地址发送消息
    boolean sendMessage(String address,String message);
    //关闭
    void close();
}
