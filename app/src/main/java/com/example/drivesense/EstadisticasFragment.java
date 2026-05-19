package com.example.drivesense;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.drivesense.databinding.FragmentEstadisticasBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EstadisticasFragment extends Fragment {

    private FragmentEstadisticasBinding binding;
    private NotificacionesAdapter adapter;
    private List<String> listaAlertas;
    private Basededatos db;
    private Handler handler;
    private Runnable refrescarRunnable;
    private int selectedVehicleId = -1;
    private static final int REFRESH_MS = 3000;
    private static final int TELEMETRY_LIMIT = 50;
    private int simTick = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstadisticasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new Basededatos(requireContext());
        handler = new Handler(Looper.getMainLooper());

        SharedPreferences prefs = requireActivity().getSharedPreferences("drivesense_prefs", getContext().MODE_PRIVATE);
        selectedVehicleId = prefs.getInt("selected_vehicle_id", prefs.getInt("user_vehicle_id", -1));
        if (selectedVehicleId == -1) selectedVehicleId = prefs.getInt("user_id", -1);

        listaAlertas = new ArrayList<>();
        adapter = new NotificacionesAdapter(listaAlertas);
        binding.rvNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvNotificaciones.setAdapter(adapter);

        binding.btnLimpiarAlertas.setOnClickListener(v -> {
            int size = listaAlertas.size();
            listaAlertas.clear();
            adapter.notifyItemRangeRemoved(0, size);
            Toast.makeText(getContext(), "Feed de alertas despejado", Toast.LENGTH_SHORT).show();
        });

        refrescarRunnable = new Runnable() {
            @Override
            public void run() {
                actualizarEstadisticas();
                handler.postDelayed(this, REFRESH_MS);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(refrescarRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(refrescarRunnable);
    }

    private void actualizarEstadisticas() {
        Cursor c;
        List<TelemetryRecord> recs = new ArrayList<>();
        if (selectedVehicleId != -1) {
            c = db.obtenerTelemetriaPorVehiculo(selectedVehicleId, TELEMETRY_LIMIT);
            while (c.moveToNext()) {
                recs.add(TelemetryRecord.fromCursor(c));
            }
            c.close();
        }if (recs.isEmpty()) {
            ejecutarSimulacionDemo();
            return;
        }
        double sumRpm = 0;
        int countRpm = 0;
        for (TelemetryRecord r : recs) {
            if (r.rpm != null) { sumRpm += r.rpm; countRpm++; }
        }
        double rpmAvg = countRpm == 0 ? 0 : (sumRpm / countRpm);
        double gco2 = Utils.estimarGCO2(recs);
        binding.tvValorConsumo.setText(String.format(Locale.getDefault(), "%.0f", gco2));
        binding.tvTituloHeader.setText(String.format(Locale.getDefault(), "Consumo (RPM avg: %.0f)", rpmAvg));
        detectarEventos(recs);
    }

    private void detectarEventos(List<TelemetryRecord> recs) {
        if (recs.size() < 2) return;
        for (int i = 1; i < recs.size(); i++) {
            TelemetryRecord prev = recs.get(i);
            TelemetryRecord cur = recs.get(i - 1);
            if (prev.rpm != null && cur.rpm != null) {
                int delta = cur.rpm - prev.rpm;
                if (delta > 1500) {
                    String msg = "Aceleración brusca detectada: " + cur.timestamp;
                    if (!listaAlertas.contains(msg)) agregarNuevaAlerta(msg);
                } else if (delta < -1500) {
                    String msg = "Frenado fuerte detectado: " + cur.timestamp;
                    if (!listaAlertas.contains(msg)) agregarNuevaAlerta(msg);
                }
            }
            if (prev.fuelRate != null && cur.fuelRate != null) {
                double dFuel = cur.fuelRate - prev.fuelRate;
                if (dFuel > 2.0) {
                    String msg = "Aumento súbito de consumo detectado: " + cur.timestamp;
                    if (!listaAlertas.contains(msg)) agregarNuevaAlerta(msg);
                }
            }
        }
    }

    public void agregarNuevaAlerta(String mensajeNotificacion) {
        if (listaAlertas != null && adapter != null) {
            listaAlertas.add(0, mensajeNotificacion);
            adapter.notifyItemInserted(0);
            binding.rvNotificaciones.scrollToPosition(0);
        }
    }

    private void ejecutarSimulacionDemo() {
        simTick++;
        int gco2Simulado = 140 + (int) (Math.sin(simTick) * 12) + (simTick % 3);
        int rpmSimulada = 2100 + (int) (Math.sin(simTick) * 350);

        binding.tvValorConsumo.setText(String.valueOf(gco2Simulado));
        binding.tvTituloHeader.setText(String.format(Locale.getDefault(), "Consumo Demo (RPM avg: %d)", rpmSimulada));
        switch (simTick) {
            case 1:
                agregarNuevaAlerta("Aceleración brusca detectada\nSe registró un pico súbito de +1800 RPM.");
                break;
            case 2:
                agregarNuevaAlerta("Aumento de consumo\nEl flujo de combustible superó los 4.5 L/h momentáneamente.");
                break;
            case 3:
                agregarNuevaAlerta("Conducción eficiente\n¡Excelente! Mantienes una marcha estable y estás ahorrando.");
                break;
            default:if (simTick % 5 == 0) {
                agregarNuevaAlerta("Frenado fuerte detectado\nReducción de velocidad drástica por debajo del promedio.");
            }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(refrescarRunnable);
        binding = null;
    }

}