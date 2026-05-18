package com.example.drivesense;

public class Viaje {
    public long startTsIndex;
    public long endTsIndex;
    public String inicioTexto;
    public String rutaTexto;
    public String resumen;
    public double kmEstimados;
    public double gco2Estimado;

    public Viaje(String inicioTexto, String rutaTexto, String resumen, double kmEstimados, double gco2Estimado) {
        this.inicioTexto = inicioTexto;
        this.rutaTexto = rutaTexto;
        this.resumen = resumen;
        this.kmEstimados = kmEstimados;
        this.gco2Estimado = gco2Estimado;
    }
}
