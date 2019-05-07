package com.example.finalproject_blackjack;

import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Blackjack_Table extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private static final String TAG = "Blackjack_Table";

    private GestureDetector gestureDetector;

    final static String format = "%S %d";

    private static boolean won;
    private static boolean lost;
    private static boolean betLock;

    private static int wins;
    private static int winStreak;
    private static int highestWinStreak;
    private static int loses;
    private static int score;
    private static int bet;

    final static String[] betAmounts = {"0", "10", "20", "30"};
    private Card[] deck = new Card[52];

    private Stack<Card> dealersDeck;
    private Display display;
    private Point displaySize = new Point();

    private ArrayList<Card> dealersHand = new ArrayList<>();
    private ArrayList<Card> playersHand = new ArrayList<>();

    private Button buttonHit;
    private Button buttonStand;
    private Button buttonRestart;
    private Button buttonBet;

    private TextView gameState;
    private TextView gameStateDesc;
    private TextView textWins;
    private TextView textWinStreak;
    private TextView textLosses;
    private TextView textScore;
    private TextView textBet;

    private GridLayout gridDealer;
    private GridLayout gridPlayer;
    private GridLayout gridAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackjack__table);
        setFullScreen();

        won = false;
        lost = false;
        betLock = false;

        wins = 0;
        winStreak = 0;
        highestWinStreak = 0;
        loses = 0;
        score = 100;
        bet = 0;

        display = getWindowManager().getDefaultDisplay();
        display.getSize(displaySize);

        gestureDetector = new GestureDetector(this, this);
        gestureDetector.setOnDoubleTapListener(this);

        buttonHit = findViewById(R.id.btnHit);
        buttonStand = findViewById(R.id.btnStand);
        buttonRestart = findViewById(R.id.btnRestart);
        buttonBet = findViewById(R.id.btnBet);

        gameState = findViewById(R.id.gameState);
        gameStateDesc = findViewById(R.id.gameStateDesc);
        textWins = findViewById(R.id.wins);
        textWinStreak = findViewById(R.id.winStreak);
        textLosses = findViewById(R.id.losses);
        textScore = findViewById(R.id.score);
        textBet = findViewById(R.id.bet);

        gridDealer = findViewById(R.id.gridDealersHand);
        gridPlayer = findViewById(R.id.gridPlayersHand);
        gridAnimation = findViewById(R.id.gridAnimation);

        gridDealer.setColumnCount(7);
        gridDealer.setRowCount(1);

        gridPlayer.setColumnCount(7);
        gridPlayer.setRowCount(1);

        gridAnimation.setColumnCount(7);
        gridAnimation.setRowCount(2);

        for (int i = 0; i < 52; i += 13)
            for (int j = 1; j <= 13; j++)
                deck[i + j - 1] = new Card(i < 13 ? "Clubs" : i < 26 ? "Diamonds" : i < 39 ? "Hearts" : "Spades", j);

        textWins.setText(String.format(format, getString(R.string.wins), wins));
        textWinStreak.setText(String.format(format, getString(R.string.winStreak), winStreak));
        textLosses.setText(String.format(format, getString(R.string.losses), loses));
        textBet.setText(String.format(format, getString(R.string.bet), bet));
        textScore.setText(String.format(format, getString(R.string.score), score));

        populateDealersDeck();
        startRound();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setFullScreen() {
        View overlay = findViewById(R.id.blackJackTable);
        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void startRound() {
        if (dealersHand.size() != 0 && playersHand.size() != 0)
            return;

        addCardToHand(dealersDeck.pop(), false, true);
        addCardToHand(dealersDeck.pop(), false, false);
        //Log.d(TAG, "startRound: dealers hand\n" + printHand(dealersHand) + "Value high ace: " + sumHand(dealersHand, true) + "\nValue low ace: " + sumHand(dealersHand, false));

        addCardToHand(dealersDeck.pop(), true, false);
        addCardToHand(dealersDeck.pop(), true, false);
        //Log.d(TAG, "startRound: players hand\n" + printHand(playersHand) + "Value high ace: " + sumHand(playersHand, true) + "\nValue low ace: " + sumHand(playersHand, false));

        if (checkFor21(dealersHand))
            gameStateDesc.setText(R.string.dealerBlackJack);
        if (checkForBust(dealersHand))
            gameStateDesc.setText(R.string.bustDealer);

        if (checkFor21(playersHand))
            gameStateDesc.setText(R.string.playerBlackJack);
        if (checkForBust(playersHand))
            gameStateDesc.setText(R.string.bustPlayer);

        if (checkFor21(dealersHand) || checkForBust(playersHand))
            loose();
        else if (checkForBust(dealersHand) || checkFor21(playersHand))
            win();
    }

    private void populateDealersDeck() {
        dealersDeck = new Stack<>();
        shuffle();
        for (int i = 0; i < deck.length; i++)
            dealersDeck.push(deck[i]);
    }

    private void shuffle() {
        Random random = new Random();
        for (int i = deck.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Card temp = deck[index];
            deck[index] = deck[i];
            deck[i] = temp;
        }
    }

    private String printDeck() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deck.length; i++)
            sb.append(deck[i].toString()).append("\n");
        return sb.toString();
    }

    private String printHand(ArrayList<Card> hand) {
        if (hand.size() == 0)
            return "Empty";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hand.size(); i++)
            sb.append(hand.get(i).toString()).append("\n");
        return sb.toString();
    }

    private void displayCard(Card card, boolean player, boolean hidden) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View imgView;

        if (hidden && !player)
            imgView = layoutInflater.inflate(R.layout.card_display_item_backwards, gridPlayer, false);
        else if (!hidden && !player)
            imgView = layoutInflater.inflate(R.layout.card_display_item_no_click_flip, gridPlayer, false);
        else
            imgView = layoutInflater.inflate(R.layout.card_display_item, gridPlayer, false);

        //this line is useful if we want to edit easyflipView before it gets put into the grid
        //EasyFlipView easyFlipView = imgView.findViewById(R.id.EFV);

        ImageView image = imgView.findViewById(R.id.imgCard);
        image.setImageDrawable(getCardImage(card));


        if (player)
            gridPlayer.addView(imgView);
        else
            gridDealer.addView(imgView);
//        animateLastCard(player);
    }

    private void animateLastCard(boolean player) {
        EasyFlipView easyFlipView;
        ImageView imageView;
        Point point;

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View imgView = layoutInflater.inflate(R.layout.card_display_item_front, gridAnimation, false);
        ImageView image = imgView.findViewById(R.id.imgCard);

        if (player) {
            if (gridPlayer.getChildCount() < 1)
                return;
            easyFlipView = gridPlayer.getChildAt(gridPlayer.getChildCount() - 1).findViewById(R.id.EFV);
            point = getCardPoint(true, gridPlayer.getChildCount());
        } else {
            if (gridDealer.getChildCount() < 1)
                return;
            easyFlipView = gridDealer.getChildAt(gridDealer.getChildCount() - 1).findViewById(R.id.EFV);
            point = getCardPoint(false, gridDealer.getChildCount());
        }

        Animation translateAnimation = new TranslateAnimation(displaySize.x / 2.0f, point.x, -105, point.y);
        translateAnimation.setDuration(5000);

        if (easyFlipView.isFrontSide())
            imageView = easyFlipView.findViewById(R.id.imgCard);
        else
            imageView = easyFlipView.findViewById(R.id.imgCardBack);

        image.setImageDrawable(imageView.getDrawable());
//        image.setVisibility(View.INVISIBLE);
//        image.setAnimation(translateAnimation);
        image.setX(point.x);
        image.setY(point.y);

        gridAnimation.addView(imgView);
//        gridAnimation.getChildAt(gridAnimation.getChildCount() - 1).animate();
    }

    private void flipAllCards() {
        EasyFlipView easyFlipView;
        for (int i = 0; i < gridDealer.getChildCount(); i++) {
            easyFlipView = gridDealer.getChildAt(i).findViewById(R.id.EFV);
            easyFlipView.flipTheView();
        }

        for (int i = 0; i < gridPlayer.getChildCount(); i++) {
            easyFlipView = gridPlayer.getChildAt(i).findViewById(R.id.EFV);
            easyFlipView.flipTheView();
        }
    }

    private Point getCardPoint(boolean player, int index) {
        Point point = new Point();
        point.x = index * 25;
        if (player) {
            point.y = (int) gridPlayer.getY() + 22;
        } else {
            point.y = (int) gridDealer.getY() + 22;
        }
        return point;
    }

    private Drawable getCardImage(Card card) {
        Drawable drawable;
        if (card.getFace() == 1) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.ace_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.ace_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.ace_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.ace_spades);
        } else if (card.getFace() == 2) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.two_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.two_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.two_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.two_spades);
        } else if (card.getFace() == 3) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.three_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.three_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.three_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.three_spades);
        } else if (card.getFace() == 4) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.four_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.four_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.four_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.four_spades);
        } else if (card.getFace() == 5) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.five_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.five_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.five_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.five_spades);
        } else if (card.getFace() == 6) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.six_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.six_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.six_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.six_spades);
        } else if (card.getFace() == 7) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.seven_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.seven_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.seven_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.seven_spades);
        } else if (card.getFace() == 8) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.eight_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.eight_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.eight_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.eight_spades);
        } else if (card.getFace() == 9) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.nine_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.nine_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.nine_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.nine_spades);
        } else if (card.getFace() == 10) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.ten_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.ten_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.ten_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.ten_spades);
        } else if (card.getFace() == 11) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.jack_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.jack_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.jack_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.jack_spades);
        } else if (card.getFace() == 12) {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.queen_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.queen_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.queen_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.queen_spades);
        } else {
            if (card.getSuite().equalsIgnoreCase("Clubs"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.king_clubs);
            else if (card.getSuite().equalsIgnoreCase("Diamonds"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.king_diamonds);
            else if (card.getSuite().equalsIgnoreCase("Hearts"))
                drawable = AppCompatResources.getDrawable(this, R.drawable.king_hearts);
            else
                drawable = AppCompatResources.getDrawable(this, R.drawable.king_spades);
        }
        return drawable;
    }

    private void addCardToHand(Card card, boolean player, boolean hidden) {
        if (player)
            playersHand.add(card);
        else
            dealersHand.add(card);
        displayCard(card, player, hidden);
    }

    private int sumHand(ArrayList<Card> list) {
        int sum = 0;
        int aceCount = 0;

        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getFace() == 1)
                aceCount++;
            else
                sum += list.get(i).getValue();

        if (aceCount > 0)
            for (int i = 1; i <= aceCount; i++)
                if (sum + 11 <= 21)
                    sum += 11;
                else
                    sum++;

        return sum;
    }

    private boolean aceInHand(ArrayList<Card> list) {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getFace() == 1)
                return true;
        return false;
    }

    private boolean checkFor21(ArrayList<Card> list) {
        if (list.size() == 0)
            return false;

        return sumHand(list) == 21;
    }

    private boolean checkForBust(ArrayList<Card> list) {
        if (list.size() == 0)
            return false;

        return sumHand(list) > 21;
    }

    //win should be when players wins and dealer looses
    private void win() {
        gameState.setText(R.string.winState);
        won = true;
        wins++;
        winStreak++;
        if (winStreak > highestWinStreak)
            highestWinStreak = winStreak;
        finishRound();
    }

    //Loose should be when dealer wins and player looses
    private void loose() {
        gameState.setText(R.string.looseState);
        lost = true;
        loses++;
        winStreak = 0;
        finishRound();
    }

    private void tie() {
        gameState.setText(R.string.tieState);
        lost = true;
        won = true;
        finishRound();
    }

    private void finishRound() {
        Animation animationHalf = AnimationUtils.loadAnimation(this, R.anim.fade_out_half);
        buttonHit.setBackground(getDrawable(R.drawable.button_fade));
        buttonStand.setBackground(getDrawable(R.drawable.button_fade));

        buttonHit.startAnimation(animationHalf);
        buttonStand.startAnimation(animationHalf);


        buttonHit.setAlpha(0.5f);
        buttonStand.setAlpha(0.5f);

        if (betLock) {
            buttonBet.setBackground(getDrawable(R.drawable.button_fade));
            buttonBet.startAnimation(animationHalf);
            buttonBet.setAlpha(0.5f);
        }

        Animation animationFull = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        buttonRestart.startAnimation(animationFull);
        buttonRestart.setVisibility(View.VISIBLE);


        textWins.setText(String.format(format, getString(R.string.wins), wins));
        textWinStreak.setText(String.format(format, getString(R.string.winStreak), winStreak));
        textLosses.setText(String.format(format, getString(R.string.losses), loses));

        EasyFlipView easyFlipView = gridDealer.getChildAt(0).findViewById(R.id.CDIRL).findViewById(R.id.EFV);
        easyFlipView.flipTheView();

        if (won)
            score += bet * 2;
        else
            score -= bet;
        textScore.setText(String.format(format, getString(R.string.score), score));
    }

    private void finishGame() {
        if (wins > 0 || loses > 0 || score > 100)
            MainActivity.databaseHandler.addScore(new score(score, wins, highestWinStreak, loses));

        finish();
    }

    private void bet() {
        if (betLock) {
            Toast.makeText(this, "Betting is locked, bet can not be changed this round.", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select how much you want to bet.");
        builder.setItems(betAmounts, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                bet = Integer.parseInt(betAmounts[whichButton]);
                textBet.setText(String.format(format, getString(R.string.bet), bet));
                setFullScreen();
            }
        });
        builder.show();
    }

    private void hit() {
        if (won || lost)
            return;

        if (!betLock) {
            betLock = true;

            Animation animationHalf = AnimationUtils.loadAnimation(this, R.anim.fade_out_half);
            buttonBet.setBackground(getDrawable(R.drawable.button_fade));
            buttonBet.startAnimation(animationHalf);
            buttonBet.setAlpha(0.5f);
        }

        if (dealersDeck.empty())
            populateDealersDeck();

        addCardToHand(dealersDeck.pop(), true, false);
        //Log.d(TAG, "hit: players hand \n" + printHand(playersHand) + "Value: " + sumHand(playersHand));

        if (checkForBust(playersHand)) {
            gameStateDesc.setText(R.string.bustPlayer);
            loose();
        }
    }

    private void stand() {
        if (won || lost)
            return;

        betLock = true;

        while (sumHand(dealersHand) < 17) {
            addCardToHand(dealersDeck.pop(), false, false);
        }

        if (checkForBust(dealersHand)) {
            gameStateDesc.setText(R.string.bustDealer);
            win();
        }

        if (!won && !lost)
            if (sumHand(playersHand) > sumHand(dealersHand)) {
                gameStateDesc.setText(R.string.playerHigher);
                win();
            } else if (sumHand(playersHand) == sumHand(dealersHand)) {
                tie();
            } else {
                gameStateDesc.setText(R.string.dealerHigher);
                loose();
            }
    }

    private void restart() {
        if (!won && !lost)
            return;

        if (score < 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Game over")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).show();
            finishGame();
        }

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        buttonRestart.startAnimation(animation);
        buttonRestart.setVisibility(View.INVISIBLE);

        gameState.setText("");
        gameStateDesc.setText("");

        Animation animationHalf = AnimationUtils.loadAnimation(this, R.anim.fade_in_half);
        buttonHit.setBackground(getDrawable(R.drawable.buttons));
        buttonStand.setBackground(getDrawable(R.drawable.buttons));
        buttonBet.setBackground(getDrawable(R.drawable.buttons));

        buttonHit.startAnimation(animationHalf);
        buttonStand.startAnimation(animationHalf);
        buttonBet.startAnimation(animationHalf);

        buttonHit.setAlpha(1.0f);
        buttonStand.setAlpha(1.0f);
        buttonBet.setAlpha(1.0f);

        playersHand = new ArrayList<>();
        dealersHand = new ArrayList<>();

        gridDealer.removeAllViewsInLayout();
        gridPlayer.removeAllViewsInLayout();
        gridAnimation.removeAllViewsInLayout();

        won = false;
        lost = false;
        betLock = false;

        populateDealersDeck();
        startRound();
    }

    private void exit() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to quit")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finishGame();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        setFullScreen();
                    }
                }).show();

    }

    //Methods for Buttons
    public void bet(View view) {
        bet();
    }

    public void hit(View view) {
        hit();
    }

    public void stand(View view) {
        stand();
    }

    public void restart(View view) {
        restart();
    }

    public void exitButton(View view) {
        exit();
    }

    public void flip(View view) {
        ((EasyFlipView) view).flipTheView();
    }

    //Gesture methods
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
//        Log.d(TAG, "onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
//        Log.d(TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        restart();
//        Log.d(TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        stand();
//        Log.d(TAG, "onScroll: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
//        Log.d(TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
//        Log.d(TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        hit();
//        Log.d(TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
//        Log.d(TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
//        Log.d(TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }
}