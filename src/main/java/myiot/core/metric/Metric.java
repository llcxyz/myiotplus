package myiot.core.metric;

import myiot.core.sensor.Sensor;
import myiot.core.types.DynamicType;

/**
 * Created by aron on 2016/12/16.
 */
public interface Metric extends Runnable{
    public String getItem();
    public void read();
    public void write(Object o);
    public void notify_data(Object value);
    public String getName();
    public int getInterval();
    public boolean isActive();
    public void attach(Sensor sensor);
    public Object getValue();
    public void setValue(Object o);

    public String getParentNameSpace();

    public String getNameSpace();

    public Long getTimestamp();
    public void setTimestamp(Long timestamp);

    public String getMtype();

    public DynamicType getType();

    public Sensor getSensor();



}
