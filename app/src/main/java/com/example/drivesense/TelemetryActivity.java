package com.example.drivesense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class TelemetryActivity extends AppCompatActivity {

    private Basededatos db;
    private int vehicleId = -1;
    private ListView lvTelemetry;
    private Button btnDemo, btnStart, btnStop, btnExport;
    private ArrayAdapter<String> listAdapter;
    private final List<String> lines = new ArrayList<>();
    private Handler recordHandler;
    private boolean isRecording = false;
    private int recordIntervalMs = 3000; // cada 3s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemetry);

        db = new Basededatos(this);
        lvTelemetry = findViewById(R.id.lvTelemetry);
        btnDemo = findViewById(R.id.btnDemoTelemetry);
        btnStart = findViewById(R.id.btnStartRecording);
        btnStop = findViewById(R.id.btnStopRecording);
        btnExport = findViewById(R.id.btnExportCSV);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lines);
        lvTelemetry.setAdapter(listAdapter);

        vehicleId = getIntent().getIntExtra("vehicle_id", -1);
        if (vehicleId == -1) {
            SharedPreferences prefs = getSharedPreferences("drivesense_prefs", MODE_PRIVATE);
            vehicleId = prefs.getInt("selected_vehicle_id", -1);
        }
        if (vehicleId == -1) {
            Toast.makeText(this, "No hay vehículo vinculado. Vincula un vehículo desde Perfil antes de grabar telemetría.", Toast.LENGTH_LONG).show();
            btnDemo.setEnabled(false);
            btnStart.setEnabled(false);
            btnStop.setEnabled(false);
            btnExport.setEnabled(false);
            cargarTelemetria();
            return;
        }

        recordHandler = new Handler(Looper.getMainLooper());
        btnStart.setOnClickListener(v -> {
            if (vehicleId == -1) {
                Toast.makeText(this, "Selecciona un vehículo antes de grabar", Toast.LENGTH_SHORT).show();
                return;
            }
            startRecording();
        });
        btnStop.setOnClickListener(v -> stopRecording());

        btnExport.setOnClickListener(v -> {
            if (vehicleId == -1) {
                Toast.makeText(this, "Selecciona un vehículo antes de exportar", Toast.LENGTH_SHORT).show();
                return;
            }
            exportarCSV();
        });

        cargarTelemetria();
    }

    private void cargarTelemetria() {
        lines.clear();
        if (vehicleId == -1) {
            listAdapter.notifyDataSetChanged();
            return;
        }
        Cursor c = db.obtenerTelemetriaPorVehiculo(vehicleId, 200);
        while (c.moveToNext()) {
            String ts = c.getString(c.getColumnIndexOrThrow("timestamp"));
            String rpm = c.isNull(c.getColumnIndexOrThrow("rpm")) ? "-" : String.valueOf(c.getInt(c.getColumnIndexOrThrow("rpm")));
            String fuel = c.isNull(c.getColumnIndexOrThrow("fuel_rate")) ? "-" : String.valueOf(c.getDouble(c.getColumnIndexOrThrow("fuel_rate")));
            lines.add(ts + " | RPM:" + rpm + " | fuel:" + fuel);
        }
        c.close();
        listAdapter.notifyDataSetChanged();
    }

    private String isoNow(long millis) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date(millis));
    }

    private final Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRecording) return;
            recordHandler.postDelayed(this, recordIntervalMs);
        }
    };

    private void startRecording() {
        if (isRecording) {
            Toast.makeText(this, "Grabación ya iniciada", Toast.LENGTH_SHORT).show();
            return;
        }
        isRecording = true;
        recordHandler.post(recordRunnable);
        Toast.makeText(this, "Grabación demo iniciada", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        if (!isRecording) {
            Toast.makeText(this, "No hay grabación en curso", Toast.LENGTH_SHORT).show();
            return;
        }
        isRecording = false;
        recordHandler.removeCallbacks(recordRunnable);
        Toast.makeText(this, "Grabación demo detenida", Toast.LENGTH_SHORT).show();
    }

    private void exportarCSV() {
        try {
            String csv = db.exportarTelemetriaCSV(vehicleId);
            String filename = "telemetry_vehicle_" + vehicleId + ".csv";
            File file = new File(getExternalFilesDir(null), filename);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(csv.getBytes());
            }
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Compartir CSV"));
        } catch (Exception e) {
            Toast.makeText(this, "Error exportando CSV: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recordHandler != null) recordHandler.removeCallbacks(recordRunnable);
    }
}
