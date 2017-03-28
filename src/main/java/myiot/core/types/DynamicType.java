package myiot.core.types;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuc.acs.utils.NumberUtils;
import myiot.core.util.IotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aron on 2016/12/20.
 */
public class DynamicType implements Cloneable{
    private static Logger logger = LogManager.getLogger(DynamicType.class);

    private String name;

    private DynamicField[]  fields;

    private int arraySize = 0;

    public DynamicType(String name){
        this.name = name;
    }

    public DynamicType(JSONObject jsonObject){
        //System.out.println("load2 ...."+jsonObject);

        this.name = jsonObject.getString("name");
        JSONArray objects = jsonObject.getJSONArray("fields");
        fields = new DynamicField[objects.size()];

        for(int i=0;i<objects.size();i++){
            fields[i] = new DynamicField(objects.getJSONObject(i).getString("name"),objects.getJSONObject(i).getString("type"));
        }

    }

    public Object toJson(Object o) throws TypeDefineError {
        Object ret = null;
        if(o instanceof JIVariant){
           if(DynamicTypeFactory.basicTypes.containsKey(this.name)){
               try {
                   ret = basicDataType2Json((JIVariant) o);
               } catch (JIException e) {
                   e.printStackTrace();
               }
           }
            else {
               try {
                   ret = Struct2Json((JIVariant)o);
               } catch (JIException e) {
                   e.printStackTrace();

               }
           }
        }

        return ret;

    }

    protected  Object basicDataType2Json(JIVariant val) throws JIException {

        Object ret = null;
        Object value = val.getObject();

        logger.debug(" Type=   "+this.name+", Value = "+value);

        if ( value instanceof Double )
        {
            ret = (Double)value;
        }
        else if ( value instanceof Float )
        {
            ret = (Float)value;
        }
        else if ( value instanceof Byte )
        {
            //System.out.println("value ->>>>>>>>>>>is byte"+value);
            ret = (Byte)value;
        }
        else if ( value instanceof Character )
        {
            ret = (Character)value;
        }
        else if ( value instanceof Integer )
        {
            ret = (Integer)value;
        }
        else if ( value instanceof Short )
        {
            ret = (Short)value;
        }
        else if ( value instanceof Long )
        {
            ret = (Long)value;
        }
        else if ( value instanceof Boolean )
        {
            ret = (Boolean)value;
        }

        else if (value instanceof JIUnsignedByte) {
            ret = ((JIUnsignedByte) value).getValue();

        } else if (value instanceof JIString) {
            JIString js = (JIString) value;
            ret = js.getString();
        } else if (value instanceof JIUnsignedInteger) {
            ret = ((JIUnsignedInteger) value).getValue();
        } else if (value instanceof JIUnsignedShort) {
            ret = ((JIUnsignedShort) value).getValue();
        } else if (value instanceof JIArray) {
            JIArray array = (JIArray) value;
            Object[] o = (Object[]) array.getArrayInstance();
            //guess the array type.

            if (o.length > 0) {
                //单个类型长度.


                if (o[0] instanceof Boolean) {
                    Boolean[] boolArray = new Boolean[o.length];
                    for (int i = 0; i < o.length; i++) {
                        boolArray[i] = (Boolean) o[i];
                    }
                    ret = boolArray;
                } else if (o[0] instanceof Integer) {
                    Integer[] intArray = new Integer[o.length];
                    for (int i = 0; i < o.length; i++) {
                        intArray[i] = (Integer) o[i];
                    }
                    ret = intArray;
                } else if (o[0] instanceof JIUnsignedByte) {  //字节数组.

                    int size = getSizeOfType(this.getName());
                    logger.debug("get size of "+this.getName()+", is "+size+", total_len="+o.length);
                    int u_array_size = o.length/size;  //数组长度
                    JSONArray jsonArray = new JSONArray(u_array_size);
                    byte[] rbytes = new byte[o.length];
                    for (int i = 0; i <o.length; i++) {
                        rbytes[i] = ((JIUnsignedByte)o[i]).getValue().byteValue();
                    }

                    for(int i=0;i<u_array_size;i++){
                        byte[] n = new byte[size];
                        System.arraycopy(rbytes,i*size,n,0,size);
                        jsonArray.add(getTypeInstance(this.getName(), n));
                    }

                    ret = jsonArray;


//                    Integer[] intArray = new Integer[o.length];
//                    for (int i = 0; i < o.length; i++) {
//                        intArray[i] = ((JIUnsignedByte) o[i]).getValue().intValue();
//                    }
//                    ret = intArray;

                } else if (o[0] instanceof JIUnsignedShort) {
                    //二维数组.

                    System.out.println("二维数组:");
                    Integer[] intArray = new Integer[o.length];
                    for (int i = 0; i < o.length; i++) {
                        intArray[i] = ((JIUnsignedShort) o[i]).getValue().intValue();
                    }
                    ret = intArray;
                }

                else if(o[0] instanceof  JIUnsignedInteger){
                    Integer[] intArray = new Integer[o.length];
                    for (int i = 0; i < o.length; i++) {
                        intArray[i] = ((JIUnsignedInteger) o[i]).getValue().intValue();
                    }
                    ret = intArray;
                }

            }

        }

        // System.out.println("basic value"+ret);

        return ret;


    }

    protected  Object Struct2Json(JIVariant value) throws JIException, TypeDefineError {
        Object ret = null;

        if(value.isArray()) {
            JIArray array = value.getObjectAsArray();

            //VariantDumper.dumpValue(value);
            //System.out.println("ARRAY class -> " + array.getArrayClass());
            Object o = array.getArrayInstance();
            //System.out.println("array intance->" + o);
            final Object[] bytes = (Object[]) o;
            JIUnsignedByte[] rbytes = new JIUnsignedByte[bytes.length];
            for(int i=0;i<bytes.length;i++){
                if(bytes[i] instanceof  JIUnsignedByte){
                    rbytes[i] = ((JIUnsignedByte)bytes[i]);
                    // System.out.println("bytes:"+rbytes[i].getValue());
                }
            }

            ret = buildStructJson(rbytes);


        }
        return ret;

    }

    protected  Object buildStructJson(JIUnsignedByte[] bytes) throws TypeDefineError {
        Object ret = null;
        String type = IotUtils.popItem(this.name);
        byte[] rbytes = new byte[bytes.length];
        logger.debug("Build Struct for "+this.name+" total_len="+rbytes.length);

        for(int i=0;i<bytes.length;i++){
            rbytes[i] = (bytes[i]).getValue().byteValue();
            //logger.debug("Bytes["+i+"]="+rbytes[i]);
        }
        logger.debug("get size of "+this.name+" array size is "+arraySize);

        if(arraySize!=0){

            JSONArray array  = new JSONArray(arraySize);

            int object_size = getSize();

            int obj_arraySize = rbytes.length/ object_size;

            logger.debug("get object "+this.name+" byte size:"+object_size+" array size is "+obj_arraySize);

            if(rbytes.length != arraySize * object_size){

                throw  new TypeDefineError("类型:"+this.name+",长度定义错误,数据长度:"+rbytes.length+",单对象长度:"+object_size+",数组长度:"+arraySize);

            }

            for(int i=0;i<obj_arraySize;i++){
                byte[] rb = new byte[object_size];
                System.arraycopy(rbytes,i*object_size,rb,0,object_size);
                array.add(buildSingle(rb));
            }
            ret = array;
        }

        else ret = buildSingle(rbytes);

        return ret;

    }

    protected Object buildSingle(byte[] bytes){

        JSONObject jsonObject = new JSONObject();
        int offset = 0;
        for(int i=0;i<fields.length;i++){
            int size = getSizeOfType(fields[i].getType());
            byte[] b = new byte[size];
            System.arraycopy(bytes,offset, b, 0,size);
            offset = offset + size;
            jsonObject.put(fields[i].getName(), getTypeInstance(fields[i].getType(), b));
        }

        return jsonObject;

    }

    private Object getTypeInstance(String type, byte[] bytes){
        if(type.equals("uint8") || type.equals("uint8[]")){
            return new Integer(bytes[0]);
        }
        else if(type.equals("uint16") || type.equals("uint16[]")){
            return new Integer(NumberUtils.byte2ToUnsignedShort(bytes,0));
        }
        else if(type.equals("boolean") || type.equals("boolean[]")){
            return new Boolean(bytes[0]==1?true:false);
        }
        else if(type.equals("uint32") || type.equals("uint32[]")){
            return new Integer(NumberUtils.byte4ToInt(bytes,0));
        }
        else if(type.equals("int") || type.equals("int[]")){
            return new Integer(NumberUtils.byte4ToInt(bytes,0));
        }

        return null;

    }

    private int getSizeOfType(String type){
        return DynamicTypeFactory.basicTypes.get(type).size;
    }

    private Class getClassOfType(String type){
        return DynamicTypeFactory.basicTypes.get(type).cls;
    }

    private  int getSize(){

        int size= 0 ;
        for(DynamicField f: fields){
            size += getSizeOfType(f.getType());
        }

        return size;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFields(DynamicField[] fields) {
        this.fields = fields;
    }

    public DynamicField[] getFields(){return this.fields;}

    public String toString(){
        return "DynamicType[name="+name+",arraySize="+arraySize+", fields="+fields;
    }

    public int getArraySize() {
        return arraySize;
    }

    public void setArraySize(int arraySize) {
        this.arraySize = arraySize;
    }
}
