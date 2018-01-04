package com.xl.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText editText1, editText2, editText3, editText4, editText5;
    private Button button;
    private CreditScoreView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.my_view);
        button = (Button) findViewById(R.id.btn);
        editText1 = (EditText) findViewById(R.id.edit1);
        editText2 = (EditText) findViewById(R.id.edit2);
        editText3 = (EditText) findViewById(R.id.edit3);
        editText4 = (EditText) findViewById(R.id.edit4);
        editText5 = (EditText) findViewById(R.id.edit5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float a = Integer.valueOf(editText1.getText().toString());
                float b = Integer.valueOf(editText2.getText().toString());
                float c = Integer.valueOf(editText3.getText().toString());
                float d = Integer.valueOf(editText4.getText().toString());
                float e = Integer.valueOf(editText5.getText().toString());
                view.setData(a,b,c,d,e);
            }
        });
    }
}
