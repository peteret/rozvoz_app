package eu.webprofik.rozvoz.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.Objects;

import eu.webprofik.rozvoz.MainActivity;
import eu.webprofik.rozvoz.R;
import eu.webprofik.rozvoz.Record;
import eu.webprofik.rozvoz.RecordsAdapter;
import eu.webprofik.rozvoz.SwipeToDeleteCallback;

import eu.webprofik.rozvoz.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public List<Record> records = new ArrayList<>();
    private RecordsAdapter adapter;
    private RecyclerView mRecyclerView;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        loadRecordsFromFile();


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mRecyclerView = root.findViewById(R.id.recyclerView);

//        // Pridanie príkladových záznamov
//        records.add(new Record("Adresa 1", "123456789", true, 29.99));
//        records.add(new Record("Adresa 2", "987654321", false, 19.99));

        // Inicializácia a nastavenie adaptéra
        adapter = new RecordsAdapter(root.getContext(), records);
        mRecyclerView.setAdapter(adapter);

        // Inicializácia SwipeToDeleteCallback
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        // Inicializácia RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Pridanie tlačidla na pridávanie záznamov
        Button addRecordButton = root.findViewById(R.id.addRecordButton);
        addRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddRecordDialog();
            }
        });

        return root;
    }

    private void showAddRecordDialog() {
        // Vytvorenie a konfigurácia AlertDialog s vlastným layoutom
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.AlertDialogTheme);
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_record, null);
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
    public void onPause() {
        super.onPause();

        // Uloženie záznamov do vnútornej pamäte
        saveRecordsToFile();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Uloženie záznamov do vnútornej pamäte
        saveRecordsToFile();
    }

    private void saveRecordsToFile() {
        try {
            // Otvorenie alebo vytvorenie súboru pre ukladanie záznamov
            FileOutputStream fileOutputStream = requireActivity().openFileOutput("records.txt", Context.MODE_PRIVATE);

            // Konvertovanie záznamov na reťazec (napr. JSON)
            Gson gson = new Gson();
            String recordsJson = gson.toJson(records);
            if (records == null){
                Toast.makeText(binding.getRoot().getContext(), "Records Null Warning", Toast.LENGTH_SHORT).show();
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
    public void onResume() {
        super.onResume();

        // Načítanie záznamov z SharedPreferences
        loadRecordsFromFile();
    }

    private void loadRecordsFromFile() {
        try {

            // Otvorenie súboru s uloženými záznamami
            FileInputStream fileInputStream = requireActivity().openFileInput("records.txt");

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
            if (records == null){
                Toast.makeText(binding.getRoot().getContext(), "File not found", Toast.LENGTH_SHORT).show();
                records = new ArrayList<>();
            }
            System.out.println(records);
            System.out.println("nacitavanie");
            this.adapter.records = records;
            adapter.notifyDataSetChanged();

            for (Record var : records)
            {
                System.out.println(var.getAddress());
            }



            // Zatvorenie súboru
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}