package myiot.core.sensor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import myiot.core.metric.Metric;
import myiot.core.util.IotConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asynchttpclient.*;

import java.nio.charset.Charset;


/**
 * Created by aron on 2016/12/19.
 */

public class ErpSensor extends GenericSensor {

    private Logger logger = LogManager.getLogger(ErpSensor.class);


    private String url;

    private String username;

    private String password;


    private  String db;

    private AsyncHttpClient client = null;


    public ErpSensor(IotConfig iotConfig){
        super(iotConfig);
        JSONObject args = (JSONObject)iotConfig.getParams("args");

        this.db = (String)args.get("db");
        this.url = (String)args.get("url");
        this.username =(String)args.get("username");
        this.password = (String)args.get("password");

        initClient();

        logger.debug("create ErpSensor instance with "+JSON.toJSONString(iotConfig));

        initClient();

    }

    public ErpSensor(String id, String db ,String url, String username, String password) {
        super(id);
        this.db = db;
        this.url = url;
        this.username = username;
        this.password = password;
        initClient();

       // access_url = (String)config.getParams("url");


    }
    public void initClient(){
        DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder();
        builder.setMaxConnections(1000);
        builder.setMaxConnectionsPerHost(1000);
        builder.setUsePooledMemory(true);

        //builder.setConnectTimeout(Integer.valueOf((String)config.getParams("timeout")));

        DefaultAsyncHttpClientConfig clientconfig = builder.build();

        client=  new DefaultAsyncHttpClient(clientconfig);

    }

    @Override
    public void read(final Metric metric) {

        String access_url = url+"/metric/fetch?key="+metric.getItem()+"&db="+this.db;
        logger.debug("acccess_url:"+access_url);
        client.prepareGet(access_url).execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                String body = response.getResponseBody(Charset.forName("UTF-8"));
                //metric.setValue(body);
                logger.debug("return :"+body);
                JSONObject json = JSON.parseObject(body);
                if(json.containsKey(metric.getItem())){
                    metric.notify_data(json.get(metric.getItem()));
                }

                return response;
            }

            public void onThrowable(Throwable cause){

                logger.debug(metric.getItem()+" 读取失败:"+cause.getMessage());

                cause.printStackTrace();

            }
        });

    }

    @Override
    public void write(Metric metric, Object value) {

    }
}
