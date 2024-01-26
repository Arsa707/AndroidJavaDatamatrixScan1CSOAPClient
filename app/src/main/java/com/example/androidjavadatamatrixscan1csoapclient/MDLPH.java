package com.example.androidjavadatamatrixscan1csoapclient;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MDLPH{
    //request url at home = "http://192.168.1.67/HomeBase/ws/mdlph/";
    //request url at work= "http://server04/holding/ws/mdlph/";

    private Context context;
    public static String login;
    public static String password;
    public static String id;
    public static String host;
    public static String base;
    public static String service;
    public static String namespace;
    private String methodName;
    private String soapAction;
    private HashMap property;
    private HttpTransportSE ht;
    private SoapSerializationEnvelope envelope;
    private List<HeaderProperty> headerList;
    private SoapObjectCustom request;

    MDLPH(MethodName methodName, HashMap property, Context context){
        super();
        this.methodName = methodName.toString();
        this.soapAction = namespace + "#MDLPH:" + this.methodName;
        this.property = property;
        this.context = context;
        initialization();
    }

    private void initialization(){
        checkSettings();

        request = new SoapObjectCustom(namespace, this.methodName);
        property.forEach((k,v)-> request.addProperty((String)k,v)); //Добавляем свойства в запрос
        envelope = getSoapSerializationEnvelope(request);

        if (property.containsKey("РезультатСканирования")) {
            envelope.addMapping(namespace, "РезультатСканирования", ResultScan.class);
        }
        if (property.containsKey("РезультатыСканирования")) {
            envelope.addMapping(namespace, "РезультатыСканирования", ResultsScan.class);
        }

        ht = getHttpTransportSE();

        //Авторизация+
        String basicAuthName = login;
        String basicAuthPass = password;
        headerList = new ArrayList<>();
        if (basicAuthName != null && basicAuthPass != null) {
            byte[] token = (basicAuthName + ":" + basicAuthPass).getBytes();
            headerList.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode(token)));
        }
        headerList.add(new HeaderProperty("Connection", "Close"));
        //Авторизация-
    }
    public void call() throws XmlPullParserException, IOException {
        ht.call(this.soapAction, this.envelope, this.headerList);
    }
    private SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = false;
        envelope.implicitTypes = true;
        envelope.setOutputSoapObject(request);
        return envelope;
    }
    private HttpTransportSE getHttpTransportSE() {
        String requestUrl = "http://"+host+"/"+base+"/ws/"+service+"/";
        HttpTransportSE ht = new HttpTransportSE(requestUrl, 20000);
        ht.debug = true;
        return ht;
    }
    public String getMethodName() {
        return methodName;
    }
    public SoapSerializationEnvelope getEnvelope() {
        return envelope;
    }

    public static class SoapObjectCustom extends SoapObject {public SoapObjectCustom(String namespace, String name) {
        super(namespace, name);
    }
        @Override
        public SoapObject addProperty(String name, Object value) {
            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.name = name;
            propertyInfo.type = value == null ? PropertyInfo.OBJECT_CLASS : value.getClass();
            propertyInfo.setValue(value);

            // Добавил эту строку
            propertyInfo.setNamespace(this.namespace);

            return addProperty(propertyInfo);
        }
    }

    public void checkSettings(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        login = prefs.getString("login", "");
        password = prefs.getString("password", "");
        id = prefs.getString("id", "");
        host = prefs.getString("host", "");
        base = prefs.getString("base", "");
        namespace = prefs.getString("namespace", "");
        service = prefs.getString("service", "");
    }

    public static void checkSettings(Context context){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        login = prefs.getString("login", "");
        password = prefs.getString("password", "");
        id = prefs.getString("id", "");
        host = prefs.getString("host", "");
        base = prefs.getString("base", "");
        namespace = prefs.getString("namespace", "");
        service = prefs.getString("service", "");
    }
}
