package com.example.pr_4;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.security.Permission;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        contactsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactsList);
        listView.setAdapter(adapter);

        permissionAsk();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            fetchContects();
        }else{
            Toast.makeText(this, "Permission Denied For Contact View ", Toast.LENGTH_SHORT).show();
        }

        // Register context menu for long item click
        registerForContextMenu(listView);

        // Handle item click to perform actions
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Perform action when item is clicked (e.g., open contact details)
//                String contactName = contactsList.get(position);
//                Toast.makeText(MainActivity.this, "Clicked on: " + contactName, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void permissionAsk() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    100);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    100);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    100);
        }

        if (ContextCompat.checkSelfPermission(this , Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED){
            Toast.makeText(this, "Allow Contact Permission from Setting", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchContects() {
        contactsList.clear();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Contact Permission denined!", Toast.LENGTH_SHORT).show();
            permissionAsk();
            return;
        }

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        // Check if cursor is not null and contains contacts
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                // Fetch phone numbers for each contact
                Cursor phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{contactId},
                        null);

                // Add each contact with their phone numbers to the list
                if (phoneCursor != null && phoneCursor.moveToFirst()) {
                    do {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactsList.add(contactName + " - " + phoneNumber);
                    } while (phoneCursor.moveToNext());
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        if (contactsList.isEmpty()) {
            contactsList.add("No contacts available");
        }

        adapter.notifyDataSetChanged(); // Notify the adapter of data changes
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String selectedContact = contactsList.get(info.position);

        // Split the selected contact into name and number
        String[] parts = selectedContact.split(" - ");
        String contactName = parts[0];
        String contactNumber = parts[1];

        if (item.getItemId() == R.id.menu_sms) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission Denied for send SMS", Toast.LENGTH_SHORT).show();
            }else{
                sendSMS(contactNumber , contactName);
            }

            return true;
        } else if (item.getItemId() == R.id.menu_call) {
            makeCall(contactNumber);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }


    private void sendSMS(String phoneNumber , String contactName) {
        try {
            if (phoneNumber != null) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, "Hello " + contactName , null, null);
                Toast.makeText(getApplicationContext(), "SMS sent successfully! On " + phoneNumber, Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failed to send SMS.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void makeCall(String phoneNumber) {


        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Permission denied to make calls", Toast.LENGTH_SHORT).show();
        }
    }
}
