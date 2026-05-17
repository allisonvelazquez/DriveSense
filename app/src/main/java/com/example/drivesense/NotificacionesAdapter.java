package com.example.drivesense;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.ViewHolder> {

    private final List<String> listaAlertas;

    public NotificacionesAdapter(List<String> listaAlertas) {
        this.listaAlertas = listaAlertas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String alerta = listaAlertas.get(position);

        // Separamos el título del detalle usando un caracter de control si queremos (ej. un guión o salto)
        if (alerta.contains("\n")) {
            String[] partes = alerta.split("\n", 2);
            holder.tvMensaje.setText(partes[0]);
            holder.tvDetalle.setText(partes[1]);
        } else {
            holder.tvMensaje.setText(alerta);
            holder.tvDetalle.setText("Evento de telemetría OBD-II registrado.");
        }

        // Si la alerta es positiva (eficiente), cambiamos el color a verde de forma dinámica
        if (alerta.toLowerCase().contains("eficiente") || alerta.toLowerCase().contains("ahorrando")) {
            holder.cardFondo.setCardBackgroundColor(Color.parseColor("#E6F4EA"));
            holder.ivIcono.setColorFilter(Color.parseColor("#137333"));
            holder.tvMensaje.setTextColor(Color.parseColor("#137333"));
        } else {
            holder.cardFondo.setCardBackgroundColor(Color.parseColor("#FCE8E6"));
            holder.ivIcono.setColorFilter(Color.parseColor("#A51D24"));
            holder.tvMensaje.setTextColor(Color.parseColor("#A51D24"));
        }
    }

    @Override
    public int getItemCount() {
        return listaAlertas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje, tvDetalle;
        ImageView ivIcono;
        CardView cardFondo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvItemMensaje);
            tvDetalle = itemView.findViewById(R.id.tvItemDetalle);
            ivIcono = itemView.findViewById(R.id.ivItemIcono);
            cardFondo = (CardView) itemView;
        }
    }
}