package eu.webprofik.rozvoz.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import eu.webprofik.rozvoz.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private int actValue;
    private String completePhoneValue;

    private static final String PREFS_NAME = "settings";
    private static final String ACT_KEY = "actValue";
    private static final String COMPLETE_PHONE_KEY = "completePhoneValue";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve values from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        actValue = prefs.getInt(ACT_KEY, 0);
        completePhoneValue = prefs.getString(COMPLETE_PHONE_KEY, "");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView actTextView = binding.act;
        final EditText completePhoneEditText = binding.completePhone;

        // Set initial values
        actTextView.setText(String.valueOf(actValue));
        completePhoneEditText.setText(completePhoneValue);

        Button plsActButton = binding.plsAct;
        plsActButton.setOnClickListener(v -> {
            // Increment act value
            actValue++;
            // Update the TextView
            actTextView.setText(String.valueOf(actValue));
            saveToSharedPreferences();

        });

        Button minActButton = binding.minAct;
        minActButton.setOnClickListener(v -> {
            // Decrement act value, but not below 0
            actValue = Math.max(0, actValue - 1);
            // Update the TextView
            actTextView.setText(String.valueOf(actValue));
            saveToSharedPreferences();

        });

        Button completePhoneSaveButton = binding.completePhoneSave;
        completePhoneSaveButton.setOnClickListener(v -> {
            // Save the value of EditText completePhone
            completePhoneValue = completePhoneEditText.getText().toString();
            // Save values to SharedPreferences
            saveToSharedPreferences();
        });

        Button resetButton = binding.button5;
        resetButton.setOnClickListener(v -> {
            // Reset values
            actValue = 0;
            completePhoneValue = "";

            // Update the UI
            actTextView.setText(String.valueOf(actValue));
            completePhoneEditText.setText(completePhoneValue);

            // Save values to SharedPreferences
            saveToSharedPreferences();
        });

        return root;
    }

    private void saveToSharedPreferences() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(ACT_KEY, actValue);
        editor.putString(COMPLETE_PHONE_KEY, completePhoneValue);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
