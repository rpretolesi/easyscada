package com.pretolesi.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper for DB SQL
 */
public class SQLHelper extends SQLiteOpenHelper {
    // Member for Singletone
    private static SQLHelper m_Instance;
    private static SQLiteDatabase m_Db;

    /**
     * Constructor takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     *            the application context
     */
    private SQLHelper(Context context)
    {
        super(context, SQLContract.DATABASE_NAME, null, SQLContract.DATABASE_VERSION);
    }

    /**
     * Get default instance of the class to keep it a singleton
     *
     * @param context
     *            the application context
     */
    public static SQLHelper getInstance(Context context)
    {
        if (m_Instance == null)
        {
            m_Instance = new SQLHelper(context);
        }
        return m_Instance;
    }

    public static SQLHelper getInstance()
    {
        return m_Instance;
    }

    /**
     * Returns a writable database instance in order not to open and close many
     * SQLiteDatabase objects simultaneously
     *
     * @return a writable instance to SQLiteDatabase
     */
    public SQLiteDatabase getDB()
    {
        if ((m_Db == null) || (!m_Db.isOpen()))
        {
            m_Db = this.getWritableDatabase();
        }

        return m_Db;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQLContract.Settings.SQL_CREATE_ENTRIES);
        db.execSQL(SQLContract.TranspProtocolEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SQLContract.RoomEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SQLContract.ControlEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQLContract.Settings.SQL_DELETE_ENTRIES);
        db.execSQL(SQLContract.TranspProtocolEntry.SQL_DELETE_ENTRIES);
        db.execSQL(SQLContract.RoomEntry.SQL_DELETE_ENTRIES);
        db.execSQL(SQLContract.ControlEntry.SQL_DELETE_ENTRIES);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void close()
    {
        super.close();
        if (m_Db != null)
        {
            m_Db.close();
            m_Db = null;
        }
    }
}
