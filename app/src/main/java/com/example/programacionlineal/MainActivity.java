package com.example.programacionlineal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button metodoGraficoBtn;
    Button metodoSimplexBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        metodoGraficoBtn = findViewById(R.id.metodoGraficoBtn);
        metodoSimplexBtn = findViewById(R.id.metodoSimplexBtn);

    }

    public void onClick(View view){
        Intent i = null;

        switch (view.getId()){
            case R.id.metodoSimplexBtn:
                i = new Intent(this, ConstruirFuncionSimplexActivity.class);
                break;
            case R.id.metodoGraficoBtn:
                i = new Intent(this, ConstruirFuncionMetodoGraficoActivity.class);
                break;
            default:
                break;
        }
        startActivity(i);
    }
}