package myiot.core.transport;

import myiot.core.util.IotConfig;

/**
 * Created by aron on 2016/12/19.
 */
public class TransportFactory {

    public static Transport create(IotConfig config){
        String type=(String)config.getParams("type","zmq");
        if(type.equals("zmq")){
            return new ZmqTransport(config);
        }
        else if(type.equals("tcp")){
            return new TcpTransport(config);
        }

        return null;

    }
}
