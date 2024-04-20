package com.pritamdey.sentimentanalysis;

import static com.pritamdey.sentimentanalysis.App.hideKeyBoard;
import static com.pritamdey.sentimentanalysis.App.loggedInUser;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.pritamdey.sentimentanalysis.databinding.ActivityLogInBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogIn extends AppCompatActivity {
    private ActivityLogInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("sp", SharedPrefsHelper.getUserEmail(this) + " " + SharedPrefsHelper.getUserPassword(this));
        if (SharedPrefsHelper.getUserEmail(this) != null && SharedPrefsHelper.getUserPassword(this) != null){
            RetrofitClient.getClient().userLogin(new User(SharedPrefsHelper.getUserEmail(this), SharedPrefsHelper.getUserPassword(this))).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        loggedInUser = response.body();
                        startActivity(new Intent(LogIn.this, MainActivity.class));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable throwable) {
                    Log.d("logIn", throwable.getMessage());
                }
            });

        }

        binding.signIn.setOnClickListener(v -> {
            onLogIn();
        });

        binding.signUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUp.class));
            finish();
        });

    }

    private void onLogIn() {
        hideKeyBoard(this, binding.email);
        hideKeyBoard(this, binding.password);
        String email, password;

        email = binding.email.getText().toString();
        password = binding.password.getText().toString();

        if (!Validation.isValidEmail(email)) {
            Toast.makeText(this, "Please provide valid email address", Toast.LENGTH_SHORT).show();
        } else if (!Validation.isValidPassword(password)) {
            Toast.makeText(this, "Password cannot be empty or less than 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            User user = new User(email, password);

            RetrofitClient.getClient().userLogin(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        loggedInUser = response.body();
                        SharedPrefsHelper.setUserEmail(LogIn.this, email);
                        SharedPrefsHelper.setUserPassword(LogIn.this, password);
                        startActivity(new Intent(LogIn.this, MainActivity.class));
                        Toast.makeText(LogIn.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                        Log.d("signIn", new Gson().toJson(response.body()));
                        finish();
                    }


                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d("signIn", t.getMessage());
                }
            });
        }
    }


}