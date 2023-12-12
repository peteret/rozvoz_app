package eu.webprofik.rozvoz;


import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {
    public List<Record> records;

    public RecordsAdapter(List<Record> records) {
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
        holder.priceTextView.setText(String.valueOf(record.getPrice()));

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

        // Aktualizácia RecyclerView
        notifyItemRemoved(position);
    }
}
