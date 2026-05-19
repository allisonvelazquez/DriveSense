package com.example.drivesense;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.drivesense.databinding.FragmentRegistroViajesBinding;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistroViajesFragment extends Fragment {

    private FragmentRegistroViajesBinding binding;
    private Basededatos db;
    private int selectedVehicleId = -1;
    private static final long GAP_MS = 5 * 60 * 1000L; // 5 minutos

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistroViajesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new Basededatos(requireContext());

        SharedPreferences prefs = requireActivity().getSharedPreferences("drivesense_prefs", getContext().MODE_PRIVATE);
        selectedVehicleId = prefs.getInt("selected_vehicle_id", -1);

        binding.rvHistorialViajes.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Viaje> viajes = reconstruirViajes(selectedVehicleId);
        if (viajes.isEmpty()) {
            viajes = obtenerViajesDeDemostracion();
        }
        ViajeAdapter adapter = new ViajeAdapter(viajes);
        binding.rvHistorialViajes.setAdapter(adapter);

        double totalKm = 0;
        double totalGco2 = 0;
        int score = 0;
        for (Viaje v : viajes) {
            totalKm += v.kmEstimados;
            totalGco2 += v.gco2Estimado;
            score += Math.max(0, Math.min(100, (int)(100 - v.kmEstimados)));
        }
        binding.tvKpiDistancia.setText(String.format(Locale.getDefault(), "%.1f km", totalKm));
        binding.tvKpiAhorro.setText(String.format(Locale.getDefault(), "%.2f kg", totalGco2 / 1000.0)); // g -> kg
        binding.tvKpiScore.setText(viajes.isEmpty() ? "--/100" : String.format(Locale.getDefault(), "%d/100", score / Math.max(1, viajes.size())));
    }
    private List<Viaje> reconstruirViajes(int vehicleId) {
        List<Viaje> salida = new ArrayList<>();
        if (vehicleId == -1) return salida;

        Cursor c = db.obtenerTelemetriaPorVehiculoOrdenAsc(vehicleId);
        if (c == null || c.getCount() == 0) {
            if (c != null) c.close();
            return salida;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        List<TelemetryRecord> buffer = new ArrayList<>();
        long lastTsMs = -1;

        while (c.moveToNext()) {
            TelemetryRecord r = TelemetryRecord.fromCursor(c);
            long tsMs = parseTimestampToMillis(r.timestamp, sdf);
            if (lastTsMs == -1) {
                buffer.add(r);
                lastTsMs = tsMs;
                continue;
            }
            if (tsMs - lastTsMs > GAP_MS) {
                Viaje v = summarizeBufferAsViaje(buffer, sdf);
                if (v != null) salida.add(v);
                buffer.clear();
            }
            buffer.add(r);
            lastTsMs = tsMs;
        }
        if (!buffer.isEmpty()) {
            Viaje v = summarizeBufferAsViaje(buffer, sdf);
            if (v != null) salida.add(v);
        }
        c.close();
        return salida;
    }

    private long parseTimestampToMillis(String ts, SimpleDateFormat sdf) {
        if (ts == null) return 0;
        try {
            Date d = sdf.parse(ts);
            return d != null ? d.getTime() : 0;
        } catch (ParseException e) {
            return 0;
        }
    }
    private Viaje summarizeBufferAsViaje(List<TelemetryRecord> buffer, SimpleDateFormat sdf) {
        if (buffer == null || buffer.isEmpty()) return null;
        double totalKm = 0.0;
        TelemetryRecord prev = null;
        for (TelemetryRecord r : buffer) {
            if (prev != null && prev.lat != null && prev.lon != null && r.lat != null && r.lon != null) {
                totalKm += haversineKm(prev.lat, prev.lon, r.lat, r.lon);
            }
            prev = r;
        }
        double gco2 = Utils.estimarGCO2(buffer) * Math.max(1.0, totalKm); // ajuste demo

        String inicio = buffer.get(0).timestamp != null ? buffer.get(0).timestamp.replace('T', ' ') : "—";
        String resumen = String.format(Locale.getDefault(), "%.1f km  •  %d min",
                totalKm,
                (int)((parseTimestampToMillis(buffer.get(buffer.size()-1).timestamp, sdf) - parseTimestampToMillis(buffer.get(0).timestamp, sdf)) / 60000L)
        );
        String ruta = "Inicio ➔ Fin";
        return new Viaje(inicio, ruta, resumen, totalKm, gco2);
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private List<Viaje> obtenerViajesDeDemostracion() {
        List<Viaje> demo = new ArrayList<>();
        demo.add(new Viaje(
                "2026-05-18 19:40:10",
                "Coyula ➔ Cucei",
                "11.8 km  •  30 min",
                11.8,
                1200.0
        ));

        demo.add(new Viaje(
                "2026-05-17 14:15:00",
                "Gran Terraza ➔ Bodega Aurrera",
                "8.7 km  •  24 min",
                8.7,
                610.0
        ));

        demo.add(new Viaje(
                "2026-05-16 08:30:22",
                "Rio Nilo ➔ Coyula",
                "8.3 km  •  22 min",
                8.3,
                600.0 // en gramos de CO2
        ));

        return demo;
    }
}