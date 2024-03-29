package com.example.mobiletranslator.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.example.mobiletranslator.FileUtility;
import com.example.mobiletranslator.db.Contract.*;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TranslatorApp.db";

    private final Context context;

    //------DDL Statements------
    private static final String SQL_CREATE_PARAM =
            "CREATE TABLE " + Param.TABLE_NAME + " (" +
                    Param._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Param.COLUMN_NAME_DESCR + " TEXT UNIQUE," +
                    Param.COLUMN_NAME_VALUE + " TEXT)";

    private static final String SQL_CREATE_LANGUAGE =
            "CREATE TABLE " + Language.TABLE_NAME + " (" +
                    Language._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Language.COLUMN_NAME_NAME + " TEXT," +
                    Language.COLUMN_NAME_ISO_CODE + " TEXT," +
                    Language.COLUMN_NAME_ISO_CODE3 + " TEXT," +
                    Language.COLUMN_NAME_VISIBILITY + " TEXT," +
                    Language.COLUMN_NAME_SUPPORT_FORMAL + " INTEGER,"+
                    Language.COLUMN_OCR_FILENAME + " TEXT)";

    private static final String SQL_DELETE_PARAM = "DROP TABLE IF EXISTS " + Param.TABLE_NAME;
    private static final String SQL_DELETE_LANGUAGE = "DROP TABLE IF EXISTS " + Language.TABLE_NAME;

    //------INSERT Statements------
    private static final String SQL_INSERT_PARAM =
            "INSERT INTO "+Param.TABLE_NAME+
                    "("+Param.COLUMN_NAME_DESCR+","+
                    Param.COLUMN_NAME_VALUE+") VALUES(?,?)";

    private static final String SQL_INSERT_LANGUAGE =
            "INSERT INTO "+Language.TABLE_NAME+
                    "("+Language.COLUMN_NAME_NAME+","+
                    Language.COLUMN_NAME_ISO_CODE+","+
                    Language.COLUMN_NAME_ISO_CODE3+","+
                    Language.COLUMN_NAME_VISIBILITY+","+
                    Language.COLUMN_NAME_SUPPORT_FORMAL+","+
                    Language.COLUMN_OCR_FILENAME+") VALUES (?,?,?,?,?,?)";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_PARAM);
            db.execSQL(SQL_CREATE_LANGUAGE);
            loadData(db);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PARAM);
        db.execSQL(SQL_DELETE_LANGUAGE);
        onCreate(db);
    }

    public void loadData(SQLiteDatabase db) throws XmlPullParserException, IOException {
        db.beginTransaction();
        SQLiteStatement insertParamStatement = db.compileStatement(SQL_INSERT_PARAM);
        insertParamStatement.bindString(1,DbManager.API_KEY_PARAM_NAME);
        insertParamStatement.bindNull(2);
        insertParamStatement.executeInsert();

        ArrayList<LanguageItem> rows = FileUtility.parseLanguageFile(context.getAssets());
        SQLiteStatement insertLanguageStatement = db.compileStatement(SQL_INSERT_LANGUAGE);
        for(int i=0; i<rows.size(); ++i){
            insertLanguageStatement.clearBindings();
            insertLanguageStatement.bindString(1,rows.get(i).getName());
            insertLanguageStatement.bindString(2,rows.get(i).getIsoCode());
            insertLanguageStatement.bindString(3,rows.get(i).getIsoCode3());
            insertLanguageStatement.bindString(4,rows.get(i).getVisibility());
            insertLanguageStatement.bindLong(5,rows.get(i).isAllowFormalInt());
            insertLanguageStatement.bindString(6,rows.get(i).getFilename());
            insertLanguageStatement.executeInsert();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
