package com.example.drivesense;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Basededatos extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "drivesense.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_USUARIOS = "usuarios";
    public static final String Usuario_ID = "id_usuario";
    public static final String Usuario_NOMBRE = "nombre";
    public static final String Usuario_CORREO = "correo";
    public static final String Usuario_CONTRASENA = "contrasena";

    public Basededatos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsuarios =
                "CREATE TABLE " + TABLE_USUARIOS + " (" +
                        Usuario_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Usuario_NOMBRE + " TEXT NOT NULL, " +
                        Usuario_CORREO + " TEXT UNIQUE NOT NULL, " +
                        Usuario_CONTRASENA + " TEXT NOT NULL," +
                        "default_vehicle_id INTEGER DEFAULT -1" +
                        ");";
        db.execSQL(createUsuarios);

        String createVehicles =
                "CREATE TABLE vehicles(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "vin TEXT," +
                        "make TEXT," +
                        "model TEXT," +
                        "year INTEGER" +
                        ");";
        db.execSQL(createVehicles);

        String createTelemetry =
                "CREATE TABLE telemetry (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "vehicle_id INTEGER NOT NULL," +
                        "timestamp TEXT NOT NULL," +
                        "rpm INTEGER," +
                        "throttle REAL," +
                        "maf REAL," +
                        "engine_load REAL," +
                        "fuel_rate REAL," +
                        "lat REAL," +
                        "lon REAL," +
                        "synced INTEGER DEFAULT 0," +
                        "viaje_id INTEGER DEFAULT -1"+
                        ");";
        db.execSQL(createTelemetry);
        String createViaje =
                "CREATE TABLE viajes ("+
                        "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        "vehicle_id INTEGER NOT NULL,"+
                        "start_ts TEXT NOT NULL,"+
                        "end_ts TEXT NOT NULL,"+
                        "distance_km REAL,"+
                        "duration INTEGER,"+
                        "avg_consumption REAL,"+
                        "gco2 REAL,"+
                        "score INTEGER"+
                        ");";
        db.execSQL(createViaje);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS vehicles");
        db.execSQL("DROP TABLE IF EXISTS telemetry");
        db.execSQL("DROP TABLE IF EXISTS viajes");
        onCreate(db);
    }

    public boolean insertarUsuario(String nombre, String correo, String contrasena) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Usuario_NOMBRE, nombre);
        values.put(Usuario_CORREO, correo);
        values.put(Usuario_CONTRASENA, hashPassword(contrasena));

        long resultado = db.insert(TABLE_USUARIOS, null, values);
        db.close();

        return resultado != -1;
    }
    public boolean verificarUsuario(String correo, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + Usuario_ID + " FROM " + TABLE_USUARIOS +
                " WHERE " + Usuario_CORREO + " = ? AND " + Usuario_CONTRASENA + " = ?";

        Cursor cursor = db.rawQuery(query,
                new String[]{correo, hashPassword(password)});

        boolean existe = cursor.moveToFirst();
        cursor.close();
        db.close();
        return existe;
    }
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public String obtenerNombreUsuario(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + Usuario_NOMBRE + " FROM " + TABLE_USUARIOS + " WHERE " + Usuario_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        String nombre = null;
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(Usuario_NOMBRE));
        }
        cursor.close();
        db.close();
        return nombre;
    }
    public int obtenerIdUsuarioPorCorreo(String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + Usuario_ID + " FROM " + TABLE_USUARIOS + " WHERE " + Usuario_CORREO + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{correo});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(Usuario_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }

    public int getDefaultVehicleIdForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT default_vehicle_id FROM " + TABLE_USUARIOS + " WHERE " + Usuario_ID + " = ?";
        Cursor c = db.rawQuery(q, new String[]{String.valueOf(userId)});
        int vid = -1;
        if (c.moveToFirst()) {
            vid = c.getInt(c.getColumnIndexOrThrow("default_vehicle_id"));
        }
        c.close();
        db.close();
        return vid;
    }
    public boolean setDefaultVehicleForUser(int userId, int vehicleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("default_vehicle_id", vehicleId);
        int rows = db.update(TABLE_USUARIOS, cv, Usuario_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }
    public boolean clearDefaultVehicleForUser(int userId) {
        return setDefaultVehicleForUser(userId, -1);
    }

    public long insertarVehicle(int userId, String vin, String make, String model, Integer year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("vin", vin);
        values.put("make", make);
        values.put("model", model);
        if(year != null) values.put("year", year);
        long id = db.insert("vehicles", null, values);
        db.close();
        return id;
    }

    public long upsertVehicleForUser(int userId, String vin, String make, String model, Integer year) {
        int defaultVid = getDefaultVehicleIdForUser(userId);
        if (defaultVid > 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("vin", vin);
            cv.put("make", make);
            cv.put("model", model);
            if (year != null) cv.put("year", year);
            int rows = db.update("vehicles", cv, "id = ?", new String[]{String.valueOf(defaultVid)});
            db.close();
            return rows > 0 ? defaultVid : -1;
        } else {
            long id = insertarVehicle(userId, vin, make, model, year);
            if (id > 0) setDefaultVehicleForUser(userId, (int) id);
            return id;
        }
    }
    public Cursor obtenerVehiclePorId(int vehicleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM vehicles WHERE id = ?";
        return db.rawQuery(q, new String[]{String.valueOf(vehicleId)});
    }

    public long insertarTelemetry(int vehicleId, String timestamp, Integer rpm, Double throttle, Double maf, Double engineLoad, Double fuelRate, Double lat, Double lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("vehicle_id", vehicleId);
        values.put("timestamp", timestamp);
        if (rpm != null) values.put("rpm", rpm);
        if (throttle != null) values.put("throttle", throttle);
        if (maf != null) values.put("maf", maf);
        if (engineLoad != null) values.put("engine_load", engineLoad);
        if (fuelRate != null) values.put("fuel_rate", fuelRate);
        if (lat != null) values.put("lat", lat);
        if (lon != null) values.put("lon", lon);
        values.put("synced", 0);
        long id = db.insert("telemetry", null, values);
        db.close();
        return id;
    }
    public Cursor obtenerTelemetriaPorVehiculo(int vehicleId, int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM telemetry WHERE vehicle_id = ? ORDER BY timestamp DESC LIMIT ?";
        return db.rawQuery(q, new String[]{String.valueOf(vehicleId), String.valueOf(limit)});
    }
    public Cursor obtenerTelemetriaPorVehiculoOrdenAsc(int vehicleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM telemetry WHERE vehicle_id = ? ORDER BY timestamp ASC";
        return db.rawQuery(q, new String[]{String.valueOf(vehicleId)});
    }

    public String exportarTelemetriaCSV(int vehicleId) {
        Cursor c = obtenerTelemetriaPorVehiculo(vehicleId, 10000);
        StringBuilder sb = new StringBuilder();
        sb.append("id,vehicle_id,timestamp,rpm,throttle,maf,engine_load,fuel_rate,lat,lon\n");
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow("id"));
            String ts = c.getString(c.getColumnIndexOrThrow("timestamp"));
            String rpm = c.isNull(c.getColumnIndexOrThrow("rpm")) ? "" : String.valueOf(c.getInt(c.getColumnIndexOrThrow("rpm")));
            String throttle = c.isNull(c.getColumnIndexOrThrow("throttle")) ? "" : String.valueOf(c.getDouble(c.getColumnIndexOrThrow("throttle")));
            String maf = c.isNull(c.getColumnIndexOrThrow("maf")) ? "" : String.valueOf(c.getDouble(c.getColumnIndexOrThrow("maf")));
            String engine_load = c.isNull(c.getColumnIndexOrThrow("engine_load")) ? "" : String.valueOf(c.getDouble(c.getColumnIndexOrThrow("engine_load")));
            String fuel_rate = c.isNull(c.getColumnIndexOrThrow("fuel_rate")) ? "" : String.valueOf(c.getDouble(c.getColumnIndexOrThrow("fuel_rate")));
            String lat = c.isNull(c.getColumnIndexOrThrow("lat")) ? "" : String.valueOf(c.getDouble(c.getColumnIndexOrThrow("lat")));
            String lon = c.isNull(c.getColumnIndexOrThrow("lon")) ? "" : String.valueOf(c.getDouble(c.getColumnIndexOrThrow("lon")));
            sb.append(id).append(",").append(vehicleId).append(",").append(ts).append(",")
                    .append(rpm).append(",").append(throttle).append(",").append(maf).append(",")
                    .append(engine_load).append(",").append(fuel_rate).append(",")
                    .append(lat).append(",").append(lon).append("\n");
        }
        c.close();
        return sb.toString();
    }

    public Cursor obtenerViajePorId(int viajeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM viajes WHERE id = ?";
        return db.rawQuery(q, new String[]{String.valueOf(viajeId)});
    }
}