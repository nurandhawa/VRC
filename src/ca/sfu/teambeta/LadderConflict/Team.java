package ca.sfu.teambeta;

class Team {
    private String player1;
    private String player2;

    public Team(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public String toString() {
        return "Player 1 " + player1 + " Player 2: " + player2;
    }
}