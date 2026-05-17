package com.example.drivesense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
// Este import cambiará automáticamente según el nombre de tu archivo XML
import com.example.drivesense.databinding.FragmentRegistroViajesBinding;
import java.util.ArrayList;
import java.util.List;

public class RegistroViajesFragment extends Fragment {

    private FragmentRegistroViajesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos usando el binding específico de Registro de Viajes
        binding = FragmentRegistroViajesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inyectar datos en las tarjetas de estadísticas globales (Kpis)
        binding.tvKpiDistancia.setText("184 km");
        binding.tvKpiAhorro.setText("5.2 kg");
        binding.tvKpiScore.setText("91/100");

        // 2. Generar lista de trayectos simulados
        List<Viaje> historial = new ArrayList<>();
        historial.add(new Viaje("Hoy, 08:15", "CUCEI ➔ Plaza del Sol", "11.2 km  •  22 min", 114, true));
        historial.add(new Viaje("Ayer, 19:40", "La Minerva ➔ Providencia", "5.8 km  •  14 min", 148, false));
        historial.add(new Viaje("14 May, 13:10", "Plaza Patria ➔ Centro Histórico", "8.1 km  •  19 min", 121, true));
        historial.add(new Viaje("12 May, 09:05", "Chapultepec ➔ Tlaquepaque Centro", "12.4 km  •  28 min", 116, true));

        // 3. Vincular el adaptador al RecyclerView
        ViajeAdapter adapter = new ViajeAdapter(historial);
        binding.rvHistorialViajes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistorialViajes.setAdapter(adapter);

        // Evita conflictos de scroll dentro del NestedScrollView
        binding.rvHistorialViajes.setNestedScrollingEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Liberamos el binding para evitar fugas de memoria
        binding = null;
    }
}