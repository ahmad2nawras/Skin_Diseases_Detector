package com.ahmad.skindiseasesdetecter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class medicineActivity extends AppCompatActivity {
    private TextView title;
    private TextView className;
    private TextView detailsText;
    private Button back, details;
    private Button pharmacies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        Bundle bundle = getIntent().getExtras();
        String fileName="m" + bundle.getString("fileName")  + ".txt";

        title = findViewById(R.id.medicationTilte);
        className = findViewById(R.id.medicationClassName);
        detailsText = findViewById(R.id.medicationText);
        back = findViewById(R.id.medicationBackButon);
        details = findViewById(R.id.detailsButton);
        pharmacies = findViewById(R.id.medicinePharmacies);


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

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailsBtn = new Intent(getApplicationContext(), detailsActivity.class);
                startActivity(detailsBtn);
            }
        });

        pharmacies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapintent=new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.co.uk/maps?q=Pharmacy&hl=en"));

                startActivity(mapintent);
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
            int i =1;
            while ( (tempData = bufferedReader.readLine()) != null ){
                stringBuilder.append(i+". "+tempData+"\n\n");
                i++;
            }
            inputStream.close();
            myStoredText = stringBuilder.toString();
        }
        return  myStoredText;
    }


}