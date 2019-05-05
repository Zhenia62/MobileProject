package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.teleg.programm.R;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalenderFragment extends Fragment {


    private CalendarPickerView calendar;

    public CalenderFragment() {
    }

    public static CalenderFragment newInstance() {
        return new CalenderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calender, container, false);

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 10);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, - 10);

        calendar = view.findViewById(R.id.calendar_view);
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);

        calendar.deactivateDates(list);
        ArrayList<Date> arrayList = new ArrayList<>();
        try {
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
            String strdate = "22-4-2019";
            String strdate2 = "30-4-2019";
            Date newdate = dateformat.parse(strdate);
            Date newdate2 = dateformat.parse(strdate2);
            arrayList.add(newdate);
            arrayList.add(newdate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("MMMM, yyyy", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(new Date())
                .withDeactivateDates(list)
                .withHighlightedDates(arrayList);
        return view;
    }
}