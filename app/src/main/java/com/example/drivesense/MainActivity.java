package com.example.drivesense;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.drivesense.databinding.ActivityMainBinding;
import androidx.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            replaceFragment(new EstadisticasFragment());
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.buscar) {
                replaceFragment(new BuscarRutaFragment());
            } else if (id == R.id.registro) {
                replaceFragment(new RegistroViajesFragment());
            } else if (id == R.id.estadisticas) {
                replaceFragment(new EstadisticasFragment());
            } else if (id == R.id.perfil) {
                replaceFragment(new PerfilFragment());
            }

            return true;
        });
        handleIncomingIntent(getIntent());
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }
    private void handleIncomingIntent(@Nullable Intent intent) {
        if (intent == null) return;
        String open = intent.getStringExtra("open_fragment");
        String focus = intent.getStringExtra("focus_field");
        if ("buscar_ruta".equals(open)) {
            BuscarRutaFragment frag = new BuscarRutaFragment();
            Bundle args = new Bundle();
            if (focus != null) args.putString("focus_field", focus);
            frag.setArguments(args);
            replaceFragment(frag);
            binding.bottomNavigationView.setSelectedItemId(R.id.buscar);
        }
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}