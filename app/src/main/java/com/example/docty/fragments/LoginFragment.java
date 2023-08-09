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


public class LoginFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;



    public LoginFragment() {

    }


    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextView register = view.findViewById(R.id.register);
        EditText email = view.findViewById(R.id.email);
        EditText password = view.findViewById(R.id.password);
        Button login = view.findViewById(R.id.login);

        register.setOnClickListener(v->{
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
        });

        login.setOnClickListener(v->{
            String emailStr = email.getText().toString();
            String passStr = password.getText().toString();
            if (emailStr.isEmpty() && passStr.isEmpty()){
                Toast.makeText(getContext(), "Please Enter all Field", Toast.LENGTH_SHORT).show();
            }
            else{
                MainActivity.auth.signInWithEmailAndPassword(
                        emailStr,passStr
                        ).addOnSuccessListener(task->{
                            MainActivity.user = MainActivity.auth.getCurrentUser();
                            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_dashboard);
                })
                        .addOnFailureListener(task->{
                            Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        return view;
    }
}