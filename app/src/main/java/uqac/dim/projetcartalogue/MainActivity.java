package uqac.dim.projetcartalogue;


import static android.graphics.Color.argb;
import static android.graphics.Color.rgb;
import static android.graphics.Color.valueOf;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.odml.image.BitmapMlImageBuilder;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {


    Button btnCapture,btnCamera, btnCopy;
    TextView txtScannedData, txtType;
    Bitmap imgBitmap;
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int REQUEST_IMAGES_CODE = 110;
    public final double colorMargin = 0.3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on get les views
        btnCapture = findViewById(R.id.CaptureBtn);
        btnCamera = findViewById(R.id.CameraBtn);
        btnCopy = findViewById(R.id.CopyTextBtn);
        txtScannedData = findViewById(R.id.scannedData);
        txtType = findViewById(R.id.typeTxt);

        //permission pour la camera
        if(ContextCompat.checkSelfPermission(MainActivity.this,"android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.CAMERA"},REQUEST_CAMERA_CODE);
        }
        //permissions pour les photos du stockage
        if(ContextCompat.checkSelfPermission(MainActivity.this,"android.permission.READ_MEDIA_IMAGES") != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_MEDIA_IMAGES"},REQUEST_IMAGES_CODE);
        }

        // click pour stockage
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getPhoto = new Intent(MediaStore.ACTION_PICK_IMAGES);
                //getPhoto.setType("image/*");
                startActivityForResult(getPhoto,1);
            }
        });
        //click pour camera
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    Intent getPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(getPhoto,2);
                }catch(Exception e){
                    throw new RuntimeException(e);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(data != null){
                if(requestCode == 1){
                    Uri imageUri = data.getData();

                    try {

                        imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                        ExtractText(imgBitmap);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                if(requestCode == 2){
                    try {

                        Bundle extras = data.getExtras();
                        imgBitmap = (Bitmap) extras.get("data");

                        ExtractText(imgBitmap);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }


        }
    }

    private void ExtractText(Bitmap bitmap){
        int size =bitmap.getWidth()*bitmap.getHeight();
        int[] pixelsMatchingToType = new int[PokemonTypeColors.values().length];
        int[] allPixels = new int[size];
        bitmap.getPixels(allPixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        for (int i = 0; i < size; i+=300) {
            Color color = Color.valueOf(allPixels[i]);
            int index = 0;
            for (PokemonTypeColors type:PokemonTypeColors.values())
            {
                Color typeColor = Color.valueOf(Color.rgb(type.getR(),type.getG(),type.getB()));
                double contrast =ColorUtils.calculateContrast(allPixels[i], rgb(type.getR(),type.getG(),type.getB()));
                float[] HCT1 = new float[3];
                float[] HCT2 = new float[3];
                //ColorUtils.colorToLAB(allPixels[i],lab1);
                ColorUtils.colorToM3HCT(allPixels[i],HCT1);
                ColorUtils.colorToM3HCT(Color.rgb(type.getR(),type.getG(),type.getB()),HCT2);
                //ColorUtils.colorToLAB(Color.rgb(type.getR(),type.getG(),type.getB()),lab2);
                double differenceHUE = Math.abs(HCT1[0]- HCT2[0]);
                double differenceC = Math.abs(HCT1[1] - HCT2[1]);
                double differenceT = Math.abs(HCT1[2] - HCT2[2]);
                //int difference = Math.abs(rgb(type.getR(),type.getG(),type.getB()) - allPixels[i] );

                if(differenceHUE < 5 && differenceT < 10 /*&& differenceC < 10*/){
                //if((color.red() >typeColor.red() -colorMargin && color.red() < typeColor.red() +colorMargin) && (color.green() > typeColor.green() -colorMargin && color.green() < typeColor.green() +colorMargin) && (color.blue() > typeColor.blue() - colorMargin && color.blue() < typeColor.blue() + colorMargin)){
                    pixelsMatchingToType[index]++;
                    break;
                }
                index++;

            }

            //System.out.println(i);
        }
        int highestMatchNb = 0;
        int highestMatchIndex = 0;

        for(int x = 0; x < pixelsMatchingToType.length; x++){
            if(highestMatchNb < pixelsMatchingToType[x]){
                highestMatchNb = pixelsMatchingToType[x];
                highestMatchIndex = x;
            }
        }

        PokemonTypeColors matchingType =PokemonTypeColors.values()[highestMatchIndex];
        txtType.setText(matchingType.name());
        txtScannedData.setBackgroundColor(rgb(matchingType.getR(),matchingType.getG(),matchingType.getB()));
        //txtScannedData.



        TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> task = textRecognizer.process(bitmap,0);
        task.addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                txtScannedData.setText(task.getResult().getText());
            }
        });

        //FrameLayout frame  = new FrameLayout(this);
    }
}