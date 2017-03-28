package myiot.core.gateway;

import myiot.core.metric.Metric;
import myiot.core.sensor.Sensor;
import myiot.core.server.IotCC;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aron on 2016/12/16.
 */
public abstract  class  GenericGateway extends  Thread implements Gateway{

    private static Logger logger = LogManager.getLogger(GenericGateway.class);

    private String ID;

    private IotCC iotCC;

    private String gatewayName;

    private ConcurrentHashMap<String, Sensor> sensors = new ConcurrentHashMap<String, Sensor>();


    public GenericGateway(String ID,String gateway_name){
        this.ID = ID;
        this.setGatewayName(gateway_name);
        this.setName("Gateway:"+this.ID);
    }

    public void run(){
        logger.info("start gateway:"+this.ID);

        for(Sensor sensor:sensors.values()){
            sensor.start();
        }

    }

    public void register(Sensor sensor){
        sensors.put(sensor.getID(),sensor);
        sensor.attach(this);
    }

    public String getNameSpace(){
        return iotCC.getUsername()+"."+this.getID();
    }
    public IotCC getIotCC(){
        return iotCC;
    }

    public String getID(){return this.ID;}

    public void attach(IotCC iotCC){
        this.iotCC = iotCC;
    }

    public void publish(Metric metric, Object value){
        if(iotCC!=null){
            iotCC.publish(metric, value);

        }
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }
}
