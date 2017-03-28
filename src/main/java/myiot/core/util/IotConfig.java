package myiot.core.util;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aron on 2016/10/14.
 */
public class IotConfig {

    private Map<String,Object> params = new HashMap<String,Object>();


    public static IotConfig fromJsonObject(JSONObject jsonObject){
        IotConfig iotConfig = new IotConfig();
        for(String key: jsonObject.keySet()){
            iotConfig.addParams(key, jsonObject.get(key));
        }

        return iotConfig;

    }

    public Map<String,Object> getParams(){
        return params;
    }

    public Object getParams(String key){
        return params.get(key);
    }
    public Object getParams (String key,Object defaults){
        if(params.containsKey(key)){
            return params.get(key);
        }
        else return defaults;

    }

    public void addParams(String key,Object value){
        params.put(key,value);
    }
    public void removeParams(String key){
        if(params.containsKey(key)) params.remove(key);
    }

}
