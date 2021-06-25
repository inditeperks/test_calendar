package com.inditeperks.lege;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TestActivity extends AppCompatActivity implements OnMonthChangedListener {

    private MaterialCalendarView materialCalendarView;
    private List<CalendarDay> calevents = new ArrayList<>();
    private List<Event> eventList = new ArrayList<>();
    private HashMap<Integer,List<Event>> map = new HashMap<>();
    private ListView listView;
    private MyAdapter adapter;
    private Calendar cal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        materialCalendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader);
        materialCalendarView.setWeekDayTextAppearance(R.style.CalendarWidgetWeekDay);
        materialCalendarView.setDateTextAppearance(R.style.CalendarWidgetWeekDate);

        listView = (ListView)findViewById(R.id.listview);

        materialCalendarView.setDateTextAppearance(View.ACCESSIBILITY_LIVE_REGION_ASSERTIVE);
        final Calendar calendar = Calendar.getInstance();
        materialCalendarView.setSelectedDate(calendar.getTime());
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                materialCalendarView.setHeaderTextAppearance(R.style.AppTheme);
                Toast.makeText(getApplicationContext(), date.getDay() + "/" + date.getMonth() + "/" + date.getYear(), Toast.LENGTH_SHORT).show();
                adapter = new MyAdapter(TestActivity.this,eventList);
                listView.setAdapter(adapter);
            }
        });


        materialCalendarView.setOnMonthChangedListener(this);

        makeJsonObjectRequest();

    }

    private void makeJsonObjectRequest() {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        String response = loadJSONFromAsset();
        try {
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jsonObject = jArray.getJSONObject(i);
                String StartDate = jsonObject.getString("StartDate");
                Date date = simpleDateFormat.parse(StartDate);

                String title =  jsonObject.getString("Title");

                Log.d("Date ",""+date);
                CalendarDay day = CalendarDay.from(date);
                Event event = new Event(date,title);
                cal = Calendar.getInstance();
                cal.setTime(date);
                int month = cal.get(Calendar.MONTH);

                if(!map.containsKey(month))
                {
                    List<Event> events = new ArrayList<>();
                    events.add(event);
                    map.put(month,events);
                }else
                {
                    List<Event> events = map.get(month);
                    events.add(event);
                    map.put(month,events);

                }

                calevents.add(day);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // after parsing
        cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        List<Event> event =  map.get(month);
        if(event!=null && event.size()>0)
            adapter.addItems(event);
        EventDecorator eventDecorator = new EventDecorator(Color.RED, calevents);
        materialCalendarView.addDecorator(eventDecorator);

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = TestActivity.this.getAssets().open("testjson.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.getDate());
        int month = cal.get(Calendar.MONTH);
        List<Event> event =  map.get(month);
        if(event!=null && event.size()>0)
            adapter.addItems(event);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}