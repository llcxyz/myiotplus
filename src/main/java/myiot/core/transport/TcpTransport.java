package myiot.core.transport;

import myiot.core.util.IotConfig;

/**
 * Created by aron on 2016/12/17.
 */
public class TcpTransport implements Transport{


    public TcpTransport(IotConfig config){

    }
    @Override
    public Object read() {
        return null;
    }

    @Override
    public boolean write(Object o) {
        return false;
    }

}
