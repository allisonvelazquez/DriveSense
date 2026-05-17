package com.example.drivesense;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ViajeAdapter extends RecyclerView.Adapter<ViajeAdapter.ViajeViewHolder> {

    private final List<Viaje> listaViajes;

    public ViajeAdapter(List<Viaje> listaViajes) {
        this.listaViajes = listaViajes;
    }

    @NonNull
    @Override
    public ViajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viaje, parent, false);
        return new ViajeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViajeViewHolder holder, int position) {
        Viaje viaje = listaViajes.get(position);

        holder.tvFecha.setText(viaje.getFecha());
        holder.tvRuta.setText(viaje.getRuta());
        holder.tvDetalle.setText(viaje.getInfoDistancia());
        holder.tvCO2.setText(String.valueOf(viaje.getEmisionCO2()));

        if (viaje.isEsEficiente()) {
            holder.viewColor.setBackgroundColor(Color.parseColor("#137333"));
            holder.tvCO2.setTextColor(Color.parseColor("#137333"));
        } else {
            holder.viewColor.setBackgroundColor(Color.parseColor("#A51D24"));
            holder.tvCO2.setTextColor(Color.parseColor("#A51D24"));
        }
    }

    @Override
    public int getItemCount() {
        return listaViajes.size();
    }

    public static class ViajeViewHolder extends RecyclerView.ViewHolder {
        View viewColor;
        TextView tvFecha, tvRuta, tvDetalle, tvCO2;

        public ViajeViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColor = itemView.findViewById(R.id.viewStatusColor);
            tvFecha = itemView.findViewById(R.id.tvViajeFecha);
            tvRuta = itemView.findViewById(R.id.tvViajeRuta);
            tvDetalle = itemView.findViewById(R.id.tvViajeDetalleDistancia);
            tvCO2 = itemView.findViewById(R.id.tvViajeValorCO2);
        }
    }
}