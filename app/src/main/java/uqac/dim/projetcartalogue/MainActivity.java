package uqac.dim.projetcartalogue;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    Button btnCapture, btnCopy;
    TextView txtScannedData;
    private static final int REQUEST_CAMERA_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapture = findViewById(R.id.CaptureBtn);
        btnCopy = findViewById(R.id.CopyTextBtn);
        txtScannedData = findViewById(R.id.scannedData);

        if(ContextCompat.checkSelfPermission(MainActivity.this,"android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.CAMERA"},REQUEST_CAMERA_CODE);
        }

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getPhoto = new Intent(Intent.ACTION_GET_CONTENT);
                getPhoto.setType("image/*");
                startActivityForResult(getPhoto,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            Bitmap imgBitmap = null;
            try {
                imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}