package com.pritamdey.sentimentanalysis;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pritamdey.sentimentanalysis.databinding.PreviousCommentBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private Context context;
    private List<Review> commentsList;

    public CommentAdapter(Context context, List<Review> reviews) {
        this.context = context;
        commentsList = reviews;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentHolder(PreviousCommentBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        Log.d("adapter", "onBindViewHolder: " + commentsList);
        Review review = commentsList.get(position);
        String date;
        try {
            date = new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(review.getCreatedAt().substring(0, 10)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        holder.binding.title.setText(review.getService());
        holder.binding.review.setText(review.getComment());
        holder.binding.date.setText(date);
        switch(review.getLabel()){
            case "positive":
                holder.binding.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.green));
                break;
            case "negative":
                holder.binding.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.red));
                break;
            default:
                holder.binding.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.yellow));
                break;
        }
        holder.binding.cardView.setStrokeWidth(4);
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        final PreviousCommentBinding binding;
        public CommentHolder(@NonNull PreviousCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
