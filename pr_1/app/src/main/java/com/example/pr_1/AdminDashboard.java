package com.example.pr_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class AdminDashboard extends AppCompatActivity {
    private ListView leaveListView;
    private Button approveButton,rejectButton,logout;
    private DbManager dbManager;
    private LeaveListAdapter leaveListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        leaveListView = findViewById(R.id.adminleaveRequestsListView);
        approveButton = findViewById(R.id.approveButton);
        rejectButton = findViewById(R.id.rejectButton);
        logout = findViewById(R.id.logoutButton);
        dbManager = new DbManager(this);

        // Open the database connection
        try {
            dbManager.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening database", Toast.LENGTH_SHORT).show();
        }

        loadLeaveRequests();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the session data
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(AdminDashboard.this, MainActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        leaveListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle checkbox selection
                leaveListAdapter.toggleSelection(position);
            }
        });

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveSelectedLeaveRequests();
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectLeaves();
            }
        });
    }


    private void loadLeaveRequests() {
        Cursor cursor = dbManager.getAllLeaveRequestsCursor();
        leaveListAdapter = new LeaveListAdapter(this, cursor);
        leaveListView.setAdapter(leaveListAdapter);
    }

    private void approveSelectedLeaveRequests() {
        List<Integer> selectedIds = leaveListAdapter.getSelectedIds();
        if (selectedIds.isEmpty()) {
            Toast.makeText(this, "Select leave requests to approve", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int id : selectedIds) {
            dbManager.updateLeaveRequestStatus(id, "Approved");
        }

        Toast.makeText(this, "Selected leave requests approved", Toast.LENGTH_SHORT).show();
        loadLeaveRequests(); // Refresh leave requests list
    }

    private void rejectLeaves() {
        List<Integer> selectedIds = leaveListAdapter.getSelectedIds();
        if (selectedIds.isEmpty()) {
            Toast.makeText(this, "leave requests to approved", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int id : selectedIds) {
            dbManager.updateLeaveRequestStatus(id, "Rejected");
        }

        Toast.makeText(this, "leave requests Rejected", Toast.LENGTH_SHORT).show();
        loadLeaveRequests(); // Refresh leave requests list
    }
}
