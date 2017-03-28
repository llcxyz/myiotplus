package myiot.core.protocol;

import com.alibaba.fastjson.JSONObject;
import myiot.core.metric.Metric;

/**
 * Created by aron on 2016/12/16.
 */
public class ZProtocol {

    private Long transId=0l;

    protected  synchronized  Long genId(){
        transId +=1;
        return transId;
    }

    public Object  publish(Metric m, Object o){
        JSONObject json = new JSONObject();
        json.put("action", "get_metric_value");
        json.put("namespace", m.getParentNameSpace());
        json.put("sensor", m.getSensor().getID());
        json.put("gateway", m.getSensor().getGateway().getID());
        json.put("user", m.getSensor().getGateway().getIotCC().getUsername());
        json.put("id", genId());

        JSONObject body =new JSONObject();
        body.put("item", m.getItem());
        body.put("type", m.getMtype());
        body.put("namespace", m.getNameSpace());

        body.put("value", o);
        body.put("timestamp",m.getTimestamp());

        json.put("body",body);
        return  json.toJSONString();
    }

}
