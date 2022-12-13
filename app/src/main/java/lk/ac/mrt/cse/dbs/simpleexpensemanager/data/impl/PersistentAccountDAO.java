package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteGetHelp.Account_Table;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteGetHelp.AccountNo;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteGetHelp.BankName;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteGetHelp.AccountHolderName;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteGetHelp.Balance;


public class  PersistentAccountDAO implements AccountDAO{
    /*
    * private final Map<String, Account> accounts;
    * all the accounts may appear here.
    * take help from SQLiteGetHelp method to get data.
    * */
    private final SQLiteGetHelp helper;
    private SQLiteDatabase db;


    public  PersistentAccountDAO(Context context) {
        helper = new SQLiteGetHelp(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        db = helper.getReadableDatabase(); //Create and/or open a database

        //column array, same as π(projection) in mysql
        String[] projection = {
                AccountNo
        };

        /*
        To get all formats belonging to an order, just look up rows with the ID of that order:
        * in mysql:SELECT * FROM Formats WHERE OrderID = ?
        * in java:
            Cursor cursor = db.query(
                Table Name        // Table to query
                    projection,         //column for select clause
                    null,               // columns for WHERE clause
                    null,               // WHERE clause value
                    null,               // don't group the rows
                    null,               // don't filter by row groups
                    null                // The sorting order
                // */

        Cursor cursor = db.query(
                Account_Table,  // Table to query
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<String> accountNumbers = new ArrayList<String>();
        // read one distinct account from the cursor
        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(AccountNo));
            accountNumbers.add(accountNumber);
        }
        cursor.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<Account>();

        db = helper.getReadableDatabase();

        String[] projection = {
                AccountNo,BankName,AccountHolderName,Balance
        };

        Cursor cursor = db.query(
                Account_Table,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndex(AccountNo));
            String bankName = cursor.getString(cursor.getColumnIndex(BankName));
            String accountHolderName = cursor.getString(cursor.getColumnIndex(AccountHolderName));
            double balance = cursor.getDouble(cursor.getColumnIndex(Balance));
            Account account = new Account(accountNumber,bankName,accountHolderName,balance);

            accounts.add(account);
        }
        cursor.close();
        return accounts;

    }

    @Override
    public Account getAccount(String accNo) throws InvalidAccountException {

        db = helper.getReadableDatabase();
        String[] projection = {
                AccountNo,BankName,AccountHolderName,Balance
        };

        String selection = AccountNo + " = ?";
        String[] selectionArgs = { accNo };

        Cursor cursor = db.query(
                Account_Table,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor == null){
            String msg = "Account " + accNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else {
            cursor.moveToFirst();

            Account account = new Account(accNo, cursor.getString(cursor.getColumnIndex(BankName)),
                    cursor.getString(cursor.getColumnIndex(AccountHolderName)), cursor.getDouble(cursor.getColumnIndex(Balance)));
            return account;
        }
    }

    @Override
    public void addAccount(Account account) {

        db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AccountNo, account.getAccountNo());
        values.put(BankName, account.getBankName());
        values.put(AccountHolderName, account.getAccountHolderName());
        values.put(Balance,account.getBalance());

        db.insert(Account_Table, null, values);
        db.close();
    }

    @Override
    public void removeAccount(String accNo) throws InvalidAccountException {

        db = helper.getWritableDatabase();
        db.delete(Account_Table, AccountNo + " = ?",
                new String[] { accNo });
        db.close();
    }

    @Override
    public void updateBalance(String accNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        db = helper.getWritableDatabase();
        String[] projection = {
                Balance
        };

        String selection = AccountNo + " = ?";
        String[] selectionArgs = { accNo };

        Cursor cursor = db.query(
                Account_Table,
                 projection,
                 selection,
                 selectionArgs,
                null,
                null,
                null
        );

        double balance;
        if(cursor.moveToFirst())
            balance = cursor.getDouble(0);
        else{
            String msg = "Account " + accNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        ContentValues values = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                values.put(Balance, balance - amount);
                break;
            case INCOME:
                values.put(Balance, balance + amount);
                break;
        }

        db.update(Account_Table, values, AccountNo + " = ?",
                new String[] { accNo });

        cursor.close();
        db.close();

    }
}