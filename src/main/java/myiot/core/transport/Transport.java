package myiot.core.transport;

/**
 * Created by aron on 2016/12/16.
 */
public interface  Transport {
    public Object read();
    public boolean write(Object o);

}
