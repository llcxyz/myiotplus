package myiot.core.sensor;

import myiot.core.gateway.Gateway;
import myiot.core.metric.Metric;

/**
 * Created by aron on 2016/12/16.
 */
public interface  Sensor {
    public String getID();

    public void start();

    public void start_collecting();

    public void stop_collecting();

    public void read(Metric metric);

    public void write(Metric metric, Object value);

    public void register(Metric metric);

    public void unregister(Metric metric);

    public void attach(Gateway gateway);

    public boolean isActive();

    public void publish(Metric metric,Object value);

    public String nameSpace();

    public Gateway getGateway();


}
