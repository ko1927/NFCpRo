
package com.example.nfcpro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SessionManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BoothSession";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SESSION = "session";

    // 컬럼명
    private static final String KEY_ID = "id";
    private static final String KEY_BOOTH_ID = "booth_id";
    private static final String KEY_ADMIN_ID = "admin_id";
    private static final String KEY_LAST_LOGIN = "last_login";

    public SessionManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SESSION_TABLE = "CREATE TABLE " + TABLE_SESSION + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_BOOTH_ID + " TEXT,"
                + KEY_ADMIN_ID + " TEXT,"
                + KEY_LAST_LOGIN + " INTEGER" + ")";
        db.execSQL(CREATE_SESSION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        onCreate(db);
    }

    // 세션 저장
    public void saveSession(String boothId, String adminId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 기존 세션 삭제
        db.delete(TABLE_SESSION, null, null);

        ContentValues values = new ContentValues();
        values.put(KEY_BOOTH_ID, boothId);
        values.put(KEY_ADMIN_ID, adminId);
        values.put(KEY_LAST_LOGIN, System.currentTimeMillis());

        db.insert(TABLE_SESSION, null, values);
        db.close();
    }

    // 세션 확인
    public SessionData getSession() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SESSION, null, null, null, null, null, null);

        SessionData sessionData = null;
        if(cursor != null && cursor.moveToFirst()) {
            sessionData = new SessionData(
                    cursor.getString(cursor.getColumnIndex(KEY_BOOTH_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_ADMIN_ID)),
                    cursor.getLong(cursor.getColumnIndex(KEY_LAST_LOGIN))
            );
            cursor.close();
        }
        db.close();
        return sessionData;
    }

    // 세션 삭제 (로그아웃)
    public void clearSession() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SESSION, null, null);
        db.close();
    }

    // 세션 데이터 클래스
    public static class SessionData {
        private String boothId;
        private String adminId;
        private long lastLogin;

        public SessionData(String boothId, String adminId, long lastLogin) {
            this.boothId = boothId;
            this.adminId = adminId;
            this.lastLogin = lastLogin;
        }

        public String getBoothId() { return boothId; }
        public String getAdminId() { return adminId; }
        public long getLastLogin() { return lastLogin; }
    }
}