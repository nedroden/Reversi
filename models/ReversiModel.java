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

import javafx.application.Platform;

import java.util.ArrayList;


public class ReversiModel {

    private ReversiView _view;

    private Player[][] _board = new Player[8][8];

    private static final int DIRECTIONS[][] = {
            {0, 1}, {1, 1}, {1, 0}, {1, -1},
            {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}
    };

    public ReversiModel(ReversiView view) {
        _view = view;
    }

    public void setStartingPositions(Player player1, Player player2) {
        Player black = player1.getCharacter() == 'b' ? player1 : player2;
        Player white = player1.getCharacter() == 'w' ? player1 : player2;

        _board[3][3] = white;
        _board[3][4] = black;
        _board[4][3] = black;
        _board[4][4] = white;

        for (int y = 3; y < 5; y++)
            for (int x = 3; x < 5; x++)
                _view.setCellImage(x, y);
    }

    public Player[][] getBoard() {
        return _board;
    }

    public void addEventHandlers() {
        _view.getEndButton().setOnAction(e -> Platform.exit());
        _view.getNewGameButton().setOnAction(e -> clearBoard());
    }

    public boolean isValidMove(int baseX, int baseY, Player player, Player opponent) {
        if (!moveIsOnBoard(baseX,baseY) || _board[baseX][baseY] != null) {
            System.out.println("niet op board, of niet null");
            return false;
        }
        // Note: inspired by: https://inventwithpython.com/chapter15.html
        // houd bij welke tiles geflipt moeten worden
        ArrayList<Integer> tilesToFlip = new ArrayList<>();
        int counter = 0;
        for(int[] direction : DIRECTIONS) {
            counter++;
            System.out.println("Direction count" + counter);

            int x = baseX;
            int y = baseY;
            x += direction[0];
            y += direction[1];

            if (moveIsOnBoard(x, y) && _board[x][y] == opponent) {
                // There is a piece belonging to the other player next to our piece.
                //localTilesToFlip.add(y * 8 + x); // add first neighbour opponent
                x += direction[0];
                y += direction[1]; // one step deeper

                if (!moveIsOnBoard(x, y)) {
                    continue;
                }
                while (_board[y][x] == opponent) {
                    //localTilesToFlip.add(y * 8 + x);
                    x += direction[0];
                    y += direction[1];
                    if (!moveIsOnBoard(x, y)) {
                        //break out of while loop, then continue in for loop
                        break;
                    }
                }
                if (!moveIsOnBoard(x, y)){
                    continue;
                }
                if(_board[x][y] == player){
                    while(true){
                        x -= direction[0];
                        y -= direction[1];
                        if(x == baseX && y == baseY){
                            break;
                        }
                        tilesToFlip.add(y * 8 + x);
                    }
                }
            }
        }
        System.out.println("35 Type: " + _board[3][4].getUsername());
        System.out.println("36 Type: " + _board[4][4].getUsername());
        System.out.println("tiles to flip: " + tilesToFlip);

        if(tilesToFlip.size() == 0){
            System.out.println("hij print false hier");
            return false;
        }
        flipTiles(tilesToFlip,player);
        return true;
    }

    public void flipTiles(ArrayList<Integer> tilesToFlip, Player player){
        for (Integer tile : tilesToFlip) {
            setPlayerAtPosition(player, tile%8,tile/8);
            _view.setCellImage(tile % 8, tile / 8);
        }
    }


    // nog niet zeker of dit goed is....
    /*public ArrayList<int[]> getValidMoves(int x, int y, Player player){
        ArrayList<int[]> validMoves = new ArrayList<>();
        for(int i = 0; i < _board.length -1; i++){
            for(int j = 0; j < _board.length -1; j++){
                if(isValidMove(x,y,player, player) != false){
                    validMoves.add([x][y];
                }
            }
        }
    }*/


                    /*
            int x = baseX;
            int y = baseY;
            x += direction[0];
            y += direction[1];

            if(moveIsOnBoard(x,y) && _board[x][y] == opponent){
                // There is a piece belonging to the other player next to our piece.
                //tilesToFlip.add(y * 8 + x); // add first neighbour opponent
                x += direction[0];
                y += direction[1]; // one step deeper

                if(!moveIsOnBoard(x,y)){
                    continue;
                }
                while(_board[x][y] == opponent) {
                    System.out.println("doe je deze wel?");
                    //tilesToFlip.add(y * 8 + x); // add second neighbour opponent
                    x += direction[0];  // one step deeper
                    y += direction[1];
                    if (!moveIsOnBoard(x, y)) {
                        break;
                    }
                }
                if (!moveIsOnBoard(x, y)) {
                    continue;
                }

                if(_board[x][y] == _board[baseX][baseY]){
                    while(true){
                        System.out.println("komt hier wel --------------------");
                        x -= direction[0];
                        y -= direction[1];
                        if(x == baseX && y == baseY){
                            break;
                        }
                        tilesToFlip.add(y * 8 + x);
                */

                    //System.out.println("yo eind gevonden bij direction" + direction[0] + "," + direction[1]);

/*
                System.out.printf("Direction: %d %d:\n", direction[0], direction[1]);
                for (int integer : localTilesToFlip) {
                    System.out.println(integer);
                }

                System.out.println();

                if (_board[y][x] == player) {
                    tilesToFlip.addAll(localTilesToFlip);
                    System.out.printf("Found an end node for move (%d, %d)\n", baseX, baseY);
                }
                else if (_board[y][x] == null && baseX == 5 && baseY == 4) {
                    System.out.printf("Something went quite wrong here: (%d, %d) field %d\n", x, y, y * 8 + x);
                }
            }

            else if (baseX == 5 && baseY == 4) {
                System.out.printf("Not the opponent: X: %d, Y: %d, field: %d\n", x, y, y * 8 + x);

                if (_board[y][x] == player)
                    System.out.println("It's the player");
            }
        }

        if (tilesToFlip.size() == 0) {
            System.out.printf("Fuck. Can't find tiles for (%d, %d) field %d\n", baseX, baseY, baseY * 8 + baseX);
            return false;
        }

        for (Integer tile : tilesToFlip) {
            _board[tile % 8][tile / 8] = player;
            _view.setCellImage(tile % 8, tile / 8);
        }

        return true;
    }*/

    /*
    private boolean canMoveInDirection(int baseX, int baseY, int dirX, int dirY, Player player,Player opponent) {
        int posX = baseX + dirX;
        int posY = baseY + dirY;

        while (moveIsOnBoard(posX, posY)) {
            posX += dirX;
            posY += dirY;
            if (_board[posY][posX] == player)
                return true;
            else if (_board[posY][posX] != player && _board[posY][posX] != null)
                break;
        }

        return false;
    }*/

    public boolean moveIsOnBoard(int x, int y){
        if (x < _board.length - 1 && y < _board.length - 1 && x > 0 && y > 0){
            return true;
        }
        return false;
    }

    /*public void switchTiles(int x, int y, Player player) {
        LinkedList<Integer> tilesToFlip = new LinkedList<>();

        for (int[] direction : DIRECTIONS) {
            int posX = x;
            int posY = y;

            if (!canMoveInDirection(x, y, direction[0], direction[1], player))
                continue;

            while (posX < _board.length - 1 && posY < _board.length - 1 && posX > 0 && posY > 0) {
                posX += direction[0];
                posY += direction[1];

                if (_board[posY][posX] != player && _board[posY][posX] != null)
                    tilesToFlip.add(posY * 8 + posX);

                else if (_board[posY][posX] == player || _board[posY][posX] == null)
                    break;
            }
        }

        for (Integer tile : tilesToFlip) {
            // waarschijnlijk wel goed
            _board[tile % 8][tile / 8] = player;
            _view.setCellImage(tile % 8, tile / 8);

        }
    }*/

    public void setPlayerAtPosition(Player player, int x, int y) {
        _board[x][y] = player;
    }

    private void clearBoard() {
        _board = new Player[8][8];
        createCells();

        _view.setBoard(_board);
        _view.generateBoardVisual();
        _view.setIsFinished(false);
    }

    public void createCells() {
        for (int i = 0; i < _board.length; i++)
            for (int j = 0; j < _board[i].length; j++)
                _board[i][j] = null;
    }

    public boolean hasWon(Player player) {
        return false;
    }

    public boolean isFull() {
        for (int i = 0; i < _board.length; i++)
            for (int j = 0; j < _board[i].length; j++)
                if (_board[i][j] == null)
                    return false;

        return true;
    }

    public int getScoreForPlayer(Player player) {
        int score = 0;

        for (int i = 0; i < _board.length; i++)
            for (int j = 0; j < _board[i].length; j++)
                if (_board[i][j] != null && _board[i][j] == player)
                    score++;

        return score;
    }

    public String getTurnMessage(Player player) {
        return player.getUsername() + ", it's your turn";
    }
}
