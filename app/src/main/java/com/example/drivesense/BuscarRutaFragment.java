package com.example.drivesense;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivesense.databinding.FragmentBuscarRutaBinding;

public class BuscarRutaFragment extends Fragment {

    private FragmentBuscarRutaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos el diseño utilizando View Binding
        binding = FragmentBuscarRutaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Captura la acción de "Buscar" o "Enter" en el teclado virtual
        binding.etBuscarDestino.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Se activa con el botón Buscar del teclado o con la tecla Enter física/virtual
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                    String destino = binding.etBuscarDestino.getText().toString().trim();

                    if (!destino.isEmpty()) {
                        // Hace visible la tarjeta inferior dinámicamente
                        binding.cardAccionRuta.setVisibility(View.VISIBLE);

                        // Actualiza el encabezado de la EcoRuta con el destino deseado
                        binding.tvTagEco.setText("EcoRuta hacia " + destino);

                        Toast.makeText(getContext(), "Calculando la ruta más ecológica...", Toast.LENGTH_SHORT).show();
                    } else {
                        // Si el campo queda vacío, vuelve a ocultar la tarjeta
                        binding.cardAccionRuta.setVisibility(View.GONE);
                    }
                    return false; // Retornar false cierra el teclado automáticamente
                }
                return false;
            }
        });

        // Configura la acción al presionar el botón verde de "Iniciar Ruta"
        binding.btnIniciarRuta.setOnClickListener(v -> {
            String destino = binding.etBuscarDestino.getText().toString().trim();

            if (!destino.isEmpty()) {
                Toast.makeText(getContext(), "¡Navegación iniciada hacia " + destino + "!", Toast.LENGTH_LONG).show();

                // [Espacio Futuro]: Aquí programarás la transición al fragmento
                // que lee el OBD-II o despliega el velocímetro/consumo en tiempo real.
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Liberación crucial del binding para evitar fugas de memoria (Memory Leaks) en Android
        binding = null;
    }
}