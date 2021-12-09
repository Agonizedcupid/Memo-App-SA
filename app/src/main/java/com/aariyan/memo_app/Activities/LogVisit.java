package com.aariyan.memo_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aariyan.memo_app.Interface.CurrentLocation;
import com.aariyan.memo_app.Networking.APIs;
import com.aariyan.memo_app.Networking.ApiClient;
import com.aariyan.memo_app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogVisit extends AppCompatActivity {

    private Button datePickerBtn;

    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    int day, month, year;

    private TextView customerName, customerCode;
    private String name, code;
    private TextView lastVisit, lastMessage;
    private String date;

    private Button saveLogVisit;

    private EditText notes, catchUpNotes;

    FusedLocationProviderClient client;

    private static double latitude = 0.0, longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_visit);

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        client = LocationServices.getFusedLocationProviderClient(this);

        if (getIntent() != null) {
            name = getIntent().getStringExtra("name");
            code = getIntent().getStringExtra("code");
        }

        initUI();

        loadUserMessage();

        if (ActivityCompat.checkSelfPermission(LogVisit.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LogVisit.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
    }

    private void getCurrentLocation(CurrentLocation currentLocation) {
        //Check the location permission:
        if (ActivityCompat.checkSelfPermission(LogVisit.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(LogVisit.this, Locale.getDefault());
                            List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            currentLocation.getLocation(list.get(0).getLatitude(), list.get(0).getLongitude());
                        } catch (Exception e) {

                        }
                    }
                }
            });
        } else {
            //request for the location access
            ActivityCompat.requestPermissions(LogVisit.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
    }

    private void loadUserMessage() {
        APIs apiService = ApiClient.getClient().create(APIs.class);
        Call<ResponseBody> call = apiService.getCustomerVisitMessage(code);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONArray finalResponse = new JSONArray(response.body().string());
                    if (finalResponse.length() != 0) {
                        for (int i = 0; i < finalResponse.length(); i++) {
                            JSONObject object = finalResponse.getJSONObject(i);
                            String notes = object.getString("notes");
                            String lastDate = object.getString("lastDate");

                            lastMessage.setText(notes);
                            lastVisit.setText("Last Visit   " + lastDate);
                        }
                    } else {
                        lastMessage.setText("No data found!");
                        lastVisit.setText("No last visit date found!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LogVisit.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {

        saveLogVisit = findViewById(R.id.saveLogVisitBtn);
        lastVisit = findViewById(R.id.lastVisit);
        lastMessage = findViewById(R.id.lastMessage);

        notes = findViewById(R.id.notesEditText);
        catchUpNotes = findViewById(R.id.catchUpNoteEditText);

        customerName = findViewById(R.id.customerName);
        customerName.setText("Name: " + name);
        customerCode = findViewById(R.id.customerCode);
        customerCode.setText("Code: " + code);

        datePickerBtn = findViewById(R.id.datePickerBtn);
        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        saveLogVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIs apiService = ApiClient.getClient().create(APIs.class);

                getCurrentLocation(new CurrentLocation() {
                    @Override
                    public void getLocation(double latitude, double longitude) {
                        Call<String> call = apiService.submitLogVisit(code, "" + notes.getText().toString().trim(),
                                "" + catchUpNotes.getText().toString(), "" + date,
                                1, latitude, longitude);

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                String success = response.body();
                                if (success.equals("Success")) {
                                    onBackPressed();
                                    Toast.makeText(LogVisit.this, "Data is successfully saved!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LogVisit.this, "Failed to save data!", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(LogVisit.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });
    }

    private void showDate() {
        datePickerDialog = new DatePickerDialog(LogVisit.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                int j = i1 + 1;

                date = i + "-" + j + "-" + i2;
                Toast.makeText(LogVisit.this, "You've selected " + date, Toast.LENGTH_LONG).show();
                datePickerBtn.setText(date + "  Selected!");
                //Toast.makeText(AddProperty.this, "" + availableStatus, Toast.LENGTH_SHORT).show();

            }
        }, day, month, year);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }
}