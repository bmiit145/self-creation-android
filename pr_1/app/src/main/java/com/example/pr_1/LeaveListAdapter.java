package com.example.pr_1;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LeaveListAdapter extends CursorAdapter {
    private List<Integer> selectedIds;

    public LeaveListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        selectedIds = new ArrayList<>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.cust_listview, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView reasonTextView = view.findViewById(R.id.reasonTextView);
        TextView statusTextView = view.findViewById(R.id.statusTextView);
        CheckBox checkBox = view.findViewById(R.id.checkBox);

        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(Dbhelper.id));
        String empName = cursor.getString(cursor.getColumnIndexOrThrow(Dbhelper.empname));
        String leaveReason = cursor.getString(cursor.getColumnIndexOrThrow(Dbhelper.leaveReason));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(Dbhelper.status));

        nameTextView.setText(empName);
        reasonTextView.setText(leaveReason);
        statusTextView.setText(status);
        checkBox.setChecked(selectedIds.contains(id));

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSelection(id);
            }
        });
    }

    public void toggleSelection(int id) {
        if (selectedIds.contains(id)) {
            selectedIds.remove((Integer) id);
        } else {
            selectedIds.add(id);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedIds() {
        return selectedIds;
    }
}
