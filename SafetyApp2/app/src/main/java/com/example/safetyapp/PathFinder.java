package com.example.safetyapp;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class PathFinder extends AppCompatActivity {
    private EditText editTextSource, editTextDestination;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ProgressBar progressBar1;
    private Spinner spinner;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_finder);

        editTextSource = findViewById(R.id.editText_source);
        editTextDestination = findViewById(R.id.editText_destination);
        radioGroup = findViewById(R.id.radio_group);
        radioGroup.clearCheck();
        spinner = findViewById(R.id.spinner);
        progressBar1=findViewById(R.id.progress_bar1);

        Button buttonSearch = findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(v -> {
            String textSource = editTextSource.getText().toString();
            String textDestination = editTextDestination.getText().toString();

            if (TextUtils.isEmpty(textSource)) {
                Toast.makeText(PathFinder.this, "Please enter Source", Toast.LENGTH_LONG).show();
                editTextSource.setError("Starting point");
                editTextSource.requestFocus();
            } else if (TextUtils.isEmpty(textDestination)) {
                Toast.makeText(PathFinder.this, "Please enter Destination", Toast.LENGTH_LONG).show();
                editTextDestination.setError("Destination point");
                editTextDestination.requestFocus();
            } else if (radioGroup.getCheckedRadioButtonId() == -1) {
                // No radio button is selected

                Toast.makeText(PathFinder.this, "Please select AM or PM", Toast.LENGTH_LONG).show();
                radioGroup.requestFocus();
            } else {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selectedId);
                if (radioButton != null) {
                    if (radioButton.getId() == R.id.radio_AM) {
                        // AM radio button is selected
                        time = "AM";
                    } else if (radioButton.getId() == R.id.radio_PM) {
                        // PM radio button is selected
                        time = "PM";
                    }
                }
                // Get the selected item from the spinner
                String selectedTimeOption = spinner.getSelectedItem().toString();
                //Toast.makeText(this, textSource+" "+textDestination+" "+time+" "+selectedTimeOption, Toast.LENGTH_SHORT).show();
                // Create a new Intent object with the current activity and the target activity class
                progressBar1.setVisibility(View.VISIBLE);
                if (!Python.isStarted()) {
                    Python.start(new AndroidPlatform(this));
                }
                Python py = Python.getInstance();
                PyObject module = py.getModule("script");

                String source = textSource.toUpperCase();
                String destination = textDestination.toUpperCase();
                String time_period = time.toUpperCase();
                String time_interval = selectedTimeOption.toUpperCase();
                PyObject runModels = module.get("run_model");
                PyObject result = runModels.call(source, destination, time_period, time_interval);
                String a = result.toString();
                progressBar1.setVisibility(View.GONE);
             Intent intent = new Intent(PathFinder.this, PythonRunner.class);

                intent.putExtra("source", source);
                intent.putExtra("destination", destination);
                intent.putExtra("time", time_period);
                intent.putExtra("interval", time_interval);
                intent.putExtra("a", a);


                startActivity(intent);
                finish();


            }
        });
    }
}
