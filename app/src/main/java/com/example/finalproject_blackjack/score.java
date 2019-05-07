package com.example.finalproject_blackjack;

public class score {
    private int score;
    private int wins;
    private int winStreak;
    private int losses;

    public score(int score, int wins, int winStreak, int losses) {
        this.score = score;
        this.wins = wins;
        this.winStreak = winStreak;
        this.losses = losses;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
}
