package myiot.core.sensor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuc.acs.protocol.opc.DataChangeListener;
import com.zhuc.acs.protocol.opc.OPCServer;
import com.zhuc.acs.protocol.opc.OperationFailedException;
import myiot.core.gateway.GenericGateway;
import myiot.core.metric.Metric;
import myiot.core.types.DynamicTypeFactory;
import myiot.core.types.TypeDefineError;
import myiot.core.util.IotConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;

import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
 * Created by aron on 2016/12/16.
 */
public class OpcSensor extends GenericSensor implements DataCallback, ServerConnectionStateListener{


    private static Logger logger = LogManager.getLogger(OpcSensor.class);

    private OPCServer server;

    private int refresh_time = 1000;

    public OpcSensor(IotConfig config){
        super(config);
        JSONObject args = (JSONObject)config.getParams("args");

        this.active = false;


        server = new OPCServer(args.getString("name"), args.getString("host"), args.getString("username"),
                args.getString("password"), args.getString("clsId"), args.getString("domain"));


//        server=  new OPCServer("local", "192.168.60.252", "administrator", "admin",
//                "aa6c2a7c-f097-4be3-9153-f44ce2a3d916", "");

        if(args.containsKey("refresh")) {
            refresh_time = args.getIntValue("refresh");
        }

        server.addServerConnectionStateListener(this);
        server.setRefresh_time(refresh_time);

        try {
            server.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        } catch (JIException e) {
            e.printStackTrace();
        } catch (DuplicateGroupException e) {
            e.printStackTrace();
        }

        super.start_collecting();

    }

    public OpcSensor(String id) {
        super(id);
    }

    @Override
    public  synchronized void read(Metric metric)  {


//        try {
//            if(this.active) {
//
//                logger.info("reading metric ->" + metric.getItem() + "....");
//
//                ItemState  v = server.read(fixedOpcItem(metric),true);
//
//                metric.notify_data(v.getValue());
//
//            }
//
//        } catch (JIException e) {
//            e.printStackTrace();
//        } catch (AddFailedException e) {
//            e.printStackTrace();
//        }

        //
    }

    @Override
    public void write(Metric metric, Object value) {

    }

    private String fixedOpcItem(Metric m){
        if(m.getNameSpace()!=null || !m.getNameSpace().equals("")){
            return m.getNameSpace()+"."+m.getItem();
        }
        return m.getItem();
    }

    private String getMetricItem(String item){

        return item.substring(item.indexOf(".")+1, item.length());

    }

    @Override
    public void connectionStateChanged(boolean connected) {

        if(connected) {
            this.active = true;
            for(Metric mt:metrics.values()){

                try {
                    logger.debug("add item"+mt.getItem());
                    this.server.addItem(fixedOpcItem(mt),this);
                } catch (JIException e) {
                    e.printStackTrace();
                } catch (AddFailedException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    @Override
    public void changed(Item item, ItemState itemState) {

        logger.debug("received :::"+item.getId()+", = "+itemState.getValue());

        if(metrics.containsKey(getMetricItem(item.getId()))){
            Metric m = metrics.get(getMetricItem(item.getId()));

            logger.debug("Type resolv for ["+item.getId()+"] ,type="+m.getType().getName());
            if(item.getId().equals("cbw.SoapTotalData")){
                logger.debug("break on ite");

            }
            try {
                Object value = m.getType().toJson(itemState.getValue());
                m.setTimestamp(itemState.getTimestamp().getTimeInMillis());
                m.notify_data(value);  //notify data 会根据update_change决定是否更新时才发布.

            } catch (TypeDefineError typeDefineError) {
                typeDefineError.printStackTrace();
                logger.error("解析:["+item.getId()+"] 发生异常:"+typeDefineError.getMessage());
            }

        }
    }
}
