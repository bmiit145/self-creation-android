package com.example.pr_1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EmployeeDashboard extends AppCompatActivity {

    EditText etName, etLeaveReason;
    Button btnFromDate, btnToDate, btnApplyLeave, logout;
    Calendar fromCalendar, toCalendar;
    SimpleDateFormat dateFormat;
    DbManager dbManager;
    private ListView leaveReqListView;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        etName = findViewById(R.id.etName);
        etLeaveReason = findViewById(R.id.etLeaveReason);
        btnFromDate = findViewById(R.id.btnFromDate);
        btnToDate = findViewById(R.id.btnToDate);
        btnApplyLeave = findViewById(R.id.btnApplyLeave);
        logout = findViewById(R.id.elogout);
        fromCalendar = Calendar.getInstance();
        toCalendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        dbManager = new DbManager(this);
        dbManager.open();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String loggedInUserEmail = sharedPreferences.getString("ename", ""); // Replace "userEmail" with the key used to store user email in SharedPreferences
        TextView wel = findViewById(R.id.welcome);
        wel.setText("Welcome : " + loggedInUserEmail);
        etName.setText(loggedInUserEmail);
        etName.setEnabled(false);
        loadprevLeaves(loggedInUserEmail);


        btnFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(fromCalendar, btnFromDate);
            }
        });

        btnToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(toCalendar, btnToDate);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the session data
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear(); // Clear all the stored data
                editor.apply();

                // Redirect to the MainActivity
                Intent intent = new Intent(EmployeeDashboard.this, MainActivity.class);
                // Clear the activity stack and start the MainActivity as a new task
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Finish the current activity
            }
        });

        btnApplyLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = loggedInUserEmail;
                String leaveReason = etLeaveReason.getText().toString();
                String fromDate = btnFromDate.getText().toString();
                String toDate = btnToDate.getText().toString();

                Calendar todayCalendar = Calendar.getInstance();
                todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
                todayCalendar.set(Calendar.MINUTE, 0);
                todayCalendar.set(Calendar.SECOND, 0);
                todayCalendar.set(Calendar.MILLISECOND, 0);

                // Validate To Date is not in the past and is greater than or equal to From Date
                if (toCalendar.before(todayCalendar) || toCalendar.before(fromCalendar)) {
                    Toast.makeText(EmployeeDashboard.this,
                            "Select a valid To Date greater than or equal to the From Date", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate From Date is not in the past
                if (fromCalendar.before(todayCalendar)) {
                    Toast.makeText(EmployeeDashboard.this,
                            "Select a valid From Date greater than or equal to the current date", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInserted = dbManager.addLeaveApplication(name, leaveReason, fromDate, toDate);

                // Show toast based on insertion result
                if (isInserted) {
                    String message = "Leave applied successfully!";
                    Toast.makeText(EmployeeDashboard.this, message, Toast.LENGTH_LONG).show();
                } else {
                    String message = "Failed to apply leave. Please try again.";
                    Toast.makeText(EmployeeDashboard.this, message, Toast.LENGTH_LONG).show();
                }

                // Clear input fields
                etName.setText("");
                etLeaveReason.setText("");
                btnFromDate.setText("Select Date");
                btnToDate.setText("Select Date");

                // Reload leave requests
                loadprevLeaves(loggedInUserEmail);
            }
        });

    }

    private void loadprevLeaves(String email) {
        leaveReqListView = findViewById(R.id.leaveRequestsListView);

        // Fetch leave requests of the logged-in user from the database
        Cursor cursor = dbManager.getLeaveRequestsForUser(email);

        // Check if the cursor is not null and has at least one row
        if (cursor != null && cursor.moveToFirst()) {
            // Setup adapter for the ListView
            String[] fromColumns = {"leave_reason", "status"};
            int[] toViews = {R.id.textViewLeaveReason, R.id.textViewStatus};
            adapter = new SimpleCursorAdapter(this, R.layout.emp_cust_list, cursor, fromColumns, toViews, 0);
            leaveReqListView.setAdapter(adapter);

            // Make the ListView visible
            leaveReqListView.setVisibility(View.VISIBLE);
        } else {
            // No leave requests found for the employee
            // You might want to handle this case accordingly, for example, displaying a message
            Toast.makeText(this, "No leave requests found.", Toast.LENGTH_SHORT).show();
        }
    }





    private void showDatePickerDialog(final Calendar calendar, final Button button) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                button.setText(dateFormat.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}
