package com.example.drivesense;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivesense.databinding.FragmentBuscarRutaBinding;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;



public class BuscarRutaFragment extends Fragment {

    private FragmentBuscarRutaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBuscarRutaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText etUbi = view.findViewById(R.id.etBuscarDestino);
        Bundle args = getArguments();
        if (args != null && "etBuscarDestino".equals(args.getString("focus_field"))) {
            etUbi.post(() -> {
                etUbi.requestFocus();
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(etUbi, InputMethodManager.SHOW_IMPLICIT);
            });
        }
        binding.etBuscarDestino.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    String destino = binding.etBuscarDestino.getText().toString().trim();

                    if (!destino.isEmpty()) {
                        binding.cardAccionRuta.setVisibility(View.VISIBLE);
                        binding.tvTagEco.setText("EcoRuta hacia " + destino);
                        Toast.makeText(getContext(), "Calculando la ruta más ecológica...", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.cardAccionRuta.setVisibility(View.GONE);
                    }
                    return false;
                }
                return false;
            }
        });
        binding.btnIniciarRuta.setOnClickListener(v -> {
            String destino = binding.etBuscarDestino.getText().toString().trim();
            if (!destino.isEmpty()) {
                Toast.makeText(getContext(), "¡Navegación iniciada hacia " + destino + "!", Toast.LENGTH_LONG).show();
                //  fragmento de la ruta ???????
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}