package com.example.androidjavadatamatrixscan1csoapclient;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;
import java.util.Vector;

public class ResultsScan extends Vector implements KvmSerializable {

    public Object getProperty(int arg0) {
        return this.get(arg0);
    }

    public int getPropertyCount() {
        return this.size();
    }

    public void setProperty(int index, Object value) {
        this.add(value);
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        try {
            ResultScan resultScan = (ResultScan) get(i);
            propertyInfo.type = ResultScan.class;
            propertyInfo.name = "РезультатСканирования";
            propertyInfo.setNamespace(MDLPH.namespace);
        } catch (Exception e) {
            if (get(i).equals(false) || get(i).equals(true)) {
                propertyInfo.type = boolean.class;
                propertyInfo.name = "ОчищатьПредыдущиеЗаписи";
                propertyInfo.setNamespace(MDLPH.namespace);
            } else {
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "ИдентификаторТерминала";
                propertyInfo.setNamespace(MDLPH.namespace);
            }
        }
    }
}
