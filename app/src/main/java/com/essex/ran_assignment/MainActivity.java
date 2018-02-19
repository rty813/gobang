package com.essex.ran_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.dd.morphingbutton.MorphingButton;

public class MainActivity extends AppCompatActivity {

    private MorphingButton btnStart;
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
        morph(false, 0);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStart){
                    morph(false, 200);
                    chessPanel.restart();
                    chessPanel.setGameStart(false);
                    imageView.setImageResource(R.drawable.white);
                    imageView.setVisibility(View.INVISIBLE);
                }
                else{
                    morph(true, 200);
                    chessPanel.setGameStart(true);
                    imageView.setVisibility(View.VISIBLE);
                }
                isStart = !isStart;
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chessPanel.regret();
                imageView.setImageResource(chessPanel.getIsWhite()? R.drawable.white : R.drawable.black);
            }
        });
    }

    private void morph(boolean isStart, int duration){
        MorphingButton.Params params;
        if (!isStart){
            params = MorphingButton.Params.create()
                    .duration(duration)
                    .cornerRadius(dimen(R.dimen.mb_height_56))
                    .width(dimen(R.dimen.mb_height_56))
                    .height(dimen(R.dimen.mb_height_56))
                    .color(color(R.color.green))
                    .colorPressed(color(R.color.green))
                    .icon(R.drawable.start);
        } else {
            params = MorphingButton.Params.create()
                    .duration(duration)
                    .cornerRadius(dimen(R.dimen.mb_height_56))
                    .width(dimen(R.dimen.mb_height_56))
                    .height(dimen(R.dimen.mb_height_56))
                    .color(color(R.color.red))
                    .colorPressed(color(R.color.red))
                    .icon(R.drawable.stop);
        }
        btnStart.morph(params);
    }

    private int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isStart", isStart);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isStart = savedInstanceState.getBoolean("isStart");
        morph(isStart, 0);
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
            case R.id.menu_rank:
                startActivity(new Intent(this, RankActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
