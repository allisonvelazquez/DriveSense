package com.example.drivesense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivesense.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private boolean isConnected = false; // Estado simulado de la conexión OBD-II

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inicializamos el View Binding para esta pantalla
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvNombreUsuario.setText("Allison Velazquez");
        binding.tvCorreoUsuario.setText("allison.velazquez@mail.com");
        binding.tvModeloAuto.setText("Honda Civic");
        binding.tvAnioAuto.setText("2022");

        // Listener del botón circular de Bluetooth
        binding.btnBluetooth.setOnClickListener(v -> {
            if (!isConnected) {
                // SIMULAMOS QUE SE CONECTA AL DISPOSITIVO ELM327
                isConnected = true;

                // 1. Cambiamos el texto del estado
                binding.tvEstadoDispositivo.setText("ELM327 - Conectado");
                binding.tvEstadoDispositivo.setBackgroundColor(getResources().getColor(android.R.color.black));

                // 2. Hacemos visible el cuadro verde del protocolo activo
                binding.layoutProtocolo.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), "Conectado exitosamente al OBD-II", Toast.LENGTH_SHORT).show();
            } else {
                // SIMULAMOS LA DESCONEXIÓN
                isConnected = false;

                binding.tvEstadoDispositivo.setText("ELM327 - Desconectado");
                binding.tvEstadoDispositivo.setBackgroundColor(android.graphics.Color.parseColor("#333333"));
                binding.layoutProtocolo.setVisibility(View.GONE);

                Toast.makeText(getContext(), "Dispositivo desconectado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Liberamos el binding para evitar fugas de memoria en Android
        binding = null;
    }
}