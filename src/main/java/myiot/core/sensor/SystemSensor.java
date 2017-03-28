package myiot.core.sensor;

import myiot.core.metric.Metric;
import myiot.core.util.IotConfig;

/**
 * Created by aron on 2016/12/20.
 */

public class SystemSensor extends GenericSensor{


    public SystemSensor(IotConfig config) {
        super(config);
    }

    @Override
    public void read(Metric metric) {

    }

    @Override
    public void write(Metric metric, Object value) {

    }
}
