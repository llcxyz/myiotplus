package myiot.core.sensor;

import myiot.core.util.IotConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aron on 2016/12/19.
 */
public class SensorFactory {
    private static Logger logger = LogManager.getLogger(SensorFactory.class);

    private static  Map<String, Class> supportSensor = new HashMap<String,Class>();

    static {
        supportSensor.put("ErpSensor",ErpSensor.class);
        supportSensor.put("OpcSensor", OpcSensor.class);
    }

    public static Sensor create(IotConfig config){
        String type = (String)config.getParams("type");
        if(supportSensor.containsKey(type)){
            try {
                return (Sensor)supportSensor.get(type).getConstructors()[0].newInstance(config);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        else {
            logger.error("NO SUCH SENSOR defined!!!" + config.getParams("id"));
        }

       return  null;

    }
}
