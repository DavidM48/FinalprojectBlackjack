package com.example.finalproject_blackjack;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<score> scoreList;

    public MyAdapter(List<score> scoreList) {
        this.scoreList = scoreList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.scoreboard_list_row, viewGroup, false);
        return new MyViewHolder(viewItem);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txtScore.setText(Integer.toString(scoreList.get(i).getScore()));
        myViewHolder.txtWins.setText(Integer.toString(scoreList.get(i).getWins()));
        myViewHolder.txtWinStreak.setText(Integer.toString(scoreList.get(i).getWinStreak()));
        myViewHolder.txtLosses.setText(Integer.toString(scoreList.get(i).getLosses()));
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtScore;
        TextView txtWins;
        TextView txtWinStreak;
        TextView txtLosses;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtScore = itemView.findViewById(R.id.txtScoreSB);
            this.txtWins = itemView.findViewById(R.id.txtWinsSB);
            this.txtWinStreak = itemView.findViewById(R.id.txtWinStreakSB);
            this.txtLosses = itemView.findViewById(R.id.txtLossesSB);
        }
    }
}
