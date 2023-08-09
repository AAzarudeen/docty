package com.example.docty;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.docty.activity.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoctorProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoctorProfileFragment extends Fragment {

    ImageView profile;
    EditText name, phone, experience, specialization, email;
    TextView dob;
    RadioGroup gender;
    Button save;
    Uri profileUri;

    String genderStr = "male";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DoctorProfileFragment() {
        // Required empty public constructor
    }

    public static DoctorProfileFragment newInstance(String param1, String param2) {
        DoctorProfileFragment fragment = new DoctorProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        profile = view.findViewById(R.id.profile);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        dob = view.findViewById(R.id.dob);
        gender = view.findViewById(R.id.gender);
        experience = view.findViewById(R.id.experience);
        specialization = view.findViewById(R.id.specialization);
        email = view.findViewById(R.id.email);

        MainActivity.storageRef
                .child(String.format("profiles/%s",Utils.doctorID))
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide
                                .with(getContext())
                                .load(uri)
                                .into(profile);
                    }
                }).addOnFailureListener(exception -> {
                    Toast.makeText(getContext(), "Error in loading image", Toast.LENGTH_SHORT).show();
                });

        MainActivity.db
                .collection("doctor")
                .document(Utils.doctorID)
                .get()
                .addOnSuccessListener(task->{
                    name.setText(task.getString("name"));
                    dob.setText(task.getString("dob"));
                    specialization.setText(task.getString("specialization"));
                    experience.setText(task.getString("experience"));
                    phone.setText(task.getString("phone"));
                    email.setText(task.getString("email"));

                    if (task.getString("gender").equals("male")) {
                        gender.check(R.id.male);
                    }
                    if (task.getString("gender").equals("female")) {
                        gender.check(R.id.female);
                    }
                });

        return view;
    }
}