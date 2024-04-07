        package com.example.pr_1;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;
        import android.widget.Toast;

        public class Dbhelper extends SQLiteOpenHelper {
            private static final String DATABASE_NAME = "leave_management.db";
            private static final int DATABASE_VERSION = 1;

            // Table Leave Requests
            public static final String leaveRequestsTable = "leave_requests";
            public static final String id = "_id";
            public static final String empname = "emp_name";
            public static final String leaveReason = "leave_reason";
            public static final String fromDate = "from_date";
            public static final String toDate = "to_date";
            public static final String status = "status";

            // Table Users
            public static final String tblname = "tblUsers";
            public static final String uid = "_id";
            public static final String name = "name";
            public static final String email = "email";
            public static final String password = "password";
            public static final String role = "role";

            public Dbhelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            private void createLeaveRequestsTable(SQLiteDatabase db) {
                String createTableQuery = "CREATE TABLE " + leaveRequestsTable + " ( " +
                        id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        empname + " TEXT," +
                        leaveReason + " TEXT," +
                        fromDate + " TEXT," +
                        toDate + " TEXT," +
                        status + " TEXT DEFAULT 'Pending'" +
                        ")";
                db.execSQL(createTableQuery);
            }

            private void createUsersTable(SQLiteDatabase db) {
                String createTableQuery = "CREATE TABLE " + tblname + " ( " +
                        uid + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        name + " TEXT," +
                        email + " TEXT," +
                        password + " TEXT," +
                        role + " TEXT" +
                        " ) ";
                db.execSQL(createTableQuery);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                createLeaveRequestsTable(db);
                createUsersTable(db);
            }

            public void dropDatabase() {
                SQLiteDatabase db = this.getWritableDatabase();
                // Drop all tables by executing DROP TABLE IF EXISTS query
                db.execSQL("DROP TABLE IF EXISTS " + leaveRequestsTable);
                db.execSQL("DROP TABLE IF EXISTS " + tblname);
                Log.d("Registration", "Table Droped: ");


                // Close the database connection
                db.close();
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + leaveRequestsTable);
                db.execSQL("DROP TABLE IF EXISTS " + tblname);
                onCreate(db);
            }

            public void cleanDatabase() {
                SQLiteDatabase db = this.getWritableDatabase();
                db.execSQL("DROP TABLE IF EXISTS " + leaveRequestsTable);
                db.execSQL("DROP TABLE IF EXISTS " + tblname);
                createLeaveRequestsTable(db);
                createUsersTable(db);
                db.close();
            }
        }
