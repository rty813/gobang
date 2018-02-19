package com.essex.ran_assignment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Chess_Panel extends View {
    //画笔
    private Paint paint;
    //画布的宽度
    private int PanelWidth;
    //最大行数
    private static final int MAX_LINE = 15;
    //设置棋子的大小为棋盘格子的3/4
    private static final float Size = 3 * 1.0f / 4;
    //格子的高度(必须是float)
    private float SingelHeight;
    //黑白棋的素材
    private Bitmap WhiteBitmap;
    private Bitmap BlackBitmap;
    //判断谁先出棋（一般白先手）
    private boolean IsWhite = true;
    //存放黑白棋子坐标
    private ArrayList<Point> WhitePoint = new ArrayList<>();
    private ArrayList<Point> BlackPoint = new ArrayList<>();

    private boolean IsGameOver = false;

    //五子连珠算赢
    private static final int FIVE_POINT = 5;

    private onTouchListener listener = null;
    private boolean IsGameStart;

    public Chess_Panel(Context context) {
        this(context, null);
    }

    public Chess_Panel(Context context, AttributeSet attributeSet) {            //构造函数
        super(context, attributeSet);
        inital();
    }

    private void inital() {
        paint = new Paint();
        paint.setColor(0x88000000);
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);//防抖动
        paint.setStyle(Paint.Style.FILL);
        WhiteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white);
        BlackBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);
        //AT_MOST:specSize 代表的是最大可获得的空间；
        //EXACTLY:specSize 代表的是精确的尺寸；
        //UNSPECIFIED:对于控件尺寸来说，没有任何参考意义。
        //解决嵌套在ScrollView中时等情况出现的问题
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        Log.i("onMeasure", String.valueOf(width));

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.i("onSizeChanged", String.format("%d, %d, %d, %d", w, h, oldw, oldh));
        PanelWidth = Math.min(w, h);
        SingelHeight = PanelWidth * 1.0f / MAX_LINE;
        int onlyWidth = (int) (SingelHeight * Size);
        WhiteBitmap = Bitmap.createScaledBitmap(WhiteBitmap, onlyWidth, onlyWidth, false);
        BlackBitmap = Bitmap.createScaledBitmap(BlackBitmap, onlyWidth, onlyWidth, false);
    }
    protected void onDraw(Canvas canvas) {
        Log.i("onDraw", String.format("%f, %d", SingelHeight, PanelWidth));
        super.onDraw(canvas);
        DrawBoard(canvas);
        DrawPiece(canvas);
        IsGameOver();
    }

    //绘制棋盘
    //因为棋子的中心是在棋盘的点上的，所以上下左右有个边距，一般设为1/2
    private void DrawBoard(Canvas canvas) {
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) SingelHeight / 2;
            int endX = (int) (PanelWidth - SingelHeight / 2);
            int y = (int) ((0.5 + i) * SingelHeight);
            canvas.drawLine(startX, y, endX, y, paint);//画横线
            canvas.drawLine(y, startX, y, endX, paint);//画竖线
        }
    }

    private void DrawPiece(Canvas canvas) {
        for (int i = 0, n = WhitePoint.size(); i < n; i++) {
            Point whitePoint = WhitePoint.get(i);
            canvas.drawBitmap(WhiteBitmap,
                    (whitePoint.x + (1 - Size) / 2) * SingelHeight,
                    (whitePoint.y + (1 - Size) / 2) * SingelHeight, null);
        }
        for (int i = 0, n = BlackPoint.size(); i < n; i++) {
            Point blackPoint = BlackPoint.get(i);
            canvas.drawBitmap(BlackBitmap,
                    (blackPoint.x + (1 - Size) / 2) * SingelHeight,
                    (blackPoint.y + (1 - Size) / 2) * SingelHeight, null);
        }
    }

    private void IsGameOver() {
        boolean WhiteWin = checkFiveInLine(WhitePoint);
        boolean BlackWin = checkFiveInLine(BlackPoint);
        boolean NoWin = checkNoWin(WhiteWin, BlackWin);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setPositiveButton("重来", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        restart();
                    }
                })
                .setNeutralButton("查看棋盘！", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)getContext()).finish();
                    }
                });
        if (WhiteWin) {
            builder.setTitle("白棋获胜！");
        } else if (BlackWin) {
            builder.setTitle("黑棋获胜");
        } else if (NoWin) {
            builder.setTitle("针锋相对，和棋了！");
        } else {
            return;
        }
        IsGameOver = true;
        builder.show();
    }

    public boolean getIsWhite(){
        return IsWhite;
    }

    public boolean GetGameResult() {
        return IsGameOver;
    }

    public int GetPieceSize() {
        if (WhitePoint.size() == 0 && BlackPoint.size() == 0) {
            return 0;
        }
        return 1;
    }

    private boolean checkNoWin(boolean whiteWin, boolean blackWin) {
        if (whiteWin || blackWin) {
            return false;
        }
        int max = MAX_LINE * MAX_LINE;
        //如果白棋和黑棋的总数等于棋盘格子数,说明和棋
        if (WhitePoint.size() + BlackPoint.size() == max) {
            return true;
        }
        return false;
    }

    //重新开始
    public void restart() {
        WhitePoint.clear();
        BlackPoint.clear();
        IsGameOver = false;
        IsWhite=true;
        invalidate();
    }

    //悔棋
    public void regret() {
        if (IsGameOver || !IsGameStart){
            return;
        }
        if (BlackPoint.size() > 0 || WhitePoint.size() > 0) {
            if (IsWhite) {
                BlackPoint.remove(BlackPoint.size() - 1);
                IsWhite = !IsWhite;
            } else {
                WhitePoint.remove(WhitePoint.size() - 1);
                IsWhite = !IsWhite;
            }
            invalidate();
        }
    }

    private boolean checkFiveInLine(List<Point> point) {
        for (Point p : point) {
            int x = p.x;
            int y = p.y;

            boolean checkHorizontal = checkHorizontalFiveInLine(x, y, point);
            boolean checkVertical = checkVerticalFiveInLine(x, y, point);
            boolean checkLeftDiagonal = checkLeftFiveInLine(x, y, point);
            boolean checkRightDiagonal = checkRightFiveInLine(x, y, point);
            if (checkHorizontal || checkVertical || checkLeftDiagonal || checkRightDiagonal) {
                return true;
            }
        }

        return false;
    }

    //横向五子连珠
    private boolean checkHorizontalFiveInLine(int x, int y, List<Point> point) {
        int count = 1;
        for (int i = 1; i < FIVE_POINT; i++) {
            if (point.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == FIVE_POINT) {
            return true;
        }
        return false;
    }

    //竖向五子连珠
    private boolean checkVerticalFiveInLine(int x, int y, List<Point> point) {
        int count = 1;
        for (int i = 1; i < FIVE_POINT; i++) {
            if (point.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == FIVE_POINT) {
            return true;
        }
        return false;
    }

    //左斜五子连珠
    private boolean checkLeftFiveInLine(int x, int y, List<Point> point) {
        int count = 1;
        for (int i = 1; i < FIVE_POINT; i++) {
            if (point.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == FIVE_POINT) {
            return true;
        }
        return false;
    }

    //右斜五子连珠
    private boolean checkRightFiveInLine(int x, int y, List<Point> point) {
        int count = 1;
        for (int i = 1; i < FIVE_POINT; i++) {
            if (point.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == FIVE_POINT) {
            return true;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (IsGameOver || !IsGameStart) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x, y);
            if (WhitePoint.contains(p) || BlackPoint.contains(p)) {
                return false;
            }

            if (IsWhite) {
                WhitePoint.add(p);
            } else {
                BlackPoint.add(p);
            }
            invalidate();
            IsWhite = !IsWhite;
            if (listener != null){
                listener.onTouch(IsWhite);
            }
            return true;
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / SingelHeight), (int) (y / SingelHeight));
    }

    public void setGameStart(boolean IsGameStart){
        this.IsGameStart = IsGameStart;
        this.IsGameOver = false;
    }

    public interface onTouchListener{
        public void onTouch(boolean isWhite);
    }

    public void setOnTouchListener(onTouchListener listener){
        this.listener = listener;
    }
}