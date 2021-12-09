package com.aariyan.memo_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aariyan.memo_app.Activities.CollaborationActivity;
import com.aariyan.memo_app.Activities.CustomerNameActivity;
import com.aariyan.memo_app.Activities.MemoHome;
import com.aariyan.memo_app.Activities.StockSheetActivity;

public class MainActivity extends AppCompatActivity {

    private CardView viewStockCard,createMemoCard,collaborationCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI() {
        viewStockCard = findViewById(R.id.viewStockCard);
        createMemoCard = findViewById(R.id.createMemoCard);
        collaborationCard = findViewById(R.id.collaborationPanelCard);

        viewStockCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StockSheetActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        createMemoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this, MemoHome.class));
                startActivity(new Intent(MainActivity.this, CustomerNameActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        collaborationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CollaborationActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}