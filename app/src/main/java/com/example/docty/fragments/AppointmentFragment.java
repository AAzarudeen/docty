package com.example.docty.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.docty.CalAct;
import com.example.docty.CallActivity;
import com.example.docty.R;
import com.example.docty.activity.MainActivity;
import com.example.docty.adapters.AppointmetListAdapter;

public class AppointmentFragment extends Fragment {

    String isEmpty;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AppointmentFragment() {

    }

    public static AppointmentFragment newInstance(String param1, String param2) {
        AppointmentFragment fragment = new AppointmentFragment();
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
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        CardView addAppointment = view.findViewById(R.id.add_appointment);
        CardView viewAppointment = view.findViewById(R.id.view_appointment);

//        RecyclerView appointmentList = view.findViewById(R.id.appointment_list);
//
//        appointmentList.setLayoutManager(new LinearLayoutManager(getContext()));
//        appointmentList.setAdapter(new AppointmetListAdapter(getContext()));

        MainActivity.db
                .collection("appointment")
                .document(MainActivity.user.getUid())
                .get().addOnSuccessListener(task->{
                    isEmpty = task.getString("status");
                })
                .addOnFailureListener(task->{

                });

        addAppointment.setOnClickListener(v->{
            if (isEmpty.equals("true")){
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment,new AddAppointmentFragment())
                        .commit();
            }
            else{
                Toast.makeText(getContext(), "Appointment booked", Toast.LENGTH_SHORT).show();
            }

        });

        viewAppointment.setOnClickListener(v->{
            if (isEmpty.equals("false")){
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment,new ViewAppointmentFragment())
                        .commit();
            }
            else{
                Toast.makeText(getContext(), "No current appointment", Toast.LENGTH_SHORT).show();
            }
//            startActivity(new Intent(getContext(), CalAct.class));
        });
        return view;
    }
}