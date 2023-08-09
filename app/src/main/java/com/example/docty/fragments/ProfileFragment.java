package com.example.docty.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.docty.R;
import com.example.docty.Utils;
import com.example.docty.activity.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    int dayToday, monthToday, yearToday;

    ImageView profile;
    EditText name, phone, height, weight, bloodType;
    TextView dob;
    RadioGroup gender;
    Button save,logout;
    Uri profileUri;

    String genderStr = "male";

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            Uri contentUri = data.getData();
            profile.setImageURI(contentUri);
            profileUri = data.getData();
        }
    });

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profile = view.findViewById(R.id.profile);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        dob = view.findViewById(R.id.dob);
        gender = view.findViewById(R.id.gender);
        height = view.findViewById(R.id.height);
        weight = view.findViewById(R.id.weight);
        bloodType = view.findViewById(R.id.blood_type);
        save = view.findViewById(R.id.save);
        logout = view.findViewById(R.id.logout);

        logout.setOnClickListener(v->{
            getActivity().finish();
        });

        name.setText(Utils.name);
        phone.setText(Utils.phone);
        dob.setText(Utils.dob);
        height.setText(Utils.height);
        weight.setText(Utils.weight);
        bloodType.setText(Utils.bloodType);

        Glide
                .with(getContext())
                .load(Utils.profile)
                .into(profile);

        if (Utils.gender.equals("male")) {
            gender.check(R.id.male);
        }
        if (Utils.gender.equals("female")) {
            gender.check(R.id.female);
        }

        Calendar calendar = Calendar.getInstance();
        yearToday = calendar.get(Calendar.YEAR);
        monthToday = calendar.get(Calendar.MONTH);
        dayToday = calendar.get(Calendar.DAY_OF_MONTH);

        gender.setOnCheckedChangeListener((group, button) -> {
            if (button == R.id.male) {
                genderStr = "male";
            }
            if (button == R.id.female) {
                genderStr = "female";
            }
        });

        dob.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), ProfileFragment.this, yearToday, monthToday, dayToday);
            datePickerDialog.show();
        });

        profile.setOnClickListener(v -> {
            Intent data = new Intent(Intent.ACTION_GET_CONTENT);
            data.addCategory(Intent.CATEGORY_OPENABLE);
            data.setType("image/*");
            Intent intent = Intent.createChooser(data, "Choose a file");
            startActivityForResult.launch(intent);
        });

        save.setOnClickListener(v -> {

            Map<String, String> data = new HashMap<>();

            String nameStr = name.getText().toString();
            String dobStr = dob.getText().toString();
            String phoneStr = phone.getText().toString();
            String heightStr = height.getText().toString();
            String weightStr = weight.getText().toString();
            String bloodTypeStr = bloodType.getText().toString();

            data.put("name", nameStr);
            data.put("gender", genderStr);
            data.put("dob", dobStr);
            data.put("doctorID",Utils.doctorID);
            data.put("phone", phoneStr);
            data.put("height", heightStr);
            data.put("weight", weightStr);
            data.put("blood_type", bloodTypeStr);

            Bitmap bitmap = ((BitmapDrawable) profile.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageByte = baos.toByteArray();

            StorageReference riversRef = MainActivity.storageRef.child("profiles/" + MainActivity.user.getUid() + "/");
            UploadTask uploadTask = riversRef.putBytes(imageByte);

            uploadTask.addOnSuccessListener(task -> {
                        Toast.makeText(getContext(), "Details saved", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(task -> {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        System.out.println(task.toString());
                    });

            MainActivity.db.collection("users")
                    .document(MainActivity.auth.getUid())
                    .set(data)
                    .addOnSuccessListener(task -> {
                        Toast.makeText(getContext(), "Details saved", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(task -> {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        System.out.println(task.toString());
                    });
        });
        return view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearToday = year;
        monthToday = month;
        dayToday = dayOfMonth;
        dob.setText(String.format("%d/%d/%d", dayToday, monthToday + 1, yearToday));
    }
}