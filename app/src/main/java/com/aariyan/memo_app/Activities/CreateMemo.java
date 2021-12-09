package com.aariyan.memo_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aariyan.memo_app.Constant.Constant;
import com.aariyan.memo_app.Networking.APIs;
import com.aariyan.memo_app.Networking.ApiClient;
import com.aariyan.memo_app.R;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMemo extends AppCompatActivity {


    private Spinner spinner;

    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    int day, month, year;
    String date = "";

    int hour, time;
    private static String amPm = "";
    String name = "", code = "";

    private TextView customerName,customerCode;
    private EditText notes;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memo);

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        if (getIntent() != null) {
            name = getIntent().getStringExtra("name");
            code = getIntent().getStringExtra("code");
        }

        initUI();
    }

    private void initUI() {

        customerCode = findViewById(R.id.cCodes);
        customerCode.setText(code);
        customerName = findViewById(R.id.cNames);
        customerName.setText(name);
        notes = findViewById(R.id.notesEdtText);
        saveBtn = findViewById(R.id.saveBtn);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.remind_spinner, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if (item.equals(Constant.EVERYDAY)) {
                    showWatch();
                } else {
                    showDatePicker();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIs apiService = ApiClient.getClient().create(APIs.class);
                Call<String> call = apiService.createMemo(code, "" + notes.getText().toString().trim(),
                        "" + date,
                        1);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String success = response.body();
                        if (success.equals("Success")) {
                            onBackPressed();
                            Toast.makeText(CreateMemo.this, "Data is successfully saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateMemo.this, "Failed to save data!", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(CreateMemo.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showDatePicker() {
        datePickerDialog = new DatePickerDialog(CreateMemo.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                int j = i1 + 1;

                date = i + "-" + j + "-" + i2;
                Toast.makeText(CreateMemo.this, "You've selected " + date, Toast.LENGTH_LONG).show();
                //Toast.makeText(AddProperty.this, "" + availableStatus, Toast.LENGTH_SHORT).show();

            }
        }, day, month, year);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private void showWatch() {

        TimePickerDialog dialog = new TimePickerDialog(CreateMemo.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hour = i;
                time = i1;

                if (hour >= 12) {
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(0, 0, 0, hour, time);

                String time = DateFormat.format("hh:mm", calendar).toString();

                String taskTime = time + " " + amPm;

                Toast.makeText(CreateMemo.this, "You've selected " + taskTime, Toast.LENGTH_SHORT).show();
            }
        }, 24, 0, false);

        dialog.updateTime(hour, time);
        dialog.show();
    }
}