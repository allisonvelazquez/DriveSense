package com.example.drivesense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ViajeAdapter extends RecyclerView.Adapter<ViajeAdapter.ViewHolder> {

    private final List<Viaje> items;

    public ViajeAdapter(List<Viaje> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViajeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viaje, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViajeAdapter.ViewHolder holder, int position) {
        Viaje v = items.get(position);
        holder.tvInicio.setText(v.inicioTexto);
        holder.tvRuta.setText(v.rutaTexto);
        holder.tvResumen.setText(v.resumen);
        holder.tvKm.setText(String.format("%.1f km", v.kmEstimados));
        holder.tvGco2.setText(String.format("%.1f gCO2", v.gco2Estimado));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInicio, tvRuta, tvResumen, tvKm, tvGco2;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInicio = itemView.findViewById(R.id.tvInicioViaje);
            tvRuta = itemView.findViewById(R.id.tvRutaViaje);
            tvResumen = itemView.findViewById(R.id.tvResumenViaje);
            tvKm = itemView.findViewById(R.id.tvKmViaje);
            tvGco2 = itemView.findViewById(R.id.tvGco2Viaje);
        }
    }
}
