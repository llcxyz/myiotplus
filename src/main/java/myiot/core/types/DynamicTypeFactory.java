package myiot.core.types;

import com.alibaba.fastjson.JSONArray;
import com.zhuc.acs.protocol.opc.OperationFailedException;
import myiot.core.util.IotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aron on 2016/12/20.
 */
public class DynamicTypeFactory {

    private static Logger logger = LogManager.getLogger(DynamicTypeFactory.class);

    public static Map<String, Type> basicTypes = new HashMap<String, Type>();

    private static Map<String, DynamicType> types = new HashMap<String,DynamicType>();

     static {
         Type uint8 = new Type("uint8",1, Integer.class);
         Type uint16 = new Type("uint16", 2,Integer.class);
         Type uint32 = new Type("uint32", 4, Integer.class);
         Type bool = new Type("boolean", 1, Boolean.class);
         Type bool_array = new Type("boolean[]", 1, Boolean[].class);

         Type uint8_array = new Type("uint8[]",1,Integer[].class);
         Type uint32_array = new Type("uint32[]",4,Integer[].class);

         Type intt = new Type("int", 4, Boolean.class);

         basicTypes.put(uint8.type, uint8);
         basicTypes.put(uint16.type, uint16);
         basicTypes.put(intt.type, intt);
         basicTypes.put(bool.type, bool);
         basicTypes.put(uint32.type, uint32);

         basicTypes.put(bool_array.type, bool_array);
         basicTypes.put(uint8_array.type, uint8_array);
         basicTypes.put(uint32_array.type, uint32_array);

    }

    public static void loadTypeDef(JSONArray jsonArray){
        System.out.println("load ...."+jsonArray);
        for(int i=0;i<jsonArray.size();i++){

            DynamicType type = new DynamicType(jsonArray.getJSONObject(i));

            types.put(type.getName(), type);

            logger.debug("create new DynamicType:"+type.getName());

        }
    }



    public static DynamicType getType(String type) throws NotSupportType {

        if (basicTypes.containsKey(type)){
            return new DynamicType(type);
        }

        String rtype = popItem(type);

        int size = IotUtils.getItemOffset(type);

        logger.debug("Resolv Type:"+type+", get RealType: "+rtype+",arraySize:"+size);

        if (types.containsKey(rtype)) {
            DynamicType dt =  types.get(rtype);   //必须克隆或者新建立对象.
            DynamicType newdt = new DynamicType(dt.getName());
            newdt.setFields(dt.getFields());
            if(size>0)
                newdt.setArraySize(size);

            return dt;

        }

        throw new NotSupportType("NOT Support Type!"+type);


    }

    private static String popItem(String item){

        if(item.indexOf("[") !=-1 && item.indexOf("]")!=-1){
            String off = item.substring(0, item.indexOf("["));
            return off;
        }
        return item;
    }

    static  class Type {

        public String type;
        public int size;
        public Class cls;
        public Type(String type, int size,Class cls){
            this.type =type;
            this.size = size;
            this.cls = cls;

        }
    }


}
