package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
//import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteGetHelp.Transaction_Table;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteGetHelp.AccountNo;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteGetHelp.Amount;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteGetHelp.DATE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteGetHelp.Expense_Type;



public class  PersistentTransactionDAO implements TransactionDAO {
    private final SQLiteGetHelp helper;
    private SQLiteDatabase db;

    public  PersistentTransactionDAO(Context context) {
        helper = new SQLiteGetHelp(context);
    }

    @Override
    public void logTransaction(Date date, String accNo, ExpenseType expenseType, double amount) {

        db = helper.getWritableDatabase();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues values = new ContentValues();
        values.put(DATE, df.format(date));
        values.put(AccountNo, accNo);
        values.put(Expense_Type, String.valueOf(expenseType));
        values.put(Amount, amount);

        db.insert(Transaction_Table, null, values);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<Transaction>();

        db = helper.getReadableDatabase();

        String[] projection = {
                DATE,
                AccountNo,
                Expense_Type,
                Amount
        };

        /*Table Name        // Table to query
                    projection,
                    null,               // columns for WHERE clause
                    null,               // WHERE clause value
                    null,               // don't group the rows
                    null,               // don't filter by row groups
                    null                // The sorting order
                // */

        Cursor cursor = db.query(
                Transaction_Table,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            //String date = cursor.getString(cursor.getColumnIndex(DATE));
            Date date_1 = new Date("dd-MM-yyyy");
            String accountNumber = cursor.getString(cursor.getColumnIndex(AccountNo));
            String type = cursor.getString(cursor.getColumnIndex(Expense_Type));
            ExpenseType expenseType = ExpenseType.valueOf(type);
            double amount = cursor.getDouble(cursor.getColumnIndex(Amount));
            Transaction transaction = new Transaction(date_1,accountNumber,expenseType,amount);

            transactions.add(transaction);
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        List<Transaction> transactions = new ArrayList<Transaction>();

        db = helper.getReadableDatabase();

        String[] projection = {
                DATE,
                AccountNo,
                Expense_Type,
                Amount
        };

        Cursor cursor = db.query(
                Transaction_Table,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        int size = cursor.getCount();

        while(cursor.moveToNext()) {
            //String date = cursor.getString(cursor.getColumnIndex(DATE));
            Date date_1 = new Date("dd-MM-yyyy");
            String accountNumber = cursor.getString(cursor.getColumnIndex(AccountNo));
            String type = cursor.getString(cursor.getColumnIndex(Expense_Type));
            ExpenseType expenseType = ExpenseType.valueOf(type);
            double amount = cursor.getDouble(cursor.getColumnIndex(Amount));

            Transaction transaction = new Transaction(date_1,accountNumber,expenseType,amount);
            transactions.add(transaction);
        }

        if (size <= limit) {
            return transactions;
        }
        return transactions.subList(size - limit, size);

    }

}

