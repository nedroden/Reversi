/*
 * Enjun
 *
 * @version     1.0 Beta 1
 * @author      Rocking Stars
 * @copyright   2018, Enjun
 *
 * Copyright 2018 RockingStars

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rockingstar.modules.Reversi.models;

import com.rockingstar.engine.game.Player;
import com.rockingstar.modules.Reversi.views.ReversiView;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class contains algorithmic methods, used for determining things like if there is a winner,
 * checking if a move is valid and clearing the board.
 * @author Rocking Stars
 * @since 1.0 Beta 1
 */
public class ReversiModel {

    /**
     * A ghost player, which makes sure we can have possible moves
     */
    private Player _ghost;

    /**
     * The view
     */
    private ReversiView _view;

    /**
     * ??
     */
    private int counter1;

    /**
     * The board, containing 64 cells
     */
    private Player[][] _board = new Player[8][8];

    /**
     * Possible directions in which tiles can be moved
     */
    private static final int DIRECTIONS[][] = {
            {0, 1}, {1, 1}, {1, 0}, {1, -1},
            {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}
    };

    /**
     * ReversiModel constructor
     * @param view An instance of ReversiView
     */
    public ReversiModel(ReversiView view) {
        _view = view;
        _ghost = new Player("PossibleMoves", null, 'p');
    }

    /**
     * Occupies two tiles for each player
     * @param player1 The local player
     * @param player2 The external player
     */
    public void setStartingPositions(Player player1, Player player2) {
        Player black = player1.getCharacter() == 'b' ? player1 : player2;
        Player white = player1.getCharacter() == 'w' ? player1 : player2;


        setPlayerAtPosition(white, 3, 3);
        setPlayerAtPosition(black, 3, 4);
        setPlayerAtPosition(black, 4, 3);
        setPlayerAtPosition(white, 4, 4);

        for (int y = 3; y < 5; y++)
            for (int x = 3; x < 5; x++)
                _view.setCellImage(x, y);

    }

    /**
     * Returns the game board
     * @return The game board
     */
    public Player[][] getBoard() {
        return _board;
    }

    /**
     * Flips all tiles in the parameters
     * @param tilesToFlip A list of tiles to flip
     * @param player The player that should get all of these tiles
     */
    public void flipTiles(LinkedList<Integer> tilesToFlip, Player player) {
        for (Integer tile : tilesToFlip) {
            setPlayerAtPosition(player, tile % 8, tile / 8);
            _view.setCellImage(tile % 8, tile / 8);
        }
    }

    /**
     * Flips tiles, but with a custom board. Images aren't swapped.
     * @param tilesToFlip A list of tiles to flip
     * @param player The player that should get all of these tiles
     * @param board A game board
     */
    public void flipTiles(LinkedList<Integer> tilesToFlip, Player player, Player[][] board) {
        for (Integer tile : tilesToFlip) {
            board[tile % 8][tile / 8] = player;
        }
    }

    /**
     * Returns the flippable tiles for a certain player
     * @param baseX The x position
     * @param baseY The y position
     * @param player The player making the move
     * @return A list of flippable tiles
     */
    public LinkedList<Integer> getFlippableTiles(int baseX, int baseY, Player player){
        return getFlippableTiles(baseX, baseY, player, _board);
    }

    /**
     * Returns the flippable tiles for a certain player
     * @param baseX The x position
     * @param baseY The y position
     * @param player The player making the move
     * @param board The board the changes should be made on
     * @return A list of flippable tiles
     */
    public LinkedList<Integer> getFlippableTiles(int baseX, int baseY, Player player, Player[][] board) {
        //clearPossibleMoves();
        LinkedList<Integer> tilesToFlip = new LinkedList<>();

        char currentPlayer = player.getCharacter();
        char opponent = currentPlayer == 'b' ? 'w' : 'b';

        if (!moveIsOnBoard(baseX,baseY) || board[baseX][baseY] != null) {
            System.out.println("Move is not on board, or the cell is already filled: " + (board[baseX][baseY] == null));
            return tilesToFlip;
        }

        for (int[] direction : DIRECTIONS) {
            int x = baseX;
            int y = baseY;

            x += direction[0];
            y += direction[1];

            if (moveIsOnBoard(x, y) && board[x][y] != null && board[x][y].getCharacter() == opponent) {
                //localTilesToFlip.add(y * 8 + x); // add first neighbour opponent
                x += direction[0];
                y += direction[1]; // one step deeper

                if (!moveIsOnBoard(x, y)) {
                    continue;
                }
                while (board[x][y] != null && board[x][y].getCharacter() == opponent) {
                    //localTilesToFlip.add(y * 8 + x);
                    x += direction[0];
                    y += direction[1];
                    if (!moveIsOnBoard(x, y)) {
                        break;
                    }
                }
                if (!moveIsOnBoard(x, y)) {
                    continue;
                }

                if(board[x][y] != null && board[x][y].getCharacter() == currentPlayer){
                    while(true){
                        x -= direction[0];
                        y -= direction[1];
                        if (x == baseX && y == baseY) {
                            break;
                        }
                        tilesToFlip.add(y * 8 + x);
                    }
                }
            }
        }

        return tilesToFlip;
    }

    /**
     * Checks whether or not a move is valid
     * @param x The x position
     * @param y The y position
     * @param player The player
     * @return Whether or not the move is valid
     */
    public boolean isValidMove(int x, int y, Player player) {
        return getFlippableTiles(x, y, player).size() > 0;
    }

    /**
     * Checks whether or not a move is valid (with a custom board)
     * @param x The x position
     * @param y The y position
     * @param player The player making the move
     * @param board The board to check on
     * @return Whether or not the move is valid
     */
    private boolean isValidMove(int x, int y, Player player, Player[][] board){
        return getFlippableTiles(x, y, player, board).size() > 0;
    }

    /**
     * Checks whether or not the coordinates are inside the boundaries of the board
     * @param x The x position
     * @param y The y position
     * @return Whether or not the move is inside the boundaries of the board
     */
    private boolean moveIsOnBoard(int x, int y){
        return x < _board.length && y < _board.length && x >= 0 && y >= 0;
    }

    /**
     * Returns a list of possible moves for the player
     * @param player The player to make a move
     * @return A list of possible moves
     */
    public ArrayList<Integer> getPossibleMoves(Player player) {
        clearPossibleMoves();
        ArrayList<Integer> possibleMoves = new ArrayList<>();
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board.length; j++) {
                if (_board[i][j] == null) {
                    if (isValidMove(i, j, player)) {
                        possibleMoves.add(j * 8 + i);
                        setPlayerAtPosition(_ghost, i, j);
                        _view.setCellImage(i, j);
                        //System.out.printf("Move from player %s\n", player.getUsername());
                    }
                }
            }
        }

        return possibleMoves;
    }

    /**
     * Returns a list of possible moves for the player
     * @param player The player to make a move
     * @param board The board to check
     * @return A list of possible moves
     */
    public ArrayList<Integer> getPossibleMoves(Player player, Player[][] board){
        ArrayList<Integer> possibleMoves = new ArrayList<>();

        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                if(board[i][j] == null) {
                    if (isValidMove(i, j, player, board)) {
                        possibleMoves.add(j * 8 + i);
                    }
                }
            }
        }

        return possibleMoves;
    }

    /**
     * Gets rid of the ghost objects on the board
     */
    public void clearPossibleMoves() {
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board.length; j++) {
                if (_board[i][j] == _ghost) {
                    //System.out.println(j * 8 + i);
                    _board[i][j] = null;
                    _view.setCellImage(i, j);
                }
            }
        }
    }

    /**
     * Adds a player at position (x, y)
     * @param player The player
     * @param x The x position
     * @param y The y position
     */
    public void setPlayerAtPosition(Player player, int x, int y) {
        _board[x][y] = player;
    }

    /**
     * Sets the value of all cells to null
     */
    public void createCells() {
        for (int i = 0; i < _board.length; i++)
            for (int j = 0; j < _board[i].length; j++)
                _board[i][j] = null;
    }

    /**
     * Returns the score for players 1 and 2
     * @return The score for players 1 and 2
     */
    public int[] getScore() {
        int[] scores = new int[2];
        scores[0] = 0;
        scores[1] = 0;
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board[i].length; j++) {
                if (_board[i][j] != null) {
                    if (_board[i][j].getCharacter() == 'b') {
                        scores[0]++;
                    } else if (_board[i][j].getCharacter() == 'w') {
                        scores[1]++;
                    }
                }
            }
        }
        return scores;
    }

    /**
     * Clones the board by value.
     * @param boardToCopy The board that should be copied
     * @return A clone of the specified board
     */
    public Player[][] cloneBoard(Player[][] boardToCopy){
        Player[][] clone = new Player[boardToCopy.length][boardToCopy.length];

        for (int i = 0; i < clone.length; i++) {
            for (int j = 0; j < clone.length; j++) {
                clone[i][j] = boardToCopy[i][j];
            }
        }
        return clone;
    }
}