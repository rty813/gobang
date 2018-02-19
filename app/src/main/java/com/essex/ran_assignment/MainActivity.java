package com.essex.ran_assignment;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = findViewById(R.id.imageView);
        final Chess_Panel chessPanel = findViewById(R.id.chessboard);
        chessPanel.setOnTouchListener(new Chess_Panel.onTouchListener() {
            @Override
            public void onTouch(boolean isWhite) {
                imageView.setImageResource(isWhite? R.drawable.white : R.drawable.black);
            }
        });
        btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStart){
                    btnStart.setText("开始游戏");
                    chessPanel.restart();
                    chessPanel.setGameStart(false);
                    imageView.setImageResource(R.drawable.white);
                    imageView.setVisibility(View.INVISIBLE);
                }
                else{
                    btnStart.setText("停止游戏");
                    chessPanel.setGameStart(true);
                    imageView.setVisibility(View.VISIBLE);
                }
                isStart = !isStart;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isStart", isStart);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (isStart = savedInstanceState.getBoolean("isStart")){
            btnStart.setText("停止游戏");
        }
        else{
            btnStart.setText("开始游戏");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_about:
                new AlertDialog.Builder(this)
                        .setTitle("关于")
                        .setMessage("这是xxx的课程设计")
                        .setPositiveButton("确定", null)
                        .show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
