package com.example.docty.fragments;

import android.app.Dialog;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.docty.DoctorProfileFragment;
import com.example.docty.R;
import com.example.docty.Utils;
import com.example.docty.activity.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

public class DashboardFragment extends Fragment {

    Button doctorProfile,enterCode;

    TextView userName;
    ImageView profile;

    CardView connectCard;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DashboardFragment() {

    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        userName = view.findViewById(R.id.username);
        profile = view.findViewById(R.id.profile);
        connectCard = view.findViewById(R.id.connect_card);
        doctorProfile = view.findViewById(R.id.doctorProfile);
        enterCode = view.findViewById(R.id.enterCode);

        Glide
                .with(getContext())
                .load(Utils.profile)
                .into(profile);

        userName.setText(Utils.name);

        if (Utils.doctorID.equals("")){
            connectCard.setVisibility(View.VISIBLE);
            doctorProfile.setVisibility(View.GONE);
            enterCode.setOnClickListener(v->{

                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.enter_code_dialog);

                dialog.setTitle("Enter doctor code");

                EditText code = dialog.findViewById(R.id.code);
                Button submit =  dialog.findViewById(R.id.submit);
                submit.setOnClickListener(v1 -> MainActivity
                        .db
                        .collection("users")
                        .document(MainActivity.user.getUid())
                        .update("doctorID",code.getText().toString())
                        .addOnSuccessListener(task->{
                            Toast.makeText(getContext(), "Connected with doctor", Toast.LENGTH_SHORT).show();
                            getActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_fragment,new MainFragment())
                                    .commit();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(task->{
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }));
                dialog.show();
            });
        }
        else{
            connectCard.setVisibility(View.GONE);
            doctorProfile.setVisibility(View.VISIBLE);
            doctorProfile.setOnClickListener(v->{
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment,new DoctorProfileFragment())
                        .commit();
            });
        }
        return view;
    }
}