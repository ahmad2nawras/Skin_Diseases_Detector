package com.ahmad.skindiseasesdetecter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class detailsActivity extends AppCompatActivity {

    private TextView title;
    private TextView className;
    private TextView detailsText;
    private Button back, medicine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalis);

        Bundle bundle = getIntent().getExtras();
        String fileName="d" + bundle.getString("fileName")  + ".txt";
        Log.d("name", fileName);

        title = findViewById(R.id.medicationTilte);
        className = findViewById(R.id.medicationClassName);
        detailsText = findViewById(R.id.medicationText);
        back = findViewById(R.id.medicationBackButon);
        medicine = findViewById(R.id.detailsButton);


        className.setText(bundle.getString("fileName"));
        title.setText("Details");

        try {
            detailsText.setText(readData(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnBtn = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(returnBtn);
            }
        });

        medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), medicineActivity.class);
                intent.putExtra("fileName", bundle.getString("fileName"));
                startActivity(intent);
            }
        });


    }

    private String readData(String readFileName) throws IOException {
        String myStoredText = "";
        InputStream inputStream = getAssets().open(readFileName);
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String tempData = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ( (tempData = bufferedReader.readLine()) != null ){
                stringBuilder.append(tempData+"\n");
            }
            inputStream.close();
            myStoredText = stringBuilder.toString();
        }
        return  myStoredText;
    }
}