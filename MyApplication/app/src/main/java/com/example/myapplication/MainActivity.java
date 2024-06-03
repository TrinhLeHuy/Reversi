package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int EMPTY = 0;
    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;
    private static final int BOARD_SIZE = 8;

    private int[][] gameBoard = new int[BOARD_SIZE][BOARD_SIZE];
    private int currentPlayer = PLAYER_ONE;
    private ImageView[][] boardImageViews = new ImageView[BOARD_SIZE][BOARD_SIZE];

    private String playerOneName;
    private String playerTwoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        playerOneName = intent.getStringExtra("playerOneName");
        playerTwoName = intent.getStringExtra("playerTwoName");

        initGameBoard();
    }

    private void initGameBoard() {
        gameBoard[3][3] = PLAYER_ONE;
        gameBoard[3][4] = PLAYER_TWO;
        gameBoard[4][3] = PLAYER_TWO;
        gameBoard[4][4] = PLAYER_ONE;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                String imageViewId = "image" + ((i * BOARD_SIZE) + j + 1);
                int resID = getResources().getIdentifier(imageViewId, "id", getPackageName());
                boardImageViews[i][j] = findViewById(resID);
                boardImageViews[i][j].setTag((i * BOARD_SIZE) + j);
                boardImageViews[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSquareClicked(v);
                    }
                });
            }
        }

        updateBoardUI();
    }

    public void onSquareClicked(View v) {
        ImageView square = (ImageView) v;
        int row = (int) square.getTag() / BOARD_SIZE;
        int col = (int) square.getTag() % BOARD_SIZE;

        if (isValidMove(row, col)) {
            gameBoard[row][col] = currentPlayer;
            flipPieces(row, col);
            currentPlayer = (currentPlayer == PLAYER_ONE) ? PLAYER_TWO : PLAYER_ONE;
            updateBoardUI();

            if (!hasValidMove()) {
                showGameOverDialog(getGameOverMessage());
            }
        } else {
            Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidMove(int row, int col) {
        if (gameBoard[row][col] != EMPTY) {
            return false;
        }

        int opponent = (currentPlayer == PLAYER_ONE) ? PLAYER_TWO : PLAYER_ONE;

        return checkDirection(row, col, -1, 0, opponent) ||
                checkDirection(row, col, 1, 0, opponent) ||
                checkDirection(row, col, 0, -1, opponent) ||
                checkDirection(row, col, 0, 1, opponent) ||
                checkDirection(row, col, -1, -1, opponent) ||
                checkDirection(row, col, -1, 1, opponent) ||
                checkDirection(row, col, 1, -1, opponent) ||
                checkDirection(row, col, 1, 1, opponent);
    }

    private boolean checkDirection(int row, int col, int rowDelta, int colDelta, int opponent) {
        int r = row + rowDelta;
        int c = col + colDelta;
        boolean foundOpponent = false;

        while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE) {
            if (gameBoard[r][c] == opponent) {
                foundOpponent = true;
            } else if (gameBoard[r][c] == currentPlayer) {
                return foundOpponent;
            } else {
                break;
            }
            r += rowDelta;
            c += colDelta;
        }

        return false;
    }

    private void flipPieces(int row, int col) {
        int opponent = (currentPlayer == PLAYER_ONE) ? PLAYER_TWO : PLAYER_ONE;

        flipDirection(row, col, -1, 0, opponent);
        flipDirection(row, col, 1, 0, opponent);
        flipDirection(row, col, 0, -1, opponent);
        flipDirection(row, col, 0, 1, opponent);
        flipDirection(row, col, -1, -1, opponent);
        flipDirection(row, col, -1, 1, opponent);
        flipDirection(row, col, 1, -1, opponent);
        flipDirection(row, col, 1, 1, opponent);
    }

    private void flipDirection(int row, int col, int rowDelta, int colDelta, int opponent) {
        int r = row + rowDelta;
        int c = col + colDelta;
        boolean foundOpponent = false;

        while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE) {
            if (gameBoard[r][c] == opponent) {
                foundOpponent = true;
            } else if (gameBoard[r][c] == currentPlayer) {
                if (foundOpponent) {
                    int rr = row + rowDelta;
                    int cc = col + colDelta;
                    while (rr != r || cc != c) {
                        gameBoard[rr][cc] = currentPlayer;
                        rr += rowDelta;
                        cc += colDelta;
                    }
                }
                break;
            } else {
                break;
            }
            r += rowDelta;
            c += colDelta;
        }
    }

    private boolean hasValidMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (gameBoard[i][j] == EMPTY && isValidMove(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateBoardUI() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (gameBoard[i][j] == PLAYER_ONE) {
                    boardImageViews[i][j].setImageResource(R.drawable.blackstart);
                } else if (gameBoard[i][j] == PLAYER_TWO) {
                    boardImageViews[i][j].setImageResource(R.drawable.whitestart);
                } else {
                    boardImageViews[i][j].setImageResource(R.drawable.banco);
                }
            }
        }

        // Show hints for valid moves
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (gameBoard[i][j] == EMPTY && isValidMove(i, j)) {
                    boardImageViews[i][j].setImageResource(R.drawable.bantrong);
                }
            }
        }
    }
    public void startGameAgain() {
        // Reset the game board
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameBoard[i][j] = EMPTY;
            }
        }
        // Reinitialize the initial pieces
        gameBoard[3][3] = PLAYER_ONE;
        gameBoard[3][4] = PLAYER_TWO;
        gameBoard[4][3] = PLAYER_TWO;
        gameBoard[4][4] = PLAYER_ONE;

        // Reset current player to PLAYER_ONE
        currentPlayer = PLAYER_ONE;

        // Update the UI
        updateBoardUI();
    }


    private void showGameOverDialog(String message) {
        ResultDialog dialog = new ResultDialog(message);
        dialog.setMainActivity(this); // Pass a reference to MainActivity
        dialog.show(getSupportFragmentManager(), "ResultDialog");
    }


    private String getGameOverMessage() {
        int playerOneCount = countPieces(PLAYER_ONE);
        int playerTwoCount = countPieces(PLAYER_TWO);

        String winner;
        if (playerOneCount > playerTwoCount) {
            winner = playerOneName;
        } else if (playerTwoCount > playerOneCount) {
            winner = playerTwoName;
        } else {
            winner = "It's a draw!";
        }

        return "Results:\n" + winner + " wins!";
    }



    private int countPieces(int player) {
        int count = 0;
        for (int[] row : gameBoard) {
            for (int piece : row) {
                if (piece == player) {
                    count++;
                }
            }
        }
        return count;
    }
}
