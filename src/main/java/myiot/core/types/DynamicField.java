package myiot.core.types;

/**
 * Created by aron on 2016/12/20.
 */
public class DynamicField {
    private String name;
    private String type;

    public  DynamicField(String name, String type){
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
