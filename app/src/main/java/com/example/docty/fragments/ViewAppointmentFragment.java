package com.example.docty.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.docty.CalAct;
import com.example.docty.R;
import com.example.docty.activity.MainActivity;

public class ViewAppointmentFragment extends Fragment {

    TextView name,description,date,time;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ViewAppointmentFragment() {

    }

    public static ViewAppointmentFragment newInstance(String param1, String param2) {
        ViewAppointmentFragment fragment = new ViewAppointmentFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_appointment, container, false);
        Button finished = view.findViewById(R.id.finished);
        Button join = view.findViewById(R.id.join_meeting);
        name = view.findViewById(R.id.name);
        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        description = view.findViewById(R.id.description);

        MainActivity.db
                .collection("appointment")
                .document(MainActivity.user.getUid())
                .get()
                .addOnSuccessListener(task->{
                    name.setText(task.getString("name"));
                    date.setText(task.getString("date"));
                    time.setText(task.getString("time"));
                    description.setText(task.getString("description"));
                })
                .addOnFailureListener(task->{

                });

        finished.setOnClickListener(v->{
            MainActivity.db.collection("appointment")
                    .document(MainActivity.user.getUid())
                    .update("status","true").addOnSuccessListener(task->{
                    })
                    .addOnFailureListener(task->{});

        });


        join.setOnClickListener(v->{
            startActivity(new Intent(getContext(), CalAct.class));
        });

        return view;
    }
}