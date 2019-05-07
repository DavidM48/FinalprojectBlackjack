package com.example.finalproject_blackjack;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHandler = new DatabaseHandler(this);
    }

    public void play(View view) {
        Intent intent = new Intent(this, Blackjack_Table.class);
        this.startActivity(intent);
    }

    public void scoreBoard(View view) {
        Intent intent = new Intent(this, scoreboard.class);
        this.startActivity(intent);
    }
}
