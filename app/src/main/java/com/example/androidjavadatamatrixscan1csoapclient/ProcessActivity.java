package com.example.androidjavadatamatrixscan1csoapclient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.*;

public class ProcessActivity extends AppCompatActivity {
    private static Process process;
    private ArrayList<String> datamatrixList;
    private TextView docDate;
    private TextView docNumber;
    private TextView number;
    private TextView date;
    private TextView organization;
    private TextView contragent;
    private TextView datamatrixScannedNow;
    private TextView datamatrixSum;
    private Button startScan;
    private Button block;
    private Button stopScan;
    private Button sendDatamatrix;
    private boolean nowIsStopScan;

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(this, "Прервано", Toast.LENGTH_SHORT).show();
        } else {
            setResult(result.getContents());
        }
    });

    private void setResult(String contents) {

        if (datamatrixList.stream().filter(r -> (r.equals(contents))).count() == 0) {
            datamatrixList.add(contents);
            int scannedCount = Integer.parseInt((String) datamatrixScannedNow.getText());
            scannedCount++;
            datamatrixScannedNow.setText(String.valueOf(scannedCount));

        } else Toast.makeText(this, "Код уже был отсканирован", Toast.LENGTH_SHORT).show();
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showCamera();
                } else {
                    Toast.makeText(this, "Нет доступа к камере", Toast.LENGTH_SHORT).show();
                }
            });

    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.DATA_MATRIX);
        options.setPrompt("Отсканируйте код DataMatrix");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);

        qrCodeLauncher.launch(options);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_scan);

        datamatrixList = new ArrayList<>();

        docDate = findViewById(R.id.doc_date);
        docNumber = findViewById(R.id.doc_number);
        date = findViewById(R.id.process_date);
        number = findViewById(R.id.process_number);
        organization = findViewById(R.id.organization);
        contragent = findViewById(R.id.contragent);
        datamatrixScannedNow = findViewById(R.id.datamatrix_scanned_now);
        datamatrixSum = findViewById(R.id.datamatrix_sum);

        docDate.setText(process.getDocDate());
        docNumber.setText(process.getDocNumber());
        date.setText(process.getDate());
        number.setText(process.getNumber());
        organization.setText(process.getOrganization());
        contragent.setText(process.getContragent());
        datamatrixSum.setText("0");
        datamatrixScannedNow.setText("0");

        startScan = findViewById(R.id.start_scan);
        startScan.setOnClickListener(view -> {
            checkPermissionAndShowActivity(this);
        });

        block = findViewById(R.id.block);
        block.setText(process.getBlock().length() > 0 ? "Разблокировать" : "Заблокировать");
        block.setOnClickListener(view -> {
            HashMap<String, String> property = new HashMap<>();
            property.put("processId", process.getProcessID());
            property.put("ИдентификаторТерминала", MDLPH.id);

            if (process.getBlock().length() > 0) property.put("СканированиеЗавершено", "false");

            MDLPH mdlph = new MDLPH(process.getBlock().length() > 0 ? MethodName.РазблокироватьПроцесс :
                    MethodName.ЗаблокироватьПроцессДляСканирования, property, this);
            ProcessActivity.DataLoader dataLoader = new ProcessActivity.DataLoader(mdlph);
            dataLoader.execute();
        });

        stopScan = findViewById(R.id.stop_scan);
        stopScan.setOnClickListener(view -> {
            nowIsStopScan = true;
            if (process.getBlock().length() == 0) {
                Toast.makeText(ProcessActivity.this, "Действие отменено. Для завершения процесс должен быть заблокирован!", Toast.LENGTH_SHORT).show();
            } else if (datamatrixList.size() > 0) {
                ResultsScan resultsScan = new ResultsScan();

                datamatrixList.forEach(datamatrix -> {
                    String datamatrixFinal;
                    if (datamatrix.substring(0, 1).equals("")) {
                        datamatrixFinal = datamatrix.substring(1).replaceAll("", "&#29;");
                    } else {
                        datamatrixFinal = datamatrix.replaceAll("", "&#29;");
                    }

                    ResultScan resultScan = new ResultScan(process.getProcessID(), process.getDate(), false, datamatrixFinal);
                    resultsScan.add(resultScan);
                });

                resultsScan.add(MDLPH.id);
                resultsScan.add(false);

                HashMap<String, ResultsScan> property = new HashMap<>();
                property.put("РезультатыСканирования", resultsScan);

                MDLPH mdlph = new MDLPH(MethodName.ПередатьРезультатыСканирования, property, this);

                ProcessActivity.DataLoader dataLoader = new ProcessActivity.DataLoader(mdlph);
                dataLoader.execute();

            } else{
                HashMap<String, String> property = new HashMap<>();
                property.put("processId", process.getProcessID());
                property.put("ИдентификаторТерминала", MDLPH.id);
                property.put("СканированиеЗавершено", "true");

                MDLPH mdlph = new MDLPH(MethodName.РазблокироватьПроцесс, property, this);
                ProcessActivity.DataLoader dataLoader = new ProcessActivity.DataLoader(mdlph);
                dataLoader.execute();
            }
        });

        sendDatamatrix = findViewById(R.id.send_Datamatrix);
        sendDatamatrix.setOnClickListener(view -> {
            nowIsStopScan = false;
            ResultsScan resultsScan = new ResultsScan();

            datamatrixList.forEach(datamatrix -> {
                String datamatrixFinal;
                if (datamatrix.substring(0, 1).equals("")) {
                    datamatrixFinal = datamatrix.substring(1).replaceAll("", "&#29;");
                } else {
                    datamatrixFinal = datamatrix.replaceAll("", "&#29;");
                }

                ResultScan resultScan = new ResultScan(process.getProcessID(), process.getDate(), false, datamatrixFinal);
                resultsScan.add(resultScan);
            });

            resultsScan.add(MDLPH.id);
            resultsScan.add(false);

            HashMap<String, ResultsScan> property = new HashMap<>();
            property.put("РезультатыСканирования", resultsScan);

            MDLPH mdlph = new MDLPH(MethodName.ПередатьРезультатыСканирования, property, this);

            ProcessActivity.DataLoader dataLoader = new ProcessActivity.DataLoader(mdlph);
            dataLoader.execute();
        });

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout activityScanLayout = findViewById(R.id.activity_scan_layout);
            activityScanLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout buttons = findViewById(R.id.buttons);
            LinearLayout.LayoutParams buttonsParams = (LinearLayout.LayoutParams) buttons.getLayoutParams();
            buttonsParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            buttonsParams.width = 0;
            buttons.setLayoutParams(buttonsParams);

            LinearLayout processInfo = findViewById(R.id.process_info);
            LinearLayout.LayoutParams processInfoParams = (LinearLayout.LayoutParams) processInfo.getLayoutParams();
            processInfoParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            processInfoParams.width = 0;
            processInfo.setLayoutParams(processInfoParams);
        }
    }

    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    public static void setProcess(Process process) {
        ProcessActivity.process = process;
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

                SoapPrimitive response = (SoapPrimitive) mdlph.getEnvelope().getResponse();
                if (response.toString().equals("true")) {
                    if (mdlph.getMethodName().equals(MethodName.РазблокироватьПроцесс.toString())) {
                        process.setBlock(false);
                        block.setText("Заблокировать");
                    } else if (mdlph.getMethodName().equals(MethodName.ЗаблокироватьПроцессДляСканирования.toString())) {
                        process.setBlock(true);
                        block.setText("Разблокировать");
                    } else if (mdlph.getMethodName().equals(MethodName.ПередатьРезультатыСканирования.toString())) {
                        int datamatrixCount = Integer.parseInt((String) datamatrixSum.getText());
                        datamatrixCount = datamatrixCount + Integer.parseInt((String) datamatrixScannedNow.getText());
                        datamatrixSum.setText(String.valueOf(datamatrixCount));
                        datamatrixScannedNow.setText("0");
                        datamatrixList.clear();

                        if (nowIsStopScan) {
                            if (process.getBlock().length() > 0) {
                                runOnUiThread(() -> {
                                    HashMap<String, String> property = new HashMap<>();
                                    property.put("processId", process.getProcessID());
                                    property.put("ИдентификаторТерминала", MDLPH.id);
                                    property.put("СканированиеЗавершено", "true");

                                    MDLPH mdlph = new MDLPH(MethodName.РазблокироватьПроцесс, property, ProcessActivity.this);
                                    ProcessActivity.DataLoader dataLoader = new ProcessActivity.DataLoader(mdlph);
                                    dataLoader.execute();
                                });
                            }
                        }
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ProcessActivity.this, "Ошибка запроса!", Toast.LENGTH_SHORT).show();
                    });
                }

                Intent answerIntent = new Intent();
                answerIntent.putExtra("needRequest", "true");
                setResult(RESULT_OK, answerIntent);

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ProcessActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
            return null;
        }
    }
}