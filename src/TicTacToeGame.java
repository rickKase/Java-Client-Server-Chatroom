public class TicTacToeGame {

    private static final char PLAYERX = 'X';     // Helper constant for X player
    private static final char PLAYERO = 'O';     // Helper constant for O player
    private static final char SPACE = ' ';       // Helper constant for spaces
    private static String[][] board = new String[3][3];

    /*
    Sample TicTacToe Board
      0 | 1 | 2
     -----------
      3 | 4 | 5
     -----------
      6 | 7 | 8
     */
    public void newBoard(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                board[i][j] = " ";
            }
        }
    }

    public void updateBoard(int pos, String move){
        switch (pos) {
            case 0:
                board[0][0] = move;
                break;
            case 1:
                board[0][1] = move;
                break;
            case 2:
                board[0][2] = move;
                break;
            case 3:
                board[1][0] = move;
                break;
            case 4:
                board[1][1] = move;
                break;
            case 5:
                board[1][2] = move;
                break;
            case 6:
                board[2][0] = move;
                break;
            case 7:
                board[2][1] = move;
                break;
            case 8:
                board[2][2] = move;
                break;

        }
    }
    public void printBoard(){
        System.out.print(board[0][0]+"|"+board[0][1]+"|"+board[0][2]);
        System.out.print("___________");
        System.out.print(board[1][0]+"|"+board[1][1]+"|"+board[1][2]);
        System.out.print("___________");
        System.out.print(board[2][0]+"|"+board[2][1]+"|"+board[2][2]);

    }
    // TODO 4: Implement necessary methods to manage the games of Tic Tac Toe

}