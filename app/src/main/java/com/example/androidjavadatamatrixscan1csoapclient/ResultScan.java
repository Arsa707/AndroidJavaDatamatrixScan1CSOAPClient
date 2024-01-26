package com.example.androidjavadatamatrixscan1csoapclient;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

public class ResultScan implements KvmSerializable {
    private String processId;
    private String ДатаСканирования;
    private boolean ТретичнаяУпаковка;
    private String КодУпаковки;

    public ResultScan(String processId, String ДатаСканирования, boolean ТретичнаяУпаковка, String КодУпаковки){
        this.processId = processId;
        this.ДатаСканирования = ДатаСканирования;
        this.ТретичнаяУпаковка = ТретичнаяУпаковка;
        this.КодУпаковки = КодУпаковки;
    }

    public Object getProperty(int arg0) {
        switch (arg0) {
            case 0:
                return processId;
            case 1:
                return ДатаСканирования;
            case 2:
                return ТретичнаяУпаковка;
            case 3:
                return КодУпаковки;
            default:
                break;
        }
        return null;
    }

    public int getPropertyCount() {
        return 4;
    }

    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                this.processId = value.toString();
                break;
            case 1:
                this.ДатаСканирования = value.toString();
                break;
            case 2:
                this.ТретичнаяУпаковка = (Boolean) value;
                break;
            case 3:
                this.КодУпаковки = value.toString();
                break;
            default:
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "processId";
                propertyInfo.setNamespace(MDLPH.namespace);
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "ДатаСканирования";
                propertyInfo.setNamespace(MDLPH.namespace);
                break;
            case 2:
                propertyInfo.type = boolean.class;
                propertyInfo.name = "ТретичнаяУпаковка";
                propertyInfo.setNamespace(MDLPH.namespace);
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "КодУпаковки";
                propertyInfo.setNamespace(MDLPH.namespace);
                break;
            default:
                break;
        }
    }
}