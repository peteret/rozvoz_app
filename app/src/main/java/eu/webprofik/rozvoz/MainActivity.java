package eu.webprofik.rozvoz;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public List<Record> records = new ArrayList<>();
    private RecordsAdapter adapter;
    private RecyclerView mRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mRecyclerView = findViewById(R.id.recyclerView);


        System.out.println("zaciatok");
        System.out.println(records);
        // Pridanie príkladových záznamov
        records.add(new Record("Adresa 1", "123456789", true, 29.99));
        records.add(new Record("Adresa 2", "987654321", false, 19.99));

        // Inicializácia a nastavenie adaptéra
        adapter = new RecordsAdapter(records);
        mRecyclerView.setAdapter(adapter);
        loadRecordsFromFile();
        // Inicializácia SwipeToDeleteCallback
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        // Inicializácia RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Pridanie tlačidla na pridávanie záznamov
        Button addRecordButton = findViewById(R.id.addRecordButton);
        addRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddRecordDialog();
            }
        });
    }

    private void showAddRecordDialog() {
        // Vytvorenie a konfigurácia AlertDialog s vlastným layoutom
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_record, null);
        builder.setView(dialogView);
        final TextInputEditText addressEditText = dialogView.findViewById(R.id.addressEditText);
        final TextInputEditText phoneEditText = dialogView.findViewById(R.id.phoneEditText);
        final TextInputEditText priceEditText = dialogView.findViewById(R.id.priceEditText);
        final CheckBox paidCheckBox = dialogView.findViewById(R.id.paidCheckBox);


        // Nastavenie obsahu dialógového okna a tlačidiel

        AlertDialog alertDialog = builder.create();

        // Nastavenie transparentného pozadia pre okno
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.white);
        }

        alertDialog.show();

        Button addRecord = dialogView.findViewById(R.id.addRecord);
        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                double price = Double.parseDouble(priceEditText.getText().toString());
                boolean isPaid = paidCheckBox.isChecked();

                // Pridanie nového záznamu do zoznamu
                Record newRecord = new Record(address, phone, isPaid, price);
                records.add(newRecord);
                System.out.println(records);
                System.out.println("vytovrene");
                // Aktualizácia RecyclerView
                adapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

        Button cancel = dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });




    }

    @Override
    protected void onPause() {
        super.onPause();

        // Uloženie záznamov do vnútornej pamäte
        saveRecordsToFile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Uloženie záznamov do vnútornej pamäte
        saveRecordsToFile();
    }

    private void saveRecordsToFile() {
        try {
            // Otvorenie alebo vytvorenie súboru pre ukladanie záznamov
            FileOutputStream fileOutputStream = openFileOutput("records.txt", Context.MODE_PRIVATE);

            // Konvertovanie záznamov na reťazec (napr. JSON)
            Gson gson = new Gson();
            String recordsJson = gson.toJson(records);
            if (records == null){
                return;
            }

            // Zápis reťazca do súboru
            fileOutputStream.write(recordsJson.getBytes());
            this.adapter.records = records;
            adapter.notifyDataSetChanged();

            // Zatvorenie súboru
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Načítanie záznamov z SharedPreferences
        loadRecordsFromFile();
    }

    private void loadRecordsFromFile() {
        try {
            // Otvorenie súboru s uloženými záznamami
            FileInputStream fileInputStream = openFileInput("records.txt");

            // Čítanie dát zo súboru
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            // Konvertovanie reťazca na záznamy (predpokladám, že Record má metódu fromJson())
            Gson gson = new Gson();
            Type type = new TypeToken<List<Record>>() {}.getType();
            records = gson.fromJson(stringBuilder.toString(), type);
            System.out.println(records);
            System.out.println("nacitavanie");
            this.adapter.records = records;
            adapter.notifyDataSetChanged();

            for (Record var : records)
            {
                System.out.println(var.getAddress());
            }

            // Ak záznamy ešte neboli vytvorené, inicializujte prázdny zoznam
            if (records == null) {
                records = new ArrayList<>();
            }

            // Zatvorenie súboru
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

