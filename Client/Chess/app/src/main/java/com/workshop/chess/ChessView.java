package com.workshop.chess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/** The View */
public class ChessView extends View {
    private final float scaleFactor = .9f;  //the percentage of the board size from the whole view
    private float originX;  //the cell x coordinate
    private float originY;  //the cell y coordinate
    private float cellSize; //the size of the cell
    private final int lightColor = Color.parseColor("#EEEEEE");
    private final int darkColor = Color.parseColor("#BBBBBB");
    private final Set<Integer> imgResIDs = new HashSet<>(); //a set of the images of the pieces
    {
        imgResIDs.add(R.drawable.bishop_black);
        imgResIDs.add(R.drawable.bishop_white);
        imgResIDs.add(R.drawable.king_black);
        imgResIDs.add(R.drawable.king_white);
        imgResIDs.add(R.drawable.queen_black);
        imgResIDs.add(R.drawable.queen_white);
        imgResIDs.add(R.drawable.rook_black);
        imgResIDs.add(R.drawable.rook_white);
        imgResIDs.add(R.drawable.knight_black);
        imgResIDs.add(R.drawable.knight_white);
        imgResIDs.add(R.drawable.pawn_black);
        imgResIDs.add(R.drawable.pawn_white);
    }
    private final HashMap<Integer, Bitmap> bitmaps = new HashMap<>();   //a hashmap of all the bitmaps of the pieces images
    private final Paint paint = new Paint();

    //info about the moving piece
    private Bitmap movingPieceBitmap = null;
    private ChessPiece movingPiece = null;
    private int fromCol = -1;
    private int fromRow = -1;
    private float movingPieceX = -1f;
    private float movingPieceY = -1f;

    public ChessService chessService;

    {
        loadBitmaps();
    }

    public ChessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int smaller = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(smaller, smaller);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        float boardSize = Math.min(getWidth(), getHeight()) * scaleFactor;
        cellSize = boardSize / 8;
        originX = (getWidth() - boardSize) / 2;
        originY = (getHeight() - boardSize) / 2;

        drawChessBoard(canvas);
        drawPieces(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) return false;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //getting the exact coordinates of the square the was touched
                fromCol = (int)((event.getX() - originX) / cellSize);
                fromRow = 7 - (int)((event.getY() - originY) / cellSize);

                ChessPiece piece = chessService.pieceAt(fromCol, fromRow); //getting the piece if there is one in the square (if a piece was selected)
                movingPiece = piece;
                assert piece != null;
                movingPieceBitmap = bitmaps.get(piece.getResID());  //getting the bitmap of the moving piece
                break;
            case MotionEvent.ACTION_MOVE:
                movingPieceX = event.getX();
                movingPieceY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //getting the exact coordinates of the square the was selected to land on
                int col = (int)((event.getX() - originX) / cellSize);
                int row = 7 - (int)((event.getY() - originY) / cellSize);
                chessService.movePiece(fromCol, fromRow, col, row);
                movingPiece = null;
                movingPieceBitmap = null;
                invalidate();
                break;
        }
        return true;
    }

    /** draws the pieces on the board*/
    private void drawPieces(Canvas canvas) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = chessService.pieceAt(col, row); //getting the piece according its the coordinates
                if (piece != null)
                    drawPieceAt(canvas, col, row, piece.getResID());
            }
        }
        if (movingPieceBitmap != null)  //if its a piece that was chosen to be moved, drew the piece at the new location
            canvas.drawBitmap(movingPieceBitmap, null, new RectF(movingPieceX - cellSize / 2, movingPieceY - cellSize / 2, movingPieceX + cellSize / 2, movingPieceY + cellSize / 2), paint);
    }

    /** draws a bitmap (piece) at the given coordinates */
    private void drawPieceAt(Canvas canvas, int col, int row, int resID){
        Bitmap bitmap = bitmaps.get(resID);
        canvas.drawBitmap(bitmap, null, new RectF(originX + col * cellSize,originY + (7 - row) * cellSize,originX + (col + 1) * cellSize,originY + ((7 - row) + 1) * cellSize), paint);
    }

    /** stores all the bitmaps of the pieces */
    private void loadBitmaps(){
        imgResIDs.forEach((it) -> {
            bitmaps.put(it, BitmapFactory.decodeResource(getResources(), it));
        });
    }

    /** draws the chess board */
    private void drawChessBoard(Canvas canvas){
        for(int row=0; row<8; row++) {
            for (int col=0; col<8; col++){
                drawSquareAt(canvas, col, row, (col + row) % 2 == 0);
            }
        }
    }

    /** draws a square int the given coordinates and color */
    private  void drawSquareAt( Canvas canvas, int col,  int row, boolean isDark){
        if (isDark) paint.setColor(lightColor);
        else paint.setColor(darkColor);
        canvas.drawRect(originX + col * cellSize, originY + row * cellSize, originX + (col + 1) * cellSize, originY + (row + 1) * cellSize, paint);
    }
}
