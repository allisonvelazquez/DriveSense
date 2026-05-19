package com.example.drivesense;

import android.database.Cursor;
import androidx.annotation.Nullable;
public class TelemetryRecord {
    public int id;
    public int vehicleId;
    @Nullable public String timestamp;
    @Nullable public Integer rpm;
    @Nullable public Double throttle;
    @Nullable public Double maf;
    @Nullable public Double engineLoad;
    @Nullable public Double fuelRate;
    @Nullable public Double lat;
    @Nullable public Double lon;

    public TelemetryRecord() {}
    public static TelemetryRecord fromCursor(Cursor c) {
        TelemetryRecord r = new TelemetryRecord();
        r.id = c.getInt(c.getColumnIndexOrThrow("id"));
        r.vehicleId = c.getInt(c.getColumnIndexOrThrow("vehicle_id"));
        r.timestamp = c.isNull(c.getColumnIndexOrThrow("timestamp")) ? null : c.getString(c.getColumnIndexOrThrow("timestamp"));
        // rpm
        if (!c.isNull(c.getColumnIndexOrThrow("rpm"))) {
            r.rpm = c.getInt(c.getColumnIndexOrThrow("rpm"));
        } else {
            r.rpm = null;
        }
        // throttle
        if (!c.isNull(c.getColumnIndexOrThrow("throttle"))) {
            r.throttle = c.getDouble(c.getColumnIndexOrThrow("throttle"));
        } else {
            r.throttle = null;
        }
        // maf
        if (!c.isNull(c.getColumnIndexOrThrow("maf"))) {
            r.maf = c.getDouble(c.getColumnIndexOrThrow("maf"));
        } else {
            r.maf = null;
        }
        // engine_load
        if (!c.isNull(c.getColumnIndexOrThrow("engine_load"))) {
            r.engineLoad = c.getDouble(c.getColumnIndexOrThrow("engine_load"));
        } else {
            r.engineLoad = null;
        }
        // fuel_rate -> fuelRate
        if (!c.isNull(c.getColumnIndexOrThrow("fuel_rate"))) {
            r.fuelRate = c.getDouble(c.getColumnIndexOrThrow("fuel_rate"));
        } else {
            r.fuelRate = null;
        }
        // lat / lon
        if (!c.isNull(c.getColumnIndexOrThrow("lat"))) {
            r.lat = c.getDouble(c.getColumnIndexOrThrow("lat"));
        } else {
            r.lat = null;
        }
        if (!c.isNull(c.getColumnIndexOrThrow("lon"))) {
            r.lon = c.getDouble(c.getColumnIndexOrThrow("lon"));
        } else {
            r.lon = null;
        }
        return r;
    }
}
