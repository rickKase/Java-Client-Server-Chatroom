public class TicTacToeGame {

    private static final char PLAYERX = 'X';     // Helper constant for X player
    private static final char PLAYERO = 'O';     // Helper constant for O player
    private static final char SPACE = ' ';       // Helper constant for spaces
    private static String[][] board;

    /*
    Sample TicTacToe Board
      0 | 1 | 2
     -----------
      3 | 4 | 5
     -----------
      6 | 7 | 8
     */
    public void newBoard(){
        board = new String[3][3];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                board[i][j] = "";
            }
        }
    }

    public boolean updateBoard(int pos, String move){
        switch (pos) {
            case 0:
                if(board[0][0].equals("")) {
                    board[0][0] = move;
                    return true;
                }
                return false;
            case 1:
                if(board[0][1].equals("")) {
                    board[0][1] = move;
                    return true;
                }
                return false;
            case 2:
                if(board[0][2].equals("")) {
                    board[0][2] = move;
                    return true;
                }
                return false;
            case 3:
                if(board[1][0].equals("")) {
                    board[1][0] = move;
                    return true;
                }
                return false;
            case 4:
                if(board[1][1].equals("")) {
                    board[1][1] = move;
                    return true;
                }
                return false;
            case 5:
                if(board[1][2].equals("")) {
                    board[1][2] = move;
                    return true;
                }
                return false;
            case 6:
                if(board[2][0].equals("")) {
                    board[2][0] = move;
                    return true;
                }
                return false;
            case 7:
                if(board[2][1].equals("")) {
                board[2][1] = move;
                return true;
            }
            return false;
            case 8:
                if(board[2][2].equals("")) {
                    board[2][2] = move;
                    return true;
                }
                return false;

        }
        return false;
    }
    public String stringBoard(){
        String current = board[0][0]+"|"+board[0][1]+"|"+board[0][2] + "\n" +
        "___________\n"+ board[1][0]+"|"+board[1][1]+"|"+board[1][2]+"\n"+
        "___________\n"+board[2][0]+"|"+board[2][1]+"|"+board[2][2];
        return current;
    }
    // TODO 4: Implement necessary methods to manage the games of Tic Tac Toe

}