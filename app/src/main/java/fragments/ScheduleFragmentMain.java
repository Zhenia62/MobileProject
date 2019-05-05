package fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.teleg.programm.CalendarClasses.HorizontalCalendar;
import com.example.teleg.programm.CalendarClasses.utils.HorizontalCalendarListener;
import com.example.teleg.programm.R;
import com.example.teleg.programm.serverSide.Api.ClientJson;
import com.example.teleg.programm.serverSide.Api.RsreuApi;
import com.example.teleg.programm.serverSide.Schedule.Lesson;
import com.example.teleg.programm.serverSide.Schedule.PostsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


import io.reactivex.android.schedulers.AndroidSchedulers;

public class ScheduleFragmentMain extends Fragment {

    private RecyclerView recyclerView;
    private TextView typeWeek;
    private List<Lesson> numerator;
    private List<Lesson> denominator;
    private List<Lesson> currentListLessons;
    private final Calendar startSem = new GregorianCalendar();
    private Date date;
    private Calendar novaday;

    private RsreuApi api = ClientJson.getInstance().getApi();
    SimpleDateFormat sdf = new SimpleDateFormat("dd.M.yyyy");

    public ScheduleFragmentMain() {
    }

    public static ScheduleFragmentMain newInstance() {
        return new ScheduleFragmentMain();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_main, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.schedule_content_recyclerView);
        typeWeek = (TextView) rootView.findViewById(R.id.type_week);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        date = new Date();

        novaday = new GregorianCalendar();
        novaday.setTime(date);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);



        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                .formatTopText("MMM")
                .formatMiddleText("dd")
                .formatBottomText("EEE")
                .textSize(10f, 14f, 10f)
                .showTopText(true)
                .showBottomText(true)
                .textColor(Color.LTGRAY, Color.WHITE)
                .end()
                .defaultSelectedDate(Calendar.getInstance())
                .build();

        numerator = new ArrayList<>();
        denominator = new ArrayList<>();
        currentListLessons = new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String group = sharedPreferences.getString("key_gallery_name", "");

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            loadSchedule(group);
        }


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                currentListLessons.clear();

                if (isNumerator(startSem, date)) {
                    typeWeek.setText("Числитель");
                    for (Lesson lesson : numerator
                            ) {

                        if (date.get(Calendar.DAY_OF_WEEK) == lesson.getWeekDay() + 1) {
                            currentListLessons.add(lesson);
                        }
                    }
                    Collections.sort(currentListLessons, Lesson.COMPARE_BY_COUNT);
                    //Log.d("Неделя", currentListLessons.toString());
                    PostsAdapter adapter = new PostsAdapter(currentListLessons);

                    recyclerView.setAdapter(adapter);
                } else {
                    typeWeek.setText("Знаменатель");
                    for (Lesson lesson : denominator
                            ) {

                        if (date.get(Calendar.DAY_OF_WEEK) == lesson.getWeekDay() + 1) {
                            currentListLessons.add(lesson);
                        }
                    }

                    Collections.sort(currentListLessons, Lesson.COMPARE_BY_COUNT);

                    PostsAdapter adapter = new PostsAdapter(currentListLessons);

                    recyclerView.setAdapter(adapter);
                }
            }
        });

        return rootView;
    }

    public void loadSchedule(String str) {

        api.getSettings()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(settings -> {
                    try{
                        startSem.setTime(sdf.parse(settings.getStartDate()));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                });

        api.getRasp(str)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> {
                    numerator.addAll(posts.getNumerator());
                    Log.d("Числитель", numerator.toString());
                    denominator.addAll(posts.getDenominator());
                    Log.d("Знаменатель", denominator.toString());

                    Calendar novaday = Calendar.getInstance();
                    currentListLessons.clear();
                    if (isNumerator(startSem, novaday)) {
                        typeWeek.setText("Числитель");
                        for (Lesson lesson : numerator
                                ) {

                            if (novaday.get(Calendar.DAY_OF_WEEK) == lesson.getWeekDay() + 1) {
                                currentListLessons.add(lesson);
                                Log.d("Logging", currentListLessons.toString());
                            }
                        }
                    } else {
                        typeWeek.setText("Знаменатель");
                        for (Lesson lesson : denominator
                                ) {

                            if (novaday.get(Calendar.DAY_OF_WEEK) == lesson.getWeekDay() + 1) {
                                currentListLessons.add(lesson);
                                Log.d("Logging", currentListLessons.toString());
                            }
                        }
                    }

                    Collections.sort(currentListLessons, Lesson.COMPARE_BY_COUNT);

                    PostsAdapter adapterNow = new PostsAdapter(currentListLessons);

                    recyclerView.setAdapter(adapterNow);

                }, Throwable::printStackTrace);
    }

    public boolean isNumerator(Calendar startSem, Calendar novaday) {
        return (novaday.get(Calendar.WEEK_OF_YEAR) - startSem.get(Calendar.WEEK_OF_YEAR)) % 2 == 0;
    }
}

