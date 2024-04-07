package com.example.pr_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DbManager {
    private Context context;
    private Dbhelper dbHelper;
    private SQLiteDatabase db;

    public DbManager(Context context) {
        this.context = context;
        dbHelper = new Dbhelper(context);
    }

    public DbManager open() throws SQLException {
        dbHelper = new Dbhelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public boolean addLeaveApplication(String empName, String leaveReason, String fromDate, String toDate) {
        boolean isInserted = false;
        ContentValues values = new ContentValues();
        values.put("emp_name", empName);
        values.put("leave_reason", leaveReason);
        values.put("from_date", fromDate);
        values.put("to_date", toDate);
        values.put("status", "Pending"); // Default status

        try {
            // Open the database connection
            open();

            long result = db.insert("leave_requests", null, values);

            if (result != -1) {
                isInserted = true;
                Log.d("LeaveManagement", "Insertion Success");
            } else {
                Log.d("LeaveManagement", "Insertion Failed");
            }

        } catch (SQLException e) {
            Log.e("DbManager", "Error opening database connection: " + e.getMessage());
        } finally {
            // Close the database connection
            close();
        }

        return isInserted;
    }


    public long add_UserDetails(String uname, String uemail, String upass, String urole) {
        if (isUsernameAvailable(uname)) {
        ContentValues cv = new ContentValues();
        cv.put(Dbhelper.name, uname);
        cv.put(Dbhelper.email, uemail);
        cv.put(Dbhelper.password, upass);
        cv.put(Dbhelper.role, urole);

        return db.insert(Dbhelper.tblname, null, cv);}
        else{
            return -1;
        }
    }


    public boolean isUsernameAvailable(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Query to check if the username exists
            cursor = db.query(Dbhelper.tblname, null, Dbhelper.name + "=?", new String[]{username}, null, null, null);
            return cursor == null || cursor.getCount() == 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public Cursor getLeaveRequestsForUser(String empName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"_id", "from_date", "to_date", "leave_reason", "status"};
        String selection = "emp_name=?";
        String[] selectionArgs = {empName};

        Cursor cursor = null;
        try {
            cursor = db.query("leave_requests", columns, selection, selectionArgs, null, null, null);
        } catch (SQLException e) {
            Log.e("DbManager", "Error getting leave requests for user: " + e.getMessage());
        }

        return cursor;
    }




    public Cursor getAllLeaveRequestsCursor() {
        Cursor c =  db.query("leave_requests", null, null, null, null, null, null);
        return c;
    }

    public void updateLeaveRequestStatus(int id, String status) {
        ContentValues values = new ContentValues();
        values.put("status", status);
        db.update("leave_requests", values, "_id=?", new String[]{String.valueOf(id)});
    }

    public void deleteLeaveRequest(int id) {
        db.delete("leave_requests", "_id=?", new String[]{String.valueOf(id)});
    }

    public boolean isValidUser(String username, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String[] columns = {Dbhelper.name, Dbhelper.password};
            String selection = Dbhelper.name + "= ? AND " + Dbhelper.password + "= ?";
            String[] selectionArgs = {username, password};
            cursor = db.query(Dbhelper.tblname, columns, selection, selectionArgs, null, null, null);
            return cursor.moveToFirst();
        } catch (SQLException e) {
            Log.e("DbManager", "Error querying database: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    public Cursor getLeaveRequestsForUserByEmail(String userEmail) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"_id", "from_date", "to_date", "leave_reason", "status"};
        String selection = "user_email=?";
        String[] selectionArgs = {userEmail};
        return db.query("leave_requests", null, selection, selectionArgs, null, null, null);
    }


    public String getUserRole(String username, String userPassword) {
        String role = "1";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT " + Dbhelper.role + " FROM " + Dbhelper.tblname +
                            " WHERE " + Dbhelper.name + " = ? AND " + Dbhelper.password + " = ? ",
                    new String[]{username, userPassword});

            if (cursor != null && cursor.moveToFirst()) {
                role = cursor.getString(cursor.getColumnIndexOrThrow(Dbhelper.role));
            }
        } catch (SQLException e) {
            Log.e("DbManager", "Error retrieving user role: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return role;
    }
}
