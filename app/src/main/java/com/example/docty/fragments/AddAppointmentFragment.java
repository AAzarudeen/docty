package com.example.docty.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.docty.R;
import com.example.docty.activity.MainActivity;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddAppointmentFragment extends Fragment implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    int dayToday, monthToday, yearToday;
    int minutesNow, hoursNow;

    boolean isEmpty;

    TextView dateTv,timeTv;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AddAppointmentFragment() {

    }

    public static AddAppointmentFragment newInstance(String param1, String param2) {
        AddAppointmentFragment fragment = new AddAppointmentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_appointment, container, false);

        EditText nameEdt = view.findViewById(R.id.name);
        EditText descriptionEdt = view.findViewById(R.id.description);
        dateTv = view.findViewById(R.id.date);
        timeTv = view.findViewById(R.id.time);
        Button create = view.findViewById(R.id.create);

        Calendar calendar = Calendar.getInstance();
        yearToday = calendar.get(Calendar.YEAR);
        monthToday = calendar.get(Calendar.MONTH);
        dayToday = calendar.get(Calendar.DAY_OF_MONTH);
        minutesNow = calendar.get(Calendar.MINUTE);
        hoursNow = calendar.get(Calendar.HOUR);

        dateTv.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AddAppointmentFragment.this, yearToday, monthToday, dayToday);
            datePickerDialog.show();
        });

        timeTv.setOnClickListener(v->{
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),AddAppointmentFragment.this,hoursNow,minutesNow,false);
            timePickerDialog.show();
        });

        create.setOnClickListener(v->{
                String name = nameEdt.getText().toString();
                String description = descriptionEdt.getText().toString();
                String date = dateTv.getText().toString();
                String time = timeTv.getText().toString();
                if (name.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty()){
                    Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                }else{
                    Map<String,String> data = new HashMap<>();
                    data.put("name",name);
                    data.put("description",description);
                    data.put("date",date);
                    data.put("time",time);
                    data.put("status","false");
                    MainActivity.db
                            .collection("appointment")
                            .document(MainActivity.user.getUid())
                            .set(data)
                            .addOnSuccessListener(task->{
                                Toast.makeText(getContext(), "Appointment created", Toast.LENGTH_SHORT).show();
                            });
                }
        });

        return view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearToday = year;
        monthToday = month;
        dayToday = dayOfMonth;
        dateTv.setText(String.format("%d/%d/%d", dayToday, monthToday + 1, yearToday));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hoursNow = hourOfDay;
        minutesNow = minute;
        timeTv.setText(String.format("%d:%d",hoursNow,minute));

    }
}