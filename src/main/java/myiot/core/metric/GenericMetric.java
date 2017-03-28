package myiot.core.metric;

import com.zhuc.acs.protocol.opc.OperationFailedException;
import myiot.core.sensor.Sensor;
import myiot.core.types.DynamicType;
import myiot.core.types.DynamicTypeFactory;
import myiot.core.types.NotSupportType;
import myiot.core.util.IotConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by aron on 2016/12/16.
 */
public   class GenericMetric implements Metric{

    private static Logger logger = LogManager.getLogger(GenericMetric.class);

    private String item;

    private String name;

    private int interval;

    private String m_type;

    private boolean active;

    private Sensor sensor;

    private Object value;

    private Long timestamp;

    private boolean update_change= false;  //变更时更新.

    private String namespace="";


    private DynamicType type;

    public GenericMetric(IotConfig config) throws NotSupportType {
        this.item = (String)config.getParams("item");
        this.name = (String)config.getParams("name","noname");
        this.m_type = (String)config.getParams("type");
        this.interval = (Integer)config.getParams("interval",1000);
        this.update_change = (Boolean)config.getParams("change_update",false);

        this.namespace = (String)config.getParams("namespace", "");

        this.type = DynamicTypeFactory.getType(this.m_type);

        logger.debug("create metric->"+this);

    }

    public GenericMetric(String item, String name, int interval, String m_type, boolean update_change){
        init(item,name,interval,m_type, update_change);
    }

    public GenericMetric(String item, String name, int interval, String m_type){

        init(item,name,interval,m_type, false);
    }

    private  void init(String item, String name, int interval, String m_type, boolean update_change){
        this.item = item;
        this.name = name;
        this.interval = interval;
        this.m_type = m_type;
        this.active = true;
        this.update_change = update_change;
    }

    public void attach(Sensor sensor){
        this.sensor = sensor;
    }

    public void run(){
        this.read();
    }

    @Override
    public String getItem() {
        return item;
    }

    public void read(){
        if(this.sensor!=null){
            this.sensor.read(this);
        }
    }

    @Override
    public void write(Object o) {

    }

    @Override
    public void notify_data(Object value) {
        if(this.sensor!=null){
            if(this.value!=null && this.update_change ) {

                if( !this.value.equals(value))
                    this.sensor.publish(this, value);
                this.value = value;
            }
            else {
                this.sensor.publish(this,value);
                this.value = value;
            }

        }
    }

    @Override
    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getM_type() {
        return m_type;
    }

    public void setM_type(String m_type) {
        this.m_type = m_type;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getParentNameSpace() {
        return this.sensor.nameSpace()+"";
    }

    @Override
    public String getNameSpace() {
         return namespace;
    }


    public void setValue(Object value) {
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }



    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMtype(){ return this.m_type;}

    @Override
    public DynamicType getType() {
        return type;
    }

    @Override
    public Sensor getSensor() {
        return this.sensor;
    }

    public void setType(DynamicType type) {
        this.type = type;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String toString(){

        return "Metric[item="+item+",namespace="+namespace+",type="+this.type+"]";

    }
}
