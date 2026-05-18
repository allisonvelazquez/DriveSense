package com.example.drivesense;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

public class ResumenViaje extends AppCompatActivity {
    private Basededatos db;
    private int viajeId = -1;
    private TextView tvDistancia, tvTime, tvConsumption, tvGco2, tvScoreBig, tvScoreText, tvSavings, tvSavingsLabel;
    private Button btnNuevoViaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_viaje);
        db = new Basededatos(this);

        Toolbar toolbar = findViewById(R.id.toolbarResumen);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        }
        tvDistancia = findViewById(R.id.tvDistancia);
        tvTime = findViewById(R.id.tvTime);
        tvConsumption = findViewById(R.id.tvConsumption);
        tvGco2 = findViewById(R.id.tvGco2);
        tvScoreBig = findViewById(R.id.tvScoreBig);
        tvScoreText = findViewById(R.id.tvScoreText);
        tvSavings = findViewById(R.id.tvSavings);
        tvSavingsLabel = findViewById(R.id.tvSavingsLabel);
        btnNuevoViaje = findViewById(R.id.btnNuevoViaje);

        viajeId = getIntent().getIntExtra("trip_id", -1);
        loadViaje(viajeId);
        btnNuevoViaje.setOnClickListener(v->{
            Intent intent = new Intent(ResumenViaje.this, MainActivity.class);
            intent.putExtra("open_fragment", "buscar_ruta");
            intent.putExtra("focus_field", "etBuscarDestino");
            startActivity(intent);
            finish();
        });
    }
    private void loadViaje(int id){
        Cursor c = db.obtenerViajePorId(id);
        if(c==null||!c.moveToFirst()){
            if (c != null) c.close();
            Toast.makeText(this, "No se encontró el trayecto", Toast.LENGTH_SHORT).show();
            return;
        }
        double distance = c.isNull(c.getColumnIndexOrThrow("distance_km")) ? 0.0 : c.getDouble(c.getColumnIndexOrThrow("distance_km"));
        int duration = c.isNull(c.getColumnIndexOrThrow("duration_sec")) ? 0 : c.getInt(c.getColumnIndexOrThrow("duration_sec"));
        double consumption = c.isNull(c.getColumnIndexOrThrow("avg_consumption_l_per_100km")) ? 0.0 : c.getDouble(c.getColumnIndexOrThrow("avg_consumption_l_per_100km"));
        double gco2 = c.isNull(c.getColumnIndexOrThrow("gco2_grams")) ? 0.0 : c.getDouble(c.getColumnIndexOrThrow("gco2_grams"));
        int score = c.isNull(c.getColumnIndexOrThrow("score")) ? 0 : c.getInt(c.getColumnIndexOrThrow("score"));
        tvDistancia.setText(String.format(Locale.getDefault(), "%.1f km", distance));
        tvTime.setText(String.format(Locale.getDefault(), "%d min", duration / 60));
        tvConsumption.setText(String.format(Locale.getDefault(), "%.1f L / 100km", consumption));
        tvGco2.setText(String.format(Locale.getDefault(), "CO2: %.0f g", gco2));
        tvScoreBig.setText(String.format(Locale.getDefault(), "%d\n/100", score));
        tvScoreText.setText(score >= 85 ? "Excelente" : (score >= 70 ? "Bueno" : "Mejorable"));
        tvSavings.setText(String.format(Locale.getDefault(), "- $%.2f MXN", estimateSavings(distance, consumption)));
        tvSavingsLabel.setText("ahorrados en este trayecto");
        c.close();
        applyScoreGauge(tvScoreBig, score);
    }
    private double estimateSavings(double km, double consumptionLPer100) {
        double pricePerL = 24.0;
        double savedLPer100 = consumptionLPer100 * 0.1;
        double litersSaved = (savedLPer100 / 100.0) * km;
        return litersSaved * pricePerL;
    }
    private int getColorForScore(int score) {
        if (score >= 85) return ContextCompat.getColor(this, R.color.gauge_green);
        if (score >= 70) return ContextCompat.getColor(this, R.color.gauge_yellow);
        if (score >= 50) return ContextCompat.getColor(this, R.color.gauge_orange);
        return ContextCompat.getColor(this, R.color.gauge_red);
    }
    private void applyScoreGauge(TextView tvScore, int score) {
        int fillColor = getColorForScore(score);
        int strokeColor = darken(fillColor, 0.85f);
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setSize(dpToPx(120), dpToPx(120));
        gd.setColor(fillColor);
        gd.setStroke(dpToPx(4), strokeColor);
        tvScore.setBackground(gd);
        tvScore.setTextColor(getContrastingTextColor(fillColor));
        tvScore.setText(String.format(Locale.getDefault(), "%d\n/100", score));
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    private int darken(int color, float factor) {
        int a = (color >> 24) & 0xff;
        int r = (int)(((color >> 16) & 0xff) * factor);
        int g = (int)(((color >> 8) & 0xff) * factor);
        int b = (int)((color & 0xff) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    private int getContrastingTextColor(int bgColor) {
        int r = (bgColor >> 16) & 0xff;
        int g = (bgColor >> 8) & 0xff;
        int b = bgColor & 0xff;
        double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
        return luminance > 0.6 ? 0xFF000000 : 0xFFFFFFFF;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}