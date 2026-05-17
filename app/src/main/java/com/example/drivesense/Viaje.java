package com.example.drivesense;

public class Viaje {
    private String fecha;
    private String ruta;
    private String infoDistancia;
    private int emisionCO2;
    private boolean esEficiente;

    public Viaje(String fecha, String ruta, String infoDistancia, int emisionCO2, boolean esEficiente) {
        this.fecha = fecha;
        this.ruta = ruta;
        this.infoDistancia = infoDistancia;
        this.emisionCO2 = emisionCO2;
        this.esEficiente = esEficiente;
    }

    public String getFecha() { return fecha; }
    public String getRuta() { return ruta; }
    public String getInfoDistancia() { return infoDistancia; }
    public int getEmisionCO2() { return emisionCO2; }
    public boolean isEsEficiente() { return esEficiente; }
}