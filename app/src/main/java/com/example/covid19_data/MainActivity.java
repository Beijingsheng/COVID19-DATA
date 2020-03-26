package com.example.covid19_data;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Date;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {
    EditText country;
    EditText date;
    Button search;
    TextView confirm_num;
    TextView death_num;
    TextView recover_num;
    RequestQueue mQueue;
    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        country = findViewById(R.id.country);
        date = findViewById(R.id.date);
        search = findViewById(R.id.search);
        confirm_num = findViewById(R.id.confirm_num);
        death_num = findViewById(R.id.death_num);
        recover_num = findViewById(R.id.recover_num);
        mQueue = Volley.newRequestQueue(this);
        graph = (GraphView) findViewById(R.id.graph);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country_name = country.getText().toString();
                String date_y_m_d = date.getText().toString();
                jsonParse(country_name, date_y_m_d);
            }
        });
    }

    private void jsonParse(final String country, final String date) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://pomber.github.io/covid19/timeseries.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jOb = new JSONObject(response);
                            JSONArray jsonArray = jOb.getJSONArray(country);

                            graph.removeAllSeries();

                            LineGraphSeries<DataPoint> series_confirm = new LineGraphSeries<DataPoint>(new DataPoint[] {
                                    new DataPoint(0,0)
                            });
                            LineGraphSeries<DataPoint> series_death = new LineGraphSeries<DataPoint>(new DataPoint[] {
                                    new DataPoint(0,0)
                            });
                            LineGraphSeries<DataPoint> series_recover = new LineGraphSeries<DataPoint>(new DataPoint[] {
                                    new DataPoint(0,0)
                            });
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject d = jsonArray.getJSONObject(i);
                                String dt = d.getString("date");
                                int confirmed = d.getInt("confirmed");
                                int death = d.getInt("deaths");
                                int recovered = d.getInt("recovered");
                                series_confirm.appendData(new DataPoint(i, confirmed), true, 10000);
                                series_death.appendData(new DataPoint(i, death), true, 10000);
                                series_recover.appendData(new DataPoint(i, recovered), true, 10000);

                                if (dt.equals(date)) {
                                    confirm_num.setText(Integer.toString(confirmed));
                                    death_num.setText(Integer.toString(death));
                                    recover_num.setText(Integer.toString(recovered));
                                    series_confirm.setColor(Color.rgb(115,211,230));
                                    series_death.setColor(Color.rgb(15,11,30));
                                    series_recover.setColor(Color.rgb(215,111,90));
                                    graph.addSeries(series_confirm);
                                    graph.addSeries(series_death);
                                    graph.addSeries(series_recover);
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                confirm_num.setText("That didn't work!");
            }
        });
        queue.add(stringRequest);
    }
}
