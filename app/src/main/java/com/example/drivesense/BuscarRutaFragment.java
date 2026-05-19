package com.example.drivesense;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class BuscarRutaFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    private AutocompleteSupportFragment autocompleteOrigen;
    private AutocompleteSupportFragment autocompleteDestino;

    private Button btnAgregarOrigen;
    private Button btnAgregarDestino;
    private Button btnCalcular;

    private LatLng origenLatLng;
    private LatLng destinoLatLng;

    private String origenTexto = "";
    private String destinoTexto = "";

    private Marker marcadorOrigen;
    private Marker marcadorDestino;

    public BuscarRutaFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_buscar_ruta,
                container,
                false
        );

        String apiKey = "AIzaSyCIgMOzVfpak7gOdYa4mEuOS1vrQGqeBOI";

        if (!Places.isInitialized()) {

            Places.initialize(
                    requireContext(),
                    apiKey
            );
        }

        btnAgregarOrigen = view.findViewById(R.id.btnAgregarOrigen);
        btnAgregarDestino = view.findViewById(R.id.btnAgregarDestino);
        btnCalcular = view.findViewById(R.id.btnCalcular);

        autocompleteOrigen = (AutocompleteSupportFragment)
                getChildFragmentManager()
                        .findFragmentById(R.id.autocompleteOrigen);

        autocompleteDestino = (AutocompleteSupportFragment)
                getChildFragmentManager()
                        .findFragmentById(R.id.autocompleteDestino);

        if (autocompleteOrigen != null) {

            autocompleteOrigen.setPlaceFields(Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
            ));

            autocompleteOrigen.setOnPlaceSelectedListener(
                    new PlaceSelectionListener() {

                        @Override
                        public void onPlaceSelected(@NonNull Place place) {

                            origenLatLng = place.getLatLng();

                            if (place.getAddress() != null) {
                                origenTexto = place.getAddress();
                            } else {
                                origenTexto = place.getName();
                            }

                            Toast.makeText(
                                    requireContext(),
                                    "Origen seleccionado",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        @Override
                        public void onError(@NonNull Status status) {

                            Toast.makeText(
                                    requireContext(),
                                    "Error origen: "
                                            + status.getStatusMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
        }

        if (autocompleteDestino != null) {

            autocompleteDestino.setPlaceFields(Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
            ));

            autocompleteDestino.setOnPlaceSelectedListener(
                    new PlaceSelectionListener() {

                        @Override
                        public void onPlaceSelected(@NonNull Place place) {

                            destinoLatLng = place.getLatLng();

                            if (place.getAddress() != null) {
                                destinoTexto = place.getAddress();
                            } else {
                                destinoTexto = place.getName();
                            }

                            Toast.makeText(
                                    requireContext(),
                                    "Destino seleccionado",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        @Override
                        public void onError(@NonNull Status status) {

                            Toast.makeText(
                                    requireContext(),
                                    "Error destino: "
                                            + status.getStatusMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
        }

        btnAgregarOrigen.setOnClickListener(v -> agregarOrigenEnMapa());

        btnAgregarDestino.setOnClickListener(v -> agregarDestinoEnMapa());

        btnCalcular.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireContext(),
                    ResumenViaje.class
            );

            startActivity(intent);
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment)
                        getChildFragmentManager()
                                .findFragmentById(R.id.mapContainer);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        LatLng inicio = new LatLng(20.6597, -103.3496);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(inicio, 12f)
        );
    }

    private void agregarOrigenEnMapa() {

        if (mMap == null) return;

        if (origenLatLng == null) {

            Toast.makeText(
                    requireContext(),
                    "Selecciona un origen",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (marcadorOrigen != null) {
            marcadorOrigen.remove();
        }

        marcadorOrigen = mMap.addMarker(
                new MarkerOptions()
                        .position(origenLatLng)
                        .title("Origen")
                        .snippet(origenTexto)
        );

        moverCamara();
    }

    private void agregarDestinoEnMapa() {

        if (mMap == null) return;

        if (destinoLatLng == null) {

            Toast.makeText(
                    requireContext(),
                    "Selecciona un destino",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (marcadorDestino != null) {
            marcadorDestino.remove();
        }

        marcadorDestino = mMap.addMarker(
                new MarkerOptions()
                        .position(destinoLatLng)
                        .title("Destino")
                        .snippet(destinoTexto)
        );

        moverCamara();
    }

    private void moverCamara() {

        if (mMap == null) return;

        if (origenLatLng != null && destinoLatLng != null) {

            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(origenLatLng)
                    .include(destinoLatLng)
                    .build();

            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(bounds, 150)
            );

        } else if (origenLatLng != null) {

            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            origenLatLng,
                            15f
                    )
            );

        } else if (destinoLatLng != null) {

            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            destinoLatLng,
                            15f
                    )
            );
        }
    }
}