package myiot.core.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import myiot.core.gateway.DefaultGateway;
import myiot.core.gateway.Gateway;
import myiot.core.gateway.GenericGateway;
import myiot.core.metric.GenericMetric;
import myiot.core.metric.Metric;
import myiot.core.protocol.ZProtocol;
import myiot.core.sensor.Sensor;
import myiot.core.sensor.SensorFactory;
import myiot.core.transport.Transport;
import myiot.core.transport.TransportFactory;
import myiot.core.types.DynamicTypeFactory;
import myiot.core.types.NotSupportType;
import myiot.core.util.IotConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by aron on 2016/12/16.
 */
public class IotCC {

    private static Logger logger = LogManager.getLogger(IotCC.class);

    private String username;
    private String password;
    private Transport transport;
    private ZProtocol protocol;

    private boolean remoteConfig;


    private IotContext context;


    private ConcurrentHashMap<String,Gateway> gateways = new ConcurrentHashMap<String, Gateway>();

    public IotCC(){
        this.protocol = new ZProtocol();
    }

    public IotCC(String username, String password,Transport transport){
        this.username = username;
        this.password = password;
        this.transport = transport;
        this.protocol = new ZProtocol();
    }

    public void register(Gateway gateway){
        gateways.put(gateway.getID(), gateway);
        gateway.attach(this);

    }

    public void start(){
        for(Gateway gateway: gateways.values()){
            gateway.start();
        }
    }

    private  void  loadConfig(String filename){

        JSONObject jsonconfig = null;

        jsonconfig =  localConfig(filename);
        remoteConfig = (Boolean)jsonconfig.get("join_cloud");

        this.username = jsonconfig.getString("iotcc_username");
        this.password = jsonconfig.getString("iotcc_password");

        IotConfig config = IotConfig.fromJsonObject(jsonconfig.getJSONObject("transport"));
        config.addParams("name", this.username);
        this.transport = TransportFactory.create(config);

        if(remoteConfig){
            jsonconfig = remoteConfig();
        }



        if(jsonconfig.containsKey("gateways")) {

            JSONArray array = jsonconfig.getJSONArray("gateways");
            for (int i = 0; i < array.size(); i++) {
                DynamicTypeFactory.loadTypeDef(array.getJSONObject(i).getJSONArray("type_defs"));
                loadGateway(array.getJSONObject(i));


            }



        }
        else {
            logger.error("NO GATEWAY CONFIGURED!!!");

        }
    }

    public static void lauch(String configfile){

        IotCC c = new IotCC();
        c.loadConfig(configfile);
        c.start();

    }

    private void loadGateway(JSONObject gwconfig){

        DefaultGateway defaultGateway = new DefaultGateway(gwconfig.getString("id"), gwconfig.getString("name"));

        if(gwconfig.containsKey("sensors")){
            JSONArray arrsy = gwconfig.getJSONArray("sensors");
            for(int i=0;i<arrsy.size();i++){
                loadSensor(defaultGateway, arrsy.getJSONObject(i));
            }
        }

        else {
            logger.error("GATEWAY "+defaultGateway.getID()+" NO SENSORS");
        }

        register(defaultGateway);

    }

    private  void loadSensor(Gateway gw,JSONObject object){

        IotConfig  config = IotConfig.fromJsonObject(object);
        Sensor sensor = SensorFactory.create(config);
        if(object.containsKey("metrics")){
            JSONArray array = object.getJSONArray("metrics");
                for(int i=0;i<array.size();i++){
                    loadMetric(sensor, array.getJSONObject(i));
                }
            }

        gw.register(sensor);

    }

    private  void loadMetric(Sensor sensor, JSONObject object){

        IotConfig config = IotConfig.fromJsonObject(object);
        GenericMetric genericMetric = null;
        try {
            genericMetric = new GenericMetric(config);
            sensor.register(genericMetric);
        } catch (NotSupportType notSupportType) {
            notSupportType.printStackTrace();
        }


    }

    private JSONObject  remoteConfig() {
        return null;

    }

    private  JSONObject localConfig(String filename){

        try {
            File f = new File(filename);
            Long len = f.length();
            byte[]  content = new byte[len.intValue()];

            FileInputStream stream = new FileInputStream(f);
            stream.read(content);
            stream.close();
            String json  =new String(content, "UTF-8");
            logger.debug("Read Config:"+json);
            return JSON.parseObject(json);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void publish(Metric metric, Object value){

        logger.info("publisher Data ->"+metric.getItem()+",value="+value);

       this.write(this.protocol.publish(metric,value));

    }

    public void write(Object o){
        this.transport.write(o);
    }

    public boolean isRemoteConfig() {
        return remoteConfig;
    }

    public void setRemoteConfig(boolean remoteConfig) {
        this.remoteConfig = remoteConfig;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
