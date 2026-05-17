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
    private static final int DATABASE_VERSION = 1;

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
                        Usuario_CONTRASENA + " TEXT NOT NULL" +
                        ");";
        db.execSQL(createUsuarios);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
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
    public boolean verificarUsuario(String nombre, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + Usuario_ID + " FROM " + TABLE_USUARIOS +
                " WHERE " + Usuario_NOMBRE + " = ? AND " + Usuario_CONTRASENA + " = ?";

        Cursor cursor = db.rawQuery(query,
                new String[]{nombre, hashPassword(password)});

        boolean existe = cursor.moveToFirst();
        cursor.close();
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
}