package com.example.drivesense;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Registro extends AppCompatActivity {

    private EditText Nombre, Correo, Contrasena;
    private Button Registrar;
    private Basededatos basededatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Nombre = findViewById(R.id.editTextText);
        Correo = findViewById(R.id.editTextTextEmailAddress);
        Contrasena = findViewById(R.id.editTextTextPassword);
        Registrar = findViewById(R.id.button);

        basededatos = new Basededatos(this);

        Registrar.setOnClickListener(view -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = Nombre.getText().toString().trim();
        String correo = Correo.getText().toString().trim();
        String contrasena = Contrasena.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasena)) {
            Toast.makeText(this, "Completa los campos faltantes", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean insertado = basededatos.insertarUsuario(nombre, correo, contrasena);

        if (insertado) {
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Registro.this, LogIn.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al registrar. El correo ya está en uso.", Toast.LENGTH_SHORT).show();
        }
    }
}