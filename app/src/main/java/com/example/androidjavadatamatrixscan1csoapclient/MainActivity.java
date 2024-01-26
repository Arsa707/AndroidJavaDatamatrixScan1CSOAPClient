package com.example.androidjavadatamatrixscan1csoapclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Button settings;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        settings = findViewById(R.id.button_settings);
        settings.setOnClickListener(view -> {
            Class<?> cl = null;
            try {
                cl = Class.forName("com.example.androidjavadatamatrixscan1csoapclient.SettingsActivity");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Intent intent = new Intent(MainActivity.this, cl);
            startActivityForResult(intent, 0);
        });

        //Инициализируем параметры подключения
        MDLPH.checkSettings(this);

        //Запрашиваем пул неотсканированных и заполняем список процессов на экране фоновым заданием
        HashMap<String, String> property = new HashMap<>();
        property.put("ИдентификаторТерминала", MDLPH.id);

        MDLPH mdlph = new MDLPH(MethodName.ЗапросПулаНеотсканированныхПоТерминалу, property, this);
        DataLoader dataLoader = new DataLoader(mdlph);
        dataLoader.execute();

        //Настраиваем свайпер
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String resultBlock = data.getStringExtra("needRequest");
            if (resultBlock.equals("true")) {
                //Запрашиваем пул неотсканированных и заполняем список процессов на экране фоновым заданием
                HashMap<String, String> property = new HashMap<>();
                property.put("ИдентификаторТерминала", MDLPH.id);

                MDLPH mdlph = new MDLPH(MethodName.ЗапросПулаНеотсканированныхПоТерминалу, property, this);
                DataLoader dataLoader = new DataLoader(mdlph);
                dataLoader.execute();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        MDLPH.checkSettings(this);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        //Запрашиваем пул неотсканированных и заполняем список процессов на экране фоновым заданием
        HashMap<String, String> property = new HashMap<>();
        property.put("ИдентификаторТерминала", MDLPH.id);

        MDLPH mdlph = new MDLPH(MethodName.ЗапросПулаНеотсканированныхПоТерминалу, property, this);
        DataLoader dataLoader = new DataLoader(mdlph);
        dataLoader.execute();
    }

    public class DataLoader extends AsyncTask<Void, Void, Void> {
        private MDLPH mdlph;
        DataLoader(MDLPH mdlph) {
            this.mdlph = mdlph;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                mdlph.call();
                SoapObject response = (SoapObject) mdlph.getEnvelope().getResponse();

                runOnUiThread(() -> {
                    //Заполняем список процессов на главном экране
                    if (mdlph.getMethodName().equals(MethodName.ЗапросПулаНеотсканированныхПоТерминалу.toString())) {
                        fillProcessesList(response);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            return null;
        }
    }

    public void fillProcessesList(SoapObject response) {
        //Заполняем список процессов полученными данными
        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < response.getPropertyCount(); i++) {
            SoapObject properties = (SoapObject) response.getProperty(i);
            String number = properties.getPrimitivePropertyAsString("НомерПроцесса");
            String date = properties.getPrimitivePropertyAsString("ДатаПроцесса");
            String block = properties.getPrimitivePropertyAsString("Заблокирован");
            String organization = properties.getPrimitivePropertyAsString("Организация");
            String contragent = properties.getPrimitivePropertyAsString("Контрагент");
            String docDate = properties.getPrimitivePropertyAsString("ВхДата");
            String docNumber = properties.getPrimitivePropertyAsString("ВхНомер");
            String processID = properties.getPrimitivePropertyAsString("processId");
            Process process = new Process(number, date, block, i,
                    organization, contragent, docDate, docNumber, processID);
            processes.add(process);
        }

        RecyclerView recyclerView = findViewById(R.id.list);
        final ProcessesAdapter processesAdapter = new ProcessesAdapter(processes, process -> {

            //При нажатии на процесс открываем форму процесса
            Class<?> cl = Class.forName("com.example.androidjavadatamatrixscan1csoapclient.ProcessActivity");
            Intent intent = new Intent(MainActivity.this, cl);
            ProcessActivity.setProcess(process);
            startActivityForResult(intent, 0);
        });

        recyclerView.setAdapter(processesAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
}