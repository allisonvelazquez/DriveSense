package com.example.drivesense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivesense.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private boolean isConnected = false;
    private Basededatos db;
    private int userId;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new Basededatos(getContext());
        prefs = requireActivity().getSharedPreferences("drivesense_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            startActivity(new Intent(getActivity(), LogIn.class));
            requireActivity().finish();
            return;
        }

        binding.btnReplaceVehicle.setOnClickListener(v -> confirmReplaceVehicle());
        binding.btnBluetooth.setOnClickListener(v -> toggleObdConnection());

        loadProfile();
    }

    private void loadProfile() {
        String name = db.obtenerNombreUsuario(userId);
        String email = prefs.getString("user_email", "");
        binding.tvNombreUsuario.setText(name != null ? name : "Usuario");
        binding.tvCorreoUsuario.setText(email);

        int defaultVid = db.getDefaultVehicleIdForUser(userId);
        if (defaultVid > 0) {
            Cursor vc = db.obtenerVehiclePorId(defaultVid);
            if (vc != null && vc.moveToFirst()) {
                String make = vc.getString(vc.getColumnIndexOrThrow("make"));
                String model = vc.getString(vc.getColumnIndexOrThrow("model"));
                int year = vc.isNull(vc.getColumnIndexOrThrow("year")) ? 0 : vc.getInt(vc.getColumnIndexOrThrow("year"));
                binding.tvModeloAuto.setText(make + " " + model + (year > 0 ? " (" + year + ")" : ""));
                vc.close();
            }

            binding.layoutInfoVehiculo.setVisibility(View.VISIBLE);
            binding.tvSinVehiculo.setVisibility(View.GONE);
            binding.btnReplaceVehicle.setVisibility(View.VISIBLE);

            prefs.edit().putInt("selected_vehicle_id", defaultVid).apply();
        } else {
            binding.layoutInfoVehiculo.setVisibility(View.GONE);
            binding.tvSinVehiculo.setVisibility(View.VISIBLE);
            binding.btnReplaceVehicle.setVisibility(View.GONE);

            prefs.edit().remove("selected_vehicle_id").apply();
        }
    }
    private void showAddVehicleDialogAndSetDefault() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View form = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_vehicle, null);
        EditText etVin = form.findViewById(R.id.etVin);
        EditText etMake = form.findViewById(R.id.etMake);
        EditText etModel = form.findViewById(R.id.etModel);
        EditText etYear = form.findViewById(R.id.etYear);

        builder.setTitle("Agregar vehículo")
                .setView(form)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String vin = etVin.getText().toString().trim();
                    String make = etMake.getText().toString().trim();
                    String model = etModel.getText().toString().trim();
                    Integer year = null;
                    try { year = Integer.parseInt(etYear.getText().toString().trim()); } catch (Exception ignored) {}
                    long id = db.upsertVehicleForUser(userId, vin, make, model, year);
                    if (id > 0) {
                        prefs.edit().putInt("selected_vehicle_id", (int) id).apply();
                        Toast.makeText(getContext(), "Vehículo vinculado y seleccionado", Toast.LENGTH_SHORT).show();
                        loadProfile();
                    } else {
                        Toast.makeText(getContext(), "Error al agregar/actualizar vehículo", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmReplaceVehicle() {
        new AlertDialog.Builder(getContext())
                .setTitle("Reemplazar vehículo")
                .setMessage("¿Deseas reemplazar el vehículo vinculado? Se creará uno nuevo y se desvinculará el anterior.")
                .setPositiveButton("Sí", (d, w) -> showAddVehicleDialogAndSetDefault())
                .setNegativeButton("No", null)
                .show();
    }

    private void toggleObdConnection() {
        if (!isConnected) {
            isConnected = true;
            binding.tvEstadoDispositivo.setText("ELM327 - Conectado");
            binding.tvEstadoDispositivo.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            binding.layoutProtocolo.setVisibility(View.VISIBLE);

            int defaultVid = db.getDefaultVehicleIdForUser(userId);
            if (defaultVid <= 0) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Vincular vehículo")
                        .setMessage("No tienes un vehículo vinculado. ¿Deseas crear uno ahora?")
                        .setPositiveButton("Crear", (d, w) -> showAddVehicleDialogAndSetDefault())
                        .setNegativeButton("Más tarde", null)
                        .show();
            } else {
                prefs.edit().putInt("selected_vehicle_id", defaultVid).apply();
                Toast.makeText(getContext(), "Vehículo activo: ID " + defaultVid, Toast.LENGTH_SHORT).show();
            }
        } else {
            isConnected = false;
            binding.tvEstadoDispositivo.setText("ELM327 - Desconectado");
            binding.tvEstadoDispositivo.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            binding.layoutProtocolo.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Dispositivo desconectado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
