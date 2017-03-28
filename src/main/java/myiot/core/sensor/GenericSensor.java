package myiot.core.sensor;

import myiot.core.gateway.Gateway;
import myiot.core.metric.Metric;
import myiot.core.util.IotConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by aron on 2016/12/16.
 */
public abstract class  GenericSensor extends Thread  implements Sensor {

    private static Logger logger = LogManager.getLogger(GenericSensor.class);

    private String ID;

    protected   boolean active;

    private Gateway gateway;

    private int collect_thread_pool = 5;

    ScheduledExecutorService schedule;


    protected ConcurrentHashMap<String,Metric> metrics = new ConcurrentHashMap<String, Metric>();


    public GenericSensor(IotConfig config){

        init((String)config.getParams("id"), (Integer)config.getParams("poolsize", 5));
    }

    public GenericSensor(String id){
        init(id,5);
    }

    public GenericSensor(String id,int pool_size){
        init(id,pool_size);
    }

    protected  void init(String ID,int pool_size){
        this.ID = ID;
        this.setName("Sensor:"+this.ID);
        this.active = true;

        this.collect_thread_pool = pool_size;
        schedule= Executors.newScheduledThreadPool(collect_thread_pool);

    }
    public void run(){
        this.start_collecting();
    }

    public void start_collecting(){
        logger.info("start collecting for sensor: "+this.getID()+"");

        for(Metric metric: metrics.values()){
            schedule.scheduleAtFixedRate(metric, 1, metric.getInterval(), TimeUnit.MILLISECONDS);
        }

    }
    public void stop_collecting(){

    }

    public String nameSpace(){
        return this.gateway.getNameSpace()+"."+this.getID();
    }

    public void publish(Metric metric, Object value){
        if(this.gateway!=null){
            this.gateway.publish(metric, value);
        }
    }
    public void register(Metric metric){
        metrics.put(metric.getItem(), metric);
        metric.attach(this);

    }

    public void attach(Gateway gateway){
        this.gateway = gateway;
    }


    public void unregister(Metric metric){
        metrics.remove(metric.getItem());
    }

    public void unregister(String item){
        metrics.remove(item);
    }
    public String getID(){return this.ID;}
    public boolean isActive(){return this.active;}

    public Gateway getGateway(){
        return  this.gateway;
    }


}
