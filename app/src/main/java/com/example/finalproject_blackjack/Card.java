package com.example.finalproject_blackjack;

public class Card {
    private String suite;
    private int face;
    private int value;

    public Card(String suite, int face) {
        this.suite = suite;
        this.face = face;
        if (face > 10)
            this.value = 10;
        else
            this.value = face;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String name = "";
        if (face == 1)
            name += "Ace of ";
        else if (face == 2)
            name += "Two of ";
        else if (face == 3)
            name += "Three of ";
        else if (face == 4)
            name += "Four of ";
        else if (face == 5)
            name += "Five of ";
        else if (face == 6)
            name += "Six of ";
        else if (face == 7)
            name += "Seven of ";
        else if (face == 8)
            name += "Eight of ";
        else if (face == 9)
            name += "Nine of ";
        else if (face == 10)
            name += "Ten of ";
        else if (face == 11)
            name += "Jack of ";
        else if (face == 12)
            name += "Queen of ";
        else
            name += "King of ";
        name += suite;
        return name;
    }
}
