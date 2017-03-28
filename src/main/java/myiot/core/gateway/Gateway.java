package myiot.core.gateway;

import myiot.core.metric.Metric;
import myiot.core.sensor.Sensor;
import myiot.core.server.IotCC;

/**
 * Created by aron on 2016/12/16.
 */
public interface Gateway {
    public String getID();
    public void start();
    public void register(Sensor sensor);
    public void attach(IotCC iotCC);
    public void publish(Metric metric, Object value);
    public String getNameSpace();
    public IotCC getIotCC();

}
