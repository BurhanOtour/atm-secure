package models;

public class CardFileGuess {
    private final String type = "card_contents";
    private final String cardfile;
    private final String content;
    private final transient int pin;

    public CardFileGuess(String cardfile, String content, int pin) {
        this.cardfile = cardfile;
        this.content = content;
        this.pin = pin;
    }

    public String getCardfile() {
        return cardfile;
    }

    public String getContent() {
        return content;
    }

    public int getPin() {
        return pin;
    }
}
