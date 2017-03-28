package myiot.ext;

import myiot.core.gateway.Gateway;
import myiot.core.gateway.DefaultGateway;
import myiot.core.metric.GenericMetric;
import myiot.core.sensor.ErpSensor;
import myiot.core.sensor.OpcSensor;
import myiot.core.sensor.Sensor;
import myiot.core.server.IotCC;
import myiot.core.transport.ZmqTransport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by aron on 2016/12/16.
 */
public class Sealion {
    private static Logger logger = LogManager.getLogger(Sealion.class);

    public static void main(String[] argv){

        logger.debug("OK");

        String username = "admin111";

        ZmqTransport transport = new ZmqTransport(username, "tcp://127.0.0.1:19020","tcp://127.0.0.1:19021");

        IotCC cc = new IotCC("admin","admin", transport);



        Gateway gateway = new DefaultGateway("sealion","海狮洗涤");

        Sensor sensor = new OpcSensor("opc-1");

        GenericMetric metric = new GenericMetric("Random.Int","随机数",100, "unit8");

        GenericMetric metric1 = new GenericMetric("Random.String","随机数",100, "unit8");

        sensor.register(metric);

        sensor.register(metric1);


        Sensor sensor2 = new ErpSensor("erp_system","hhxj.xunjiexidi.com", "http://127.0.0.1:10003", "admin", "admin");
        GenericMetric metric_s = new GenericMetric("ws.washing.speed","随机数",1000, "unit8");

        sensor2.register(metric_s);

        gateway.register(sensor2);

        //gateway.register(sensor);

        cc.register(gateway);



        cc.start();

    }
}
