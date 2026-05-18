package com.example.drivesense;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogIn extends AppCompatActivity implements View.OnClickListener{
    public Button b;
    private EditText Email, Contrasena;
    private Button Iniciar;
    private Basededatos basededatos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        b = (Button) findViewById(R.id.button2);
        Email = findViewById(R.id.etEmail);
        Contrasena = findViewById(R.id.etPassword);
        Iniciar = findViewById(R.id.btnSingIn);
        basededatos = new Basededatos(this);
        Iniciar. setOnClickListener(view -> iniciarSesion());
        b.setOnClickListener(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public void onClick(View view) {
        int id= view.getId();
        if(id == R.id.button2){
            Intent intent = new Intent(this, Registro.class);
            startActivity(intent);
        }
    }
    private void iniciarSesion(){
        String correo = Email.getText().toString().trim();
        String contrasena = Contrasena.getText().toString().trim();
        if(TextUtils.isEmpty(correo)|| TextUtils.isEmpty(contrasena)){
            Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean valido = basededatos.verificarUsuario(correo, contrasena);
        if (valido) {
            int userId = basededatos.obtenerIdUsuarioPorCorreo(correo);
            SharedPreferences prefs = getSharedPreferences("drivesense_prefs", MODE_PRIVATE);
            prefs.edit()
                    .putInt("user_id", userId)
                    .putString("user_email", correo)
                    .apply();

            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LogIn.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}