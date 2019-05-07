package com.example.finalproject_blackjack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

public class scoreboard extends AppCompatActivity {
    private static final String TAG = "scoreboard";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<score> scoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        scoreList = MainActivity.databaseHandler.getAllContacts();

        recyclerView = findViewById(R.id.RecycleListViewSB);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(scoreList);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void back() {
        finish();
    }

    private void resetDB(){
        //TODO
    }

    public void sortBy(String sort){
        scoreList = MainActivity.databaseHandler.getAllContacts(sort);
        this.mAdapter.notifyDataSetChanged();

        RecyclerView.Adapter mAdapter = new MyAdapter(scoreList);
        recyclerView.setAdapter(mAdapter);
    }

    public void back(View view) {
        back();
    }

    public void resetDB(View view){
        resetDB();
    }

    public void sortByScore(View view) {
        Log.d(TAG, "sortByScore: ");
        sortBy(DatabaseHandler.KEY_SCORE);
    }

    public void sortByWins(View view) {
        Log.d(TAG, "sortByWins: ");
        sortBy(DatabaseHandler.KEY_WINS);
    }

    public void sortByWinStreak(View view) {
        Log.d(TAG, "sortByWinStreak: ");
        sortBy(DatabaseHandler.KEY_WINSTREAK);
    }

    public void sortByLosses(View view) {
        Log.d(TAG, "sortByLosses: ");
        sortBy(DatabaseHandler.KEY_LOSSES);
    }
}
