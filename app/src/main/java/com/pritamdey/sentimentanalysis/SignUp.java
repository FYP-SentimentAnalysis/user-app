package com.pritamdey.sentimentanalysis;

import static com.pritamdey.sentimentanalysis.App.hideKeyBoard;
import static com.pritamdey.sentimentanalysis.App.loggedInUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.pritamdey.sentimentanalysis.databinding.ActivitySignUpBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.signUp.setOnClickListener(v -> {
            onSignUp();
        });



        binding.signIn.setOnClickListener(v -> {
            startActivity(new Intent(this, LogIn.class));
            finish();
        });
    }

    private void onSignUp() {
        hideKeyBoard(this, binding.name);
        hideKeyBoard(this, binding.email);
        hideKeyBoard(this, binding.password);

        String name, email, password;

        name = binding.name.getText().toString();
        email = binding.email.getText().toString();
        password = binding.password.getText().toString();

        if (!Validation.isValidName(name)){
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (!Validation.isValidEmail(email)){
            Toast.makeText(this, "Please provide valid email address", Toast.LENGTH_SHORT).show();
        } else if (!Validation.isValidPassword(password)) {
            Toast.makeText(this, "Password cannot be empty or less than 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            User user = new User(name, email, password);

            RetrofitClient.getClient().userSignup(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        loggedInUser = response.body();
                        SharedPrefsHelper.setUserEmail(SignUp.this, email);
                        SharedPrefsHelper.setUserEmail(SignUp.this, password);
                        startActivity(new Intent(SignUp.this, MainActivity.class));
                        Toast.makeText(SignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        Log.d("signUp", new Gson().toJson(response.body()));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(SignUp.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    Log.d("signUp", t.getMessage());
                }
            });
        }

    }
}