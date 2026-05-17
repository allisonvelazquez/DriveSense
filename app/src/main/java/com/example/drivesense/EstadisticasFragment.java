package com.example.drivesense;

import android.os.Bundle;
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

public class EstadisticasFragment extends Fragment {

    private FragmentEstadisticasBinding binding;
    private NotificacionesAdapter adapter;
    private List<String> listaAlertas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEstadisticasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(super.getContext() != null ? view : null, savedInstanceState);

        // 1. Inicializar lista de datos base
        listaAlertas = new ArrayList<>();
        listaAlertas.add("Aceleración brusca:\n+10% consumo en Av. Vallarta");
        listaAlertas.add("Frenado fuerte detectado:\nPérdida de energía cinética ineficiente");

        // 2. Configurar el RecyclerView
        adapter = new NotificacionesAdapter(listaAlertas);
        binding.rvNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvNotificaciones.setAdapter(adapter);

        // 3. Simulación al presionar el medidor: Agrega una nueva alerta al inicio del feed
        binding.layoutMedidor.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Leyendo flujo OBD-II...", Toast.LENGTH_SHORT).show();

            // Alternamos dinámicamente un evento eficiente y uno ineficiente
            if (listaAlertas.size() % 2 == 0) {
                agregarNuevaAlerta("Conducción Eficiente:\nAhorrando un 12% de CO2 actualmente");
                binding.tvValorConsumo.setText("115");
            } else {
                agregarNuevaAlerta("Motor en ralentí prolongado:\nConsumo innecesario detectado en semáforo");
                binding.tvValorConsumo.setText("158");
            }
        });

        // 4. Botón para limpiar las alertas de la pantalla
        binding.btnLimpiarAlertas.setOnClickListener(v -> {
            int tamaño = listaAlertas.size();
            listaAlertas.clear();
            adapter.notifyItemRangeRemoved(0, tamaño);
            Toast.makeText(getContext(), "Feed de alertas despejado", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Permite insertar una nueva notificación en tiempo real al principio del feed
     */
    public void agregarNuevaAlerta(String mensajeNotificacion) {
        if (listaAlertas != null && adapter != null) {
            listaAlertas.add(0, mensajeNotificacion); // Inserta arriba de todo
            adapter.notifyItemInserted(0);
            binding.rvNotificaciones.scrollToPosition(0); // Mueve la lista al inicio automáticamente
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}