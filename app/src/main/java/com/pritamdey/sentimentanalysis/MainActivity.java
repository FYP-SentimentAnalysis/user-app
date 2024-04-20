package com.pritamdey.sentimentanalysis;

import static com.pritamdey.sentimentanalysis.App.baseURL;
import static com.pritamdey.sentimentanalysis.App.hideKeyBoard;
import static com.pritamdey.sentimentanalysis.App.loggedInUser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pritamdey.sentimentanalysis.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private RecyclerView commentRecycler;
    private List<Review> commentsList;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.userName.setText(loggedInUser.getName());

        binding.logOut.setOnClickListener(v -> {
            loggedInUser = null;
            SharedPrefsHelper.setUserEmail(this, null);
            SharedPrefsHelper.setUserPassword(this, null);
            startActivity(new Intent(MainActivity.this, LogIn.class));
            finish();
        });

        commentRecycler = binding.commentRecycler;
        commentRecycler.setHasFixedSize(true);
        commentRecycler.setLayoutManager(new LinearLayoutManager(this));

        commentsList = new ArrayList<>();

        adapter = new CommentAdapter(MainActivity.this, commentsList);
        commentRecycler.setAdapter(adapter);


        RetrofitClient.getClient().userReviewsList(loggedInUser.get_id()).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        commentsList.clear();
                        commentsList.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.d("error", Objects.requireNonNull(t.getMessage()));

            }
        });


        binding.submit.setOnClickListener(v -> {
            onSubmit();
        });


        Socket socket = IO.socket(URI.create(baseURL));
        binding.comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0) socket.emit("predict", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        socket.on("prediction", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject jsonObject = new JSONObject(args[0].toString());
                    int score = (int) (jsonObject.getDouble("score") * 100);
                    String label = jsonObject.getString("label");

                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.VISIBLE);
                        switch (label) {
                            case "positive":
                                binding.score.setText("Positive");
                                binding.score.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.green));
                                binding.progressBar.setProgress(score);
                                binding.progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.green), PorterDuff.Mode.SRC_IN);
                                break;
                            case "negative":
                                binding.progressBar.setProgress(100 - score);
                                binding.progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red), PorterDuff.Mode.SRC_IN);
                                binding.score.setText("Negative");
                                binding.score.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red));
                                break;
                            default:
                                binding.score.setText("Neutral");
                                binding.score.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
                                binding.progressBar.setProgress(50);
                                binding.progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.yellow), PorterDuff.Mode.SRC_IN);
                                break;
                        }
                    });


                    Log.d("prasun bokachoda", score + " " + label);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        });

        socket.connect();
    }

    private void onSubmit() {
        hideKeyBoard(this, binding.service);
        hideKeyBoard(this, binding.comment);
        String userId, service, comment;
        userId = loggedInUser.get_id();
        service = binding.service.getText().toString();
        comment = binding.comment.getText().toString();
        Log.d("review", "onSubmit: " + new Gson().toJson(new Review(userId, service, comment)));
        RetrofitClient.getClient().newReview(new Review(userId, service, comment)).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                Log.d("response", "response.body()");
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Submitted Successfully", Toast.LENGTH_SHORT).show();
                    commentsList.add(0, response.body());
                    adapter.notifyItemInserted(0);
                    binding.service.setText("");
                    binding.comment.setText("");
                    binding.score.setText("");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.commentRecycler.smoothScrollToPosition(0);

                } else Log.d("error", RetrofitClient.parseError(response.errorBody()));
            }

            @Override
            public void onFailure(Call<Review> call, Throwable throwable) {
                throwable.printStackTrace();
                Log.d("submit", throwable.getMessage());
            }
        });

    }
}