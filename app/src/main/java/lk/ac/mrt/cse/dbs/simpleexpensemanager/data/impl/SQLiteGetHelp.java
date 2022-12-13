package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
extends SQLiteOpenHelper class to create a helper object to create, open, and/or manage a database
used helper method for implementation ;
public SQLiteOpenHelper (Context context,String name,SQLiteDatabase.CursorFactory factory,int version)
*SQLiteDatabase.CursorFactory: to use for creating cursor objects, or null for the default
 */
public class SQLiteGetHelp extends SQLiteOpenHelper{

    private static final String DB_name = "200472B.sqlite";
    /*
    version-number of the database (starting at 1);
    to identify its status (i.e. newer database or an old one
    */
    private static final int version = 1;

    //names of Tables
    public static final String Account_Table = "Account_Table";
    public static final String Transaction_Table = "Transaction_Table";
    //unique column name
    public static final String AccountNo = "AccountNo";

    //column names of Account Table
    public static final String BankName = "BankName";
    public static final String AccountHolderName = "AccountHolderName";
    public static final String Balance = "Balance";

    //column names of Transaction Table
    public static final String ID = "ID";
    public static final String DATE = "date";
    public static final String Expense_Type = "expense_Type";
    public static final String Amount = "Amount";

    public SQLiteGetHelp(Context context) {
        super(context, DB_name, null, version);
        //Context: to use for locating paths to the the database. This value may be null.
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //execute table create statement our application's SQLite database.
        database.execSQL("CREATE TABLE " + Account_Table + "(" +
                AccountNo + " TEXT PRIMARY KEY, " +
                BankName + " TEXT NOT NULL, " +
                AccountHolderName + " TEXT NOT NULL, " +
                Balance + " REAL NOT NULL)");

        database.execSQL("CREATE TABLE " + Transaction_Table + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE + " TEXT NOT NULL, " +
                Expense_Type + " TEXT NOT NULL, " +
                Amount + " REAL NOT NULL, " +
                AccountNo + " TEXT," +
                "FOREIGN KEY (" + AccountNo + ") REFERENCES " + Account_Table + "(" + AccountNo + "))");

    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // on upgrade, drop older tables
        String q;q="DROP TABLE IF EXISTS ";
        database.execSQL(q + Account_Table);
        database.execSQL(q + Transaction_Table);
        // create new tables
        onCreate(database);
    }
}