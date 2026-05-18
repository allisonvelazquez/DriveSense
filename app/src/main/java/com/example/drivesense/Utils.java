package com.example.drivesense;

import java.util.List;

public class Utils {
    public static double estimarGCO2(List<TelemetryRecord> recs) {
        if (recs == null || recs.isEmpty()) return 0.0;
        // Intentar usar fuelRate promedio (L/h)
        double sumFuel = 0.0;
        int countFuel = 0;
        for (TelemetryRecord r : recs) {
            if (r.fuelRate != null) {
                sumFuel += r.fuelRate;
                countFuel++;
            }
        }
        if (countFuel > 0) {
            double avgFuelRate = sumFuel / countFuel; // L/h
            double assumedSpeedKmh = 50.0;
            double litersPerKm = avgFuelRate / assumedSpeedKmh; // L/km
            double gCO2PerLiter = 2392.0; // g CO2 / L gasolina (aprox.)
            return litersPerKm * gCO2PerLiter;
        }

        // Si no hay fuelRate, usar RPM como proxy (heurística)
        double sumRpm = 0.0;
        int countRpm = 0;
        for (TelemetryRecord r : recs) {
            if (r.rpm != null) {
                sumRpm += r.rpm;
                countRpm++;
            }
        }
        if (countRpm == 0) return 0.0;
        double avgRpm = sumRpm / countRpm;
        // mapear RPM a consumo L/100km (demo)
        double lPer100km = 4.0 + (avgRpm - 800.0) * (8.0 / (3000.0 - 800.0));
        if (lPer100km < 0) lPer100km = 0;
        double litersPerKm = lPer100km / 100.0;
        double gCO2PerLiter = 2392.0;
        return litersPerKm * gCO2PerLiter;
    }
}
