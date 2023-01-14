package com.ahmad.skindiseasesdetecter;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmad.skindiseasesdetecter.ml.Detecter;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final float confidenceLevel = 0.975f;
    private int imageSize = 224; //default image size
    private String fileName=""; // File name
    private String phoneNumber =""; // Doctor Phone number

    TextView adv, disease, details, medicine, contact;
    ImageView photo;
    Button take, upload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Conect objects to views
        setContentView(R.layout.activity_main);
        adv = findViewById(R.id.mainAdv);
        disease = findViewById(R.id.mainDiseaseClass);
        details = findViewById(R.id.mainDetails);
        medicine = findViewById(R.id.mainMedicine);
        contact = findViewById(R.id.mainContactDoctor);
        photo = findViewById(R.id.photo);
        take = findViewById(R.id.mainTakeButton);
        upload = findViewById(R.id.mainUploadButton);

        adv.setText("Take a photo or Upload from the gallery");
        adv.setVisibility(View.VISIBLE);
        disease.setVisibility(View.GONE);
        details.setVisibility(View.GONE);
        medicine.setVisibility(View.GONE);
        contact.setVisibility(View.GONE);

        upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(cameraIntent, 2);
                } else {
                    // request camera permission if we don't have
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);

                }

            }
        });

        take.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // Lanuch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    // request camera permission if we don't have
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);

                }

            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileName != "Unknown") {
                    Intent detailsIntent = new Intent(getApplicationContext(), detailsActivity.class);
                    detailsIntent.putExtra("fileName", fileName);
                    detailsIntent.putExtra("class", "main");
                    startActivity(detailsIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Sorry, No disease is detected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileName != "Unknown") {
                    Intent medicineIntent = new Intent(getApplicationContext(), medicineActivity.class);
                    medicineIntent.putExtra("fileName", fileName);
                    medicineIntent.putExtra("class", "main");
                    startActivity(medicineIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Sorry, No disease is detected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileName != "Unknown") {
                    Intent contactIntent = new Intent(getApplicationContext(), contactActivity.class);
                    contactIntent.putExtra("phoneNumber", phoneNumber);
                    contactIntent.putExtra("class", "main");
                    startActivity(contactIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Sorry, No disease is detected", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap image = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    image = (Bitmap) data.getExtras().get("data");
                    break;
                }
                case 2: {
                    Uri imageUri = data.getData();
                    try {
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            photo.setImageBitmap(image);
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);

            details.setVisibility(View.VISIBLE);
            medicine.setVisibility(View.VISIBLE);
            contact.setVisibility(View.VISIBLE);
            disease.setVisibility(View.VISIBLE);
            adv.setText("Recognized Disese: ");


        }
    }

    private void classifyImage(Bitmap image) {
        try {
            Detecter model = Detecter.newInstance(getApplicationContext());

            // creat input for refernce
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get ID array of 224 * 224 pixels in image
            int[] intValue = new int[imageSize * imageSize];
            image.getPixels(intValue, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, B values then add to the byteBuffer
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValue[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Detecter.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();

            // find the index of the class with the biggest confidence
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidence.length; i++) {
                if (confidence[i] > maxConfidence) {
                    maxConfidence = confidence[i];
                    maxPos = i;
                }
            }
            Log.d("prob: ", String.valueOf(maxConfidence));

            ArrayList<String> classes = readData("labels.txt");

            if (maxConfidence >= confidenceLevel) {
                String className = classes.get(maxPos);
                disease.setText(className);// Check if the prob of result is more than 80%
                fileName = className;
                Log.d("class: ", className);
            } else {
                disease.setText("Unknown");
                fileName = "Unknown";

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    // Extract Classes Names from labels.txt
//    private ArrayList<String> getClasses(String readFileName) throws IOException {
//        ArrayList<String> classesNames = readData(readFileName);
//        return classesNames;
//    }

    // A method for read the data of a txt file
    private ArrayList<String> readData(String readFileName) throws IOException {
        ArrayList<String> myStoredText = new ArrayList<String>();
        InputStream inputStream = getAssets().open(readFileName);
        if(inputStream != null){

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String tempData = "";
            while ( (tempData = bufferedReader.readLine()) != null ){
                myStoredText.add(tempData);
            }
            inputStream.close();
        }

        return  myStoredText;
    }
}