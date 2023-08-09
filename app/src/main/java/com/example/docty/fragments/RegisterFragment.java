package com.example.docty.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.docty.R;
import com.example.docty.activity.MainActivity;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public RegisterFragment() {

    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        TextView login = view.findViewById(R.id.login);
        EditText email = view.findViewById(R.id.email);
        EditText password = view.findViewById(R.id.password);
        Button register = view.findViewById(R.id.register_btn);

        register.setOnClickListener(v->{
            String emailStr = email.getText().toString();
            String passStr = password.getText().toString();
            if (emailStr.isEmpty() && passStr.isEmpty()){
                Toast.makeText(getContext(), "Please Enter all Field", Toast.LENGTH_SHORT).show();
            }
            else{
                register.setClickable(false);
                MainActivity.auth.createUserWithEmailAndPassword(
                                emailStr,passStr
                        ).addOnSuccessListener(task->{
                            MainActivity.user = MainActivity.auth.getCurrentUser();
                            Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_initialDetailsFragment);
                        })
                        .addOnFailureListener(task->{
                            register.setClickable(true);
                            if (task.getMessage().contains("The email address is already in use by another account.")){
                                Toast.makeText(getContext(), "Account already registered", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        login.setOnClickListener(v->{
            Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment);
        });
        return view;
    }
}