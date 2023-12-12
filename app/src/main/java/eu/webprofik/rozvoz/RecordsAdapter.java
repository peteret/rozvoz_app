package eu.webprofik.rozvoz;


import android.app.Activity;
import android.content.Context;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 123; // You can use any integer value
    private final Context context;
    public List<Record> records;

    private int actValue;
    private String completePhoneValue;
    private static final String PREFS_NAME = "settings";
    private static final String ACT_KEY = "actValue";
    private static final String COMPLETE_PHONE_KEY = "completePhoneValue";

    public RecordsAdapter(Context context, List<Record> records) {
        this.context = context;

        // Retrieve values from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        actValue = prefs.getInt(ACT_KEY, 0);
        completePhoneValue = prefs.getString(COMPLETE_PHONE_KEY, "");
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = records.get(position);
        holder.addressTextView.setText(record.getAddress());
        holder.phoneTextView.setText(record.getPhoneNumber());
        holder.isPaidCheckBox.setChecked(record.isPaid());
        holder.isPaidCheckBox.setEnabled(false);
        if (record.isPaid()){
            holder.isPaidCheckBox.setTextColor(Color.GREEN);
        }else {
            holder.isPaidCheckBox.setTextColor(Color.RED);
        }

        holder.priceTextView.setText(String.valueOf(record.getPrice())+"€");

        // Set click listeners for the buttons
        holder.navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle navigation button click (start navigation to the address)
                // You can use the address from the current record
                String address = record.getAddress();

                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                // Check if there is an app to handle the intent
                if (mapIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                    v.getContext().startActivity(mapIntent);
                } else {
                    // Handle the case where there is no app to handle the intent
                    Toast.makeText(v.getContext(), "No navigation app installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle phone button click (start a phone call to the phone number)
                // You can use the phone number from the current record
                String phoneNumber = record.getPhoneNumber();

                // Create an intent to start a phone call
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));

                // Check if there is an app to handle the intent
                if (dialIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                    v.getContext().startActivity(dialIntent);
                } else {
                    // Handle the case where there is no app to handle the intent
                    Toast.makeText(v.getContext(), "No app to handle phone calls", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return records.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView addressTextView;
        public TextView phoneTextView;
        public CheckBox isPaidCheckBox;
        public TextView priceTextView;
        public Button navigateButton; // Add reference to the navigate button
        public Button phoneButton;    // Add reference to the phone button

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            isPaidCheckBox = itemView.findViewById(R.id.isPaidCheckBox);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            navigateButton = itemView.findViewById(R.id.navigateButton); // Initialize navigate button
            phoneButton = itemView.findViewById(R.id.phoneButton);       // Initialize phone button
        }
    }

    public void deleteItem(int position) {
        // Odstránenie položky z dátového zoznamu (napr. List<MyData>)
        records.remove(position);
        Toast.makeText(context, String.valueOf(actValue), Toast.LENGTH_SHORT).show();
        actValue++;
        saveToSharedPreferences();

        if (getItemCount() == 0){
            showConfirmationDialog();
        }




        // Aktualizácia RecyclerView
        notifyItemRemoved(position);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Zavolať");
        builder.setMessage("Naozaj chceš zavolať na ukončenie rozvozu?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call the method to make a phone call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Show the dialog to confirm the call
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Request the permission
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        } else {
                            // Permission is already granted, proceed with making the call
                            makePhoneCall("tel:" + completePhoneValue);
                        }

                        dialog.dismiss();
                    }
                }, 3000);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User canceled the call
                dialog.dismiss();
            }
        });

        // Show the dialog
        builder.show();
    }

    private void makePhoneCall(String phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
        context.startActivity(dialIntent);
        // Note: The code to end the call programmatically is not available due to security restrictions.
        // You might need the CALL_PHONE permission in your AndroidManifest.xml.
    }

    private void saveToSharedPreferences() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(ACT_KEY, actValue);
        editor.putString(COMPLETE_PHONE_KEY, completePhoneValue);
        editor.apply();
    }
}
