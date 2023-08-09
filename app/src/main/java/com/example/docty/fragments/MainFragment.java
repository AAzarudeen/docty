package com.example.docty.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.docty.R;
import com.example.docty.Utils;
import com.example.docty.activity.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainFragment extends Fragment implements NavigationBarView.OnItemSelectedListener{

    BottomNavigationView bottomNavigationView;

    DashboardFragment dashboardFragment;
    ProfileFragment profileFragment;
    PrescriptionFragment prescriptionFragment;
    AppointmentFragment appointmentFragment;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MainFragment() {

    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        MainActivity.storageRef
                .child(String.format("profiles/%s",MainActivity.user.getUid()))
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                            Utils.profile = uri;
                    }
                }).addOnFailureListener(exception -> {
                    Toast.makeText(getContext(), "Error in loading image", Toast.LENGTH_SHORT).show();
                });

        MainActivity.db
                .collection("users")
                .document(MainActivity.user.getUid())
                .get()
                .addOnSuccessListener(task->{
                    Utils.name = task.getString("name");
                    Utils.gender = task.getString("gender");
                    Utils.dob = task.getString("dob");
                    Utils.height = task.getString("height");
                    Utils.weight = task.getString("weight");
                    Utils.phone = task.getString("phone");
                    Utils.bloodType = task.getString("blood_type");
                    Utils.doctorID = task.getString("doctorID");
                    bottomNavigationView.setSelectedItemId(R.id.dashboard);
                })
                .addOnFailureListener(task->{
                    Toast.makeText(getContext(), task.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println(task.getMessage());
                });

        dashboardFragment = new DashboardFragment();
        profileFragment = new ProfileFragment();
        prescriptionFragment = new PrescriptionFragment();
        appointmentFragment = new AppointmentFragment();

        bottomNavigationView = view.findViewById(R.id.bottom_nav);
            bottomNavigationView
                    .setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile){
            changeFragment(profileFragment);
            return true;
        }
        if (Utils.doctorID.equals("")){
            Toast.makeText(getContext(), "Please connect with your doctor", Toast.LENGTH_SHORT).show();
            changeFragment(dashboardFragment);
            return false;
        }else{
            switch (item.getItemId()){
                case R.id.dashboard:
                    changeFragment(dashboardFragment);
                    return true;
                case R.id.prescription:
                    changeFragment(prescriptionFragment);
                    return true;
                case R.id.profile:
                    changeFragment(profileFragment);
                    return true;
                case R.id.appointment:
                    changeFragment(appointmentFragment);
                    return true;
            }
        }
        return false;
    }

    void changeFragment(Fragment fragment){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment,fragment)
                .commit();
    }
}