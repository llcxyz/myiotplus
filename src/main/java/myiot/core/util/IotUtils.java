package myiot.core.util;

/**
 * Created by aron on 2016/12/20.
 */
public class IotUtils {

    public static int getItemOffset(String item){

        if(item.indexOf("[") !=-1 && item.indexOf("]")!=-1){
            String off = item.substring(item.indexOf("[")+1,item.indexOf("]"));
            return Integer.parseInt(off);
        }

        return 0;

    }

    public static String popItem(String item){

        if(item.indexOf("[") !=-1 && item.indexOf("]")!=-1){
            String off = item.substring(0, item.indexOf("["));
            return off;
        }
        return item;
    }


}
