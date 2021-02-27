package com.example.sznr;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements  View.OnClickListener {

    public int pocetakRadnogVremena , krajRadnogVremena ,delay;


    private Button buttonSalji;
    Button btnDatePicker, btnTimePicker;
    TextView txtDate, txtTime;
    ListView listView;
    EditText editTextIme; // 1.

    JSONObject jsonObject=new JSONObject();

    public int mYear, mMonth, mDay, mHour, mMinute, mDanMjeseca;
    boolean vri = false, dat = false; // vrijeme i datum
    boolean internet = false,downloaded=false;
    boolean firstTime=false;
    boolean err=false;
    boolean jeLiTerminZauzet = false;

    public StringBuilder sDate = new StringBuilder("");
    public StringBuilder sTime = new StringBuilder("");
    public StringBuilder link = new StringBuilder("");

    ArrayList<String> arrayList = new ArrayList<>();
    List<Calendar> kalendar = new ArrayList<>();


    String words;


    public class doit extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


//            try {
//
//                //parse  date
//                String[] parts = words.split("&");
//
//                kalendar.clear();
//
//                for (int i = 0; i < parts.length; i++) {
//
//                    String[] dateAndTime = parts[i].split("\\.");  // 14.06.2012.06.12&
//                    // 0  1   2  3  4
//                    Calendar tren = Calendar.getInstance();
//                    tren.set(Integer.parseInt(dateAndTime[2]), Integer.parseInt(dateAndTime[1]) - 1, Integer.parseInt(dateAndTime[0]), Integer.parseInt(dateAndTime[3]), Integer.parseInt(dateAndTime[4]));
//
//                    kalendar.add((Calendar) tren.clone());
//
//
//                }
//
//                for (int i = 0; i < kalendar.size(); i++) {
//
//                    // ova jebena java krecu mjeseci od 0, uvijek moras za 1 manje spremat, (cast) .clone(da se ne dodijeli svemu
//
//                    Calendar t = (Calendar) kalendar.get(i).clone();
//
//                    Calendar krajSisanja = (Calendar) kalendar.get(i).clone();
//
//                    krajSisanja.add(Calendar.MINUTE, 30);
//
//
//                    arrayList.add("Dan         :   " + t.get(Calendar.DAY_OF_MONTH) + '\n' + "Mjesec   :   " + (t.get(Calendar.MONTH) + 1) + '\n' + "Godina   :   " + t.get(Calendar.YEAR) + '\n' + "Vrijeme  :   " + t.get(Calendar.HOUR_OF_DAY) + ":" + t.get(Calendar.MINUTE) + " - " + krajSisanja.get(Calendar.HOUR_OF_DAY) + ":" + krajSisanja.get(Calendar.MINUTE));
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            return null;
        }


    }

    public void downloadWorkTime(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        final String url = "https://szn.azurewebsites.net/api/hello?work=0";

// prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        jsonObject=response;
                        Log.d("Response", response.toString());

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview);

        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

            internet = true;
            //Create Adapter
             //new doit().execute();
            downloadWorkTime();
            downloaded=true;

            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);

            //assign adapter to listview

            listView.setAdapter(arrayAdapter);

            listView.setVisibility(View.INVISIBLE);

        } else {

            Toast.makeText(getApplicationContext(), "Nema interneta!", Toast.LENGTH_SHORT).show();

        }

        buttonSalji = (Button) findViewById(R.id.button);
        btnDatePicker = (Button) findViewById(R.id.btn_date);
        btnTimePicker = (Button) findViewById(R.id.btn_time);
        txtDate = (TextView) findViewById(R.id.in_date);
        txtTime = (TextView) findViewById(R.id.in_time); //333
        editTextIme = (EditText) findViewById(R.id.ime); //2

        buttonSalji.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

            if(!downloaded){
                downloaded=true;
                downloadWorkTime();
            }

            try {
                pocetakRadnogVremena=jsonObject.getInt("start");
                krajRadnogVremena=jsonObject.getInt("stop");
                delay=jsonObject.getInt("delay");

            }catch (Exception exception){
                //do nothing
            }

            if (v == btnDatePicker) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                Date today = Calendar.getInstance().getTime();

                                Calendar datum = Calendar.getInstance();
                                datum.set(year, monthOfYear, dayOfMonth, 0, 0);


                                mDanMjeseca = dayOfMonth;

                                if (datum.getTime().after(today) || datum.getTime().equals(today)) {
                                    txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    txtDate.setError(null);
                                    dat = true;
                                    // 2008 06 14 T 131236

                                    sDate.append(year);
                                    if (monthOfYear < 10) sDate.append('0');
                                    sDate.append((monthOfYear + 1));
                                    if (dayOfMonth < 10) sDate.append('0');
                                    sDate.append(dayOfMonth);
                                    sDate.append('T');
                                } else {
                                    txtDate.setText(null);
                                    //  txtDate.getText().clear();
                                    txtDate.setError("Datum mora biti veći od današnjeg");
                                    dat = false;
                                }

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

            if (v == btnTimePicker) {

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                if (dat == false) {
                                    txtDate.setText(null);
                                    // txtTime.getText().clear();     111
                                    txtDate.setError("Prvo namijestite datum");
                                } else {

                                    if (hourOfDay >= pocetakRadnogVremena && hourOfDay <= krajRadnogVremena) {

                                        for (int i = 0; i < kalendar.size(); i++) {

//                                            Calendar trenutnoVrijeme = (Calendar) kalendar.get(i).clone();
//
//                                            if (trenutnoVrijeme.get(Calendar.YEAR) == mYear && trenutnoVrijeme.get(Calendar.MONTH) == mMonth && trenutnoVrijeme.get(Calendar.DAY_OF_MONTH) == mDanMjeseca) {
//                                                //   jeLiTerminZauzet = true;
//
//                                                Calendar krajVrijeme = (Calendar) (Calendar) kalendar.get(i).clone();
//
//                                                krajVrijeme.add(Calendar.MINUTE, MinuteIzmeduSisanja);
//
//                                                Calendar ovajTren = Calendar.getInstance();
//                                                ovajTren.set(mYear, mMonth, mDanMjeseca, hourOfDay, minute);
//
//                                                //here check wheter appoitment is available
//                                                if ((ovajTren.after(trenutnoVrijeme) || ovajTren.equals(trenutnoVrijeme)) && (ovajTren.before(krajVrijeme))) {
//
//                                                    jeLiTerminZauzet = true;
//                                                    break;
//
//                                                } else {
//
//                                                    jeLiTerminZauzet = false;
//                                                    listView.setVisibility(View.INVISIBLE);
//
//                                                }
//                                            }
                                        }

  //                                      if (!jeLiTerminZauzet) {

                                            txtTime.setText(hourOfDay + ":" + minute);
                                            txtTime.setError(null);
                                            vri = true;                     // 2008 06 14 T 13 12 36

                                            if (hourOfDay < 10) sTime.append('0');
                                            sTime.append(hourOfDay);
                                            if (minute < 10) sTime.append('0');
                                            sTime.append(minute);
                                            sTime.append("00");

//                                        }

                                    } else {
                                        txtTime.setText(null);
                                        txtTime.setError("Vrijeme mora biti unutar radnog vremena: " +pocetakRadnogVremena+" - "+krajRadnogVremena);
                                        vri = false;
                                    }
                                }

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }

            if (!AppStatus.getInstance(getApplicationContext()).isOnline()) {

                Toast.makeText(getApplicationContext(), "Nema interneta!", Toast.LENGTH_SHORT).show();

            } else {

                if (v == buttonSalji) {

                    if (TextUtils.isEmpty(editTextIme.getText().toString())) {
                        editTextIme.setError("Unesite ime");

                    } else if(firstTime==true){

                        Toast.makeText(getApplicationContext(), "Zahtijev je vec poslan, za ponovno slanje izadite pa udite u aplikaciju", Toast.LENGTH_SHORT).show();



                    }
                    else {

                        if (vri == true && dat == true ) {

                            vri = false;
                            dat = false;

                            firstTime=true;


                            link.append("https://szn.azurewebsites.net/api/hello?param1=");
                            link.append(sDate);
                            link.append(sTime);
                            link.append("&param2=");
                            link.append(editTextIme.getText());
                            link.append("&api_key=M6N015C8W6ALJ");

                            String url=link.toString();
                            RequestQueue queue = Volley.newRequestQueue(this);
                            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // response
                                            jeLiTerminZauzet=true;
                                            Log.d("Response", response);

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // error
                                            //Log.d("Error.Response", error.getMessage());
                                            err=true;
                                            Toast.makeText(getApplicationContext(), "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            ) {

                            };
                            queue.add(postRequest);

                            if(err){
                                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                if (!jeLiTerminZauzet)
                                    Toast.makeText(getApplicationContext(), "Zahtijev je poslan!", Toast.LENGTH_SHORT).show();
                                else {

                                    txtTime.setText(null);
                                    txtTime.setError("Termin je zauzet, pogledajte u talici za slobodne");

                                    listView.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        else {

                            Toast.makeText(getApplicationContext(), "Vrijeme ili datum nisu postavljeni!", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }

        } else {

            Toast.makeText(getApplicationContext(), "Nema interneta!", Toast.LENGTH_SHORT).show();

        }
    }

}









