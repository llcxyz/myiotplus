package myiot.core.transport;

import myiot.core.util.IotConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeromq.ZMQ;

/**
 * Created by aron on 2016/12/16.
 */

public class ZmqTransport implements Transport{

    private static Logger logger = LogManager.getLogger(ZmqTransport.class);

    private  String name;

    private String pub_address;

    private String sub_address;

    private ZMQ.Socket pub;

    private ZMQ.Socket sub;

    private ZMQ.Context context;

    private String topic ;


    public ZmqTransport(IotConfig config){

        this.name = (String)config.getParams("name");
        this.sub_address = (String)config.getParams("sub_url");
        this.pub_address = (String)config.getParams("pub_url");
         init();

        logger.info("create Transport for username:"+this.name);

    }

    public ZmqTransport(String name, String pub_address,String sub_address){

        this.name = name;
        this.pub_address = pub_address;
        this.sub_address = sub_address;
        init();

    }

    private  void init(){

        context = ZMQ.context(10);
        this.topic = "/iotCC/"+this.name;
        this.create_pub();
        this.create_sub();

    }

    protected  void create_pub(){

        pub = this.context.socket(ZMQ.PUB);
        pub.connect(this.pub_address);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    protected  void create_sub(){

        sub = this.context.socket(ZMQ.SUB);
        sub.subscribe(this.topic.getBytes());
        sub.connect(this.sub_address);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    @Override
    public Object read() {
//        byte[] bytes = sub.recv();
//        return bytes;
        return null;
    }

    @Override
    public  boolean write(Object data) {

        logger.debug("Write Data->>>>>"+data);

        pub.send(this.topic+"/push\n"+data.toString(),1);
        //pub.send(data.toString());

        return false;

    }

}
