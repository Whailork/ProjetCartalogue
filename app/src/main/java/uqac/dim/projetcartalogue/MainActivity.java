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
import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {


    Button btnCapture,btnCamera, btnCopy;
    TextView txtScannedData, txtType;
    Bitmap imgBitmap;
    ArrayList<Text.TextBlock> inReadingOrder;
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int REQUEST_IMAGES_CODE = 110;
    public final double colorMargin = 0.3;
    //ordre :
    /*
    TOP
     Base / basic
     Niveau/ stage
     mega evolution
     alolan
     evolves from
    * nom
     pv

     MIDDLE
     NO.
     pokemon type
     height
     weight
    * attaques

    BOTTOM
    * weakness
    * resistance
    * description
    * retreat
    * copyright
    * illustration
    * rarity*/

    //TOP
    Pattern basicEn = Pattern.compile("Basic", Pattern.CASE_INSENSITIVE);
    Pattern basicFR = Pattern.compile("Base", Pattern.CASE_INSENSITIVE);

    Pattern stageEN = Pattern.compile("Stage",Pattern.CASE_INSENSITIVE);
    Pattern stageFR = Pattern.compile("niveau", Pattern.CASE_INSENSITIVE);

    Pattern megaEN = Pattern.compile("mega",2);
    Pattern megaFR = Pattern.compile("méga",2);

    Pattern alolanEN = Pattern.compile("Alolan",Pattern.CASE_INSENSITIVE);
    Pattern alolanFR = Pattern.compile("d'alola",2);

    Pattern evolvesFromEN = Pattern.compile("Evolves from",2);
    Pattern evolvesFromFR = Pattern.compile("Évolution de",2);

    Pattern pvEN = Pattern.compile("HP",Pattern.CASE_INSENSITIVE);
    Pattern pvFR = Pattern.compile("PV",2);

    //MIDDLE
    Pattern numberEN = Pattern.compile("^NO\\.?\\s*\\d{3}",Pattern.CASE_INSENSITIVE);
    Pattern numberFR = Pattern.compile("^N°\\s*\\d{3}",Pattern.CASE_INSENSITIVE);

    Pattern pokemonTypeEN = Pattern.compile(".*pokémon",2);
    Pattern pokemonTypeFR = Pattern.compile("pokémon.*",2);

    Pattern heightEN = Pattern.compile("HT : .*",2);
    Pattern heightFR = Pattern.compile("Taille : .* m$",2);

    Pattern weightEN = Pattern.compile("WT : .*",2);
    Pattern weightFR = Pattern.compile("Poids : .* kg$");

    //BOTTOM
    Pattern weaknessEN = Pattern.compile("weakness",2);
    Pattern weaknessFR = Pattern.compile("faiblesse",2);

    Pattern resistanceEN = Pattern.compile("resistance",2);
    Pattern resistanceFR = Pattern.compile("Résistance",2);

    Pattern retreatEN = Pattern.compile("retreat cost",2);
    Pattern retreatFR = Pattern.compile("Retraite",2);



    Pattern noSpecialChar = Pattern.compile("[^\\w\\s]",Pattern.CASE_INSENSITIVE);
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



        TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> task = textRecognizer.process(bitmap,0);
        task.addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                carteModel newCarteModel = new carteModel();

                int middleLeft = 0;
                int bottomLeft = 0;
                boolean evolvesFrom = false;
                txtScannedData.setText(task.getResult().getText());
                List<Text.TextBlock> result = task.getResult().getTextBlocks();
                inReadingOrder = new ArrayList<Text.TextBlock>();
                ArrayList<String> strInReadingOrder = new ArrayList<>();

                for(int x = 0; x < result.size(); x++)
                {
                    if(inReadingOrder.isEmpty()){
                        inReadingOrder.add(result.get(x));
                    }
                    else{
                        for(int y =0;y < inReadingOrder.size();y++)
                        {
                            if(result.get(x).getBoundingBox().left < inReadingOrder.get(y).getBoundingBox().left){
                                inReadingOrder.add(y,result.get(x));
                                break;
                            }

                        }
                        if (!inReadingOrder.contains(result.get(x))){
                            inReadingOrder.add(result.get(x));
                        }
                    }
                }
                for (Text.TextBlock t:inReadingOrder)
                {
                    strInReadingOrder.add(t.getText());
                }
                //TOP--------------------------------------------------------------------------------
                //check pour l'evolution(basic ou stage1/stage2) ou mega
                String evolutionText = "";
                Matcher mBasicFR;
                Matcher mBasicEN;
                for(int i = 0; i < 5; i++){
                    mBasicFR = basicFR.matcher(strInReadingOrder.get(i));
                    mBasicEN = basicEn.matcher(strInReadingOrder.get(i));
                    if(mBasicFR.find()){
                        evolutionText = strInReadingOrder.get(i).substring(mBasicFR.start(),mBasicFR.end());
                        String newString = strInReadingOrder.get(i).replace(evolutionText,"");
                        strInReadingOrder.remove(i);
                        strInReadingOrder.add(i,newString);
                        break;

                    }
                    else{
                        if(mBasicEN.find()){
                            evolutionText = strInReadingOrder.get(i).substring(mBasicEN.start(),mBasicEN.end());
                            String newString = strInReadingOrder.get(i).replace(evolutionText,"");
                            strInReadingOrder.remove(i);
                            strInReadingOrder.add(i,newString);
                            break;
                        }
                        else{
                            // c'est un pokemon deja evolue
                            mBasicFR = stageFR.matcher(strInReadingOrder.get(i));
                            mBasicEN = stageEN.matcher(strInReadingOrder.get(i));
                            if(mBasicFR.find()){
                                evolvesFrom = true;
                                evolutionText = strInReadingOrder.get(i).substring(mBasicFR.start(),mBasicFR.end());
                                String newString = strInReadingOrder.get(i).replace(evolutionText,"");
                                strInReadingOrder.remove(i);
                                strInReadingOrder.add(i,newString);
                                break;
                            }
                            else{
                                if(mBasicEN.find()){
                                    evolvesFrom = true;
                                    evolutionText = strInReadingOrder.get(i).substring(mBasicEN.start(),mBasicEN.end());
                                    String newString = strInReadingOrder.get(i).replace(evolutionText,"");
                                    strInReadingOrder.remove(i);
                                    strInReadingOrder.add(i,newString);
                                    break;
                                }
                                else{
                                    //c'est une mega evolution
                                    mBasicFR = megaFR.matcher(strInReadingOrder.get(i));
                                    mBasicEN = megaEN.matcher(strInReadingOrder.get(i));
                                    if(mBasicFR.find()){
                                        evolvesFrom = true;
                                        evolutionText = strInReadingOrder.get(i).substring(mBasicFR.start(),mBasicFR.end());
                                        String newString = strInReadingOrder.get(i).replace(evolutionText,"");
                                        strInReadingOrder.remove(i);
                                        strInReadingOrder.add(i,newString);
                                        break;
                                    }
                                    else{
                                        if(mBasicEN.find()){
                                            evolvesFrom = true;
                                            evolutionText = strInReadingOrder.get(i).substring(mBasicEN.start(),mBasicEN.end());
                                            String newString = strInReadingOrder.get(i).replace(evolutionText,"");
                                            strInReadingOrder.remove(i);
                                            strInReadingOrder.add(i,newString);
                                            break;
                                        }
                                    }
                                }
                            }

                        }
                    }

                }
                //on clean les textblocks qui sont vides
                strInReadingOrder = removeEmpty(strInReadingOrder);



                //ensuite on check si alolan
                Matcher mAlolan;
                boolean isAlolan = false;
                for(int i = 0; i < 5; i++){
                    mAlolan = alolanFR.matcher(strInReadingOrder.get(i));
                    if(mAlolan.find()){
                        isAlolan = true;
                        String toRemove = strInReadingOrder.get(i).substring(mAlolan.start(),mAlolan.end());
                        String newString = strInReadingOrder.get(i).replace(toRemove,"");
                        strInReadingOrder.remove(i);
                        strInReadingOrder.add(i,newString);
                        break;
                    }
                    else{
                        mAlolan = alolanEN.matcher(strInReadingOrder.get(i));
                        if(mAlolan.find()){
                            isAlolan = true;
                            String toRemove = strInReadingOrder.get(i).substring(mAlolan.start(),mAlolan.end());
                            String newString = strInReadingOrder.get(i).replace(toRemove,"");
                            strInReadingOrder.remove(i);
                            strInReadingOrder.add(i,newString);
                            break;
                        }
                    }

                }
                // on clean les textblocks qui sont vides
                strInReadingOrder = removeEmpty(strInReadingOrder);

                // si c'est pas une base on check evolves from
                String strEvolvesFrom = "";
                if(evolvesFrom){
                    Matcher mEvolvesFrom;
                    for(int i = 0; i < 5; i++){

                        mEvolvesFrom = evolvesFromFR.matcher(strInReadingOrder.get(i));
                        if(mEvolvesFrom.find()){
                            strEvolvesFrom = strInReadingOrder.get(i).substring(mEvolvesFrom.start(),mEvolvesFrom.end());
                            String newString = strInReadingOrder.get(i).replace(strEvolvesFrom,"");
                            strInReadingOrder.remove(i);
                            strInReadingOrder.add(i,newString);
                            break;
                        }
                        else{
                            mEvolvesFrom = evolvesFromEN.matcher(strInReadingOrder.get(i));
                            if(mEvolvesFrom.find()){
                                strEvolvesFrom = strInReadingOrder.get(i).substring(mEvolvesFrom.start(),mEvolvesFrom.end());
                                String newString = strInReadingOrder.get(i).replace(strEvolvesFrom,"");
                                strInReadingOrder.remove(i);
                                strInReadingOrder.add(i,newString);
                                break;
                            }
                        }

                    }
                    // on clean les textBlocks qui sont vides
                    strInReadingOrder = removeEmpty(strInReadingOrder);
                }
                // on get les pv
                Matcher mPv;
                String strPv;
                for(int i = 0; i < 5; i++){

                    mPv = pvFR.matcher(strInReadingOrder.get(i));
                    if(mPv.find()){
                        strPv = strInReadingOrder.get(i).substring(mPv.start(),mPv.end());
                        String newString = strInReadingOrder.get(i).replace(strPv,"");
                        strInReadingOrder.remove(i);
                        strInReadingOrder.add(i,newString);
                        break;
                    }
                    else{
                        mPv = pvEN.matcher(strInReadingOrder.get(i));
                        if(mPv.find()){
                            strPv = strInReadingOrder.get(i).substring(mPv.start(),mPv.end());
                            String newString = strInReadingOrder.get(i).replace(strPv,"");
                            strInReadingOrder.remove(i);
                            strInReadingOrder.add(i,newString);
                            break;
                        }
                    }


                }
                //on clean les textBlocks qui sont vides
                strInReadingOrder = removeEmpty(strInReadingOrder);

                //MIDDLE------------------------------------------------------------------------------------
                // get le numero
                Matcher mNO;
                String strNO = "";
                for(int i = 0; i < 5; i++){
                    mNO = numberFR.matcher(strInReadingOrder.get(i));
                    if(mNO.find()){
                        middleLeft = inReadingOrder.get(i).getBoundingBox().left;
                        strNO = strInReadingOrder.get(i).substring(mNO.start(),mNO.end());
                        String newString = strInReadingOrder.get(i).replace(strNO,"");
                        strInReadingOrder.remove(i);
                        strInReadingOrder.add(i,newString);
                        break;
                    }
                    else{
                        mNO = numberEN.matcher(strInReadingOrder.get(i));
                        if(mNO.find()){
                            middleLeft = inReadingOrder.get(i).getBoundingBox().left;
                            strNO = strInReadingOrder.get(i).substring(mNO.start(),mNO.end());
                            String newString = strInReadingOrder.get(i).replace(strNO,"");
                            strInReadingOrder.remove(i);
                            strInReadingOrder.add(i,newString);
                            break;
                        }
                    }

                }
                //on clean les textBlocks qui sont vides
                strInReadingOrder = removeEmpty(strInReadingOrder);

                // get le pokemon type

                Matcher mPokeType;
                String strPokeType = "";
                for(int i = 0; i < 5; i++){
                    mPokeType = pokemonTypeFR.matcher(strInReadingOrder.get(i));
                    if(mPokeType.find()){
                        strPokeType = strInReadingOrder.get(i).substring(mPokeType.start(),mPokeType.end());
                        String newString = strInReadingOrder.get(i).replace(strPokeType,"");
                        strInReadingOrder.remove(i);
                        strInReadingOrder.add(i,newString);
                        break;
                    }
                    else{
                        mPokeType = pokemonTypeEN.matcher(strInReadingOrder.get(i));
                        if(mPokeType.find()){
                            strPokeType = strInReadingOrder.get(i).substring(mPokeType.start(),mPokeType.end());
                            String newString = strInReadingOrder.get(i).replace(strPokeType,"");
                            strInReadingOrder.remove(i);
                            strInReadingOrder.add(i,newString);
                            break;
                        }
                    }

                }
                //on clean les textBlocks qui sont vides
                strInReadingOrder = removeEmpty(strInReadingOrder);

                // get le height
                Matcher mHeight;
                String strHeight = "";
                for(int i = 0; i < 5; i++){
                    mHeight = heightFR.matcher(strInReadingOrder.get(i));
                    if(mHeight.find()){
                        strHeight = strInReadingOrder.get(i).substring(mHeight.start(),mHeight.end());
                        String newString = strInReadingOrder.get(i).replace(strHeight,"");
                        strInReadingOrder.remove(i);
                        strInReadingOrder.add(i,newString);
                        break;
                    }
                    else{
                        mHeight = heightEN.matcher(strInReadingOrder.get(i));
                        if(mHeight.find()){
                            strHeight = strInReadingOrder.get(i).substring(mHeight.start(),mHeight.end());
                            String newString = strInReadingOrder.get(i).replace(strHeight,"");
                            strInReadingOrder.remove(i);
                            strInReadingOrder.add(i,newString);
                            break;
                        }
                    }

                }
                //on clean les textBlocks qui sont vides
                strInReadingOrder = removeEmpty(strInReadingOrder);

                // get le weigth

                Matcher mWeight;
                String strWeight = "";
                for(int i = 0; i < 5; i++){
                    mWeight = weightFR.matcher(strInReadingOrder.get(i));
                    if(mWeight.find()){
                        strWeight = strInReadingOrder.get(i).substring(mWeight.start(),mWeight.end());
                        String newString = strInReadingOrder.get(i).replace(strWeight,"");
                        strInReadingOrder.remove(i);
                        strInReadingOrder.add(i,newString);
                        break;
                    }
                    else{
                        mWeight = weightEN.matcher(strInReadingOrder.get(i));
                        if(mWeight.find()){
                            strWeight = strInReadingOrder.get(i).substring(mWeight.start(),mWeight.end());
                            String newString = strInReadingOrder.get(i).replace(strWeight,"");
                            strInReadingOrder.remove(i);
                            strInReadingOrder.add(i,newString);
                            break;
                        }
                    }

                }
                //on clean les textBlocks qui sont vides
                strInReadingOrder = removeEmpty(strInReadingOrder);

                //on récupere le nom qui devrait être le premier qui n'as pas de chiffre
                int index = 0;
                for(int i = 0; i < strInReadingOrder.size();i++){
                    if(!strInReadingOrder.get(i).matches(".*\\d.*")){
                        index = i;
                        break;
                    }
                }
                String strNom = strInReadingOrder.get(index);
                strInReadingOrder.remove(index);
                inReadingOrder.remove(index);

                //on fetch les attaques
                for(int i = 0; i < strInReadingOrder.size();i++){
                    if(!strInReadingOrder.get(i).matches(".*\\d.*")){
                        index = i;
                        break;
                    }
                }

                //BOTTOM------------------------------------------------------
                //weakness
                Matcher mWeakness;
                String strWeakness;
                for(int i = 0; i < strInReadingOrder.size(); i++){
                    mWeakness = weaknessFR.matcher(strInReadingOrder.get(i));
                    if(mWeakness.find()){
                        bottomLeft = inReadingOrder.get(i).getBoundingBox().left;
                        strWeakness = strInReadingOrder.get(i).substring(mWeakness.start(),mWeakness.end());
                        String newString = strInReadingOrder.get(i).replace(strWeakness,"");
                        strInReadingOrder.remove(i);
                        strInReadingOrder.add(i,newString);
                        break;
                    }
                    else{
                        mWeakness = weaknessEN.matcher(strInReadingOrder.get(i));
                        if(mWeakness.find()){
                            bottomLeft = inReadingOrder.get(i).getBoundingBox().left;
                            strWeakness = strInReadingOrder.get(i).substring(mWeakness.start(),mWeakness.end());
                            String newString = strInReadingOrder.get(i).replace(strWeakness,"");
                            strInReadingOrder.remove(i);
                            strInReadingOrder.add(i,newString);
                            break;
                        }
                    }

                }
                //on clean les textBlocks qui sont vides
                strInReadingOrder = removeEmpty(strInReadingOrder);
                //resistance
                Matcher mResistance;
                String strResistance;
                for(int i = 0; i < strInReadingOrder.size(); i++){
                    mResistance = resistanceFR.matcher(strInReadingOrder.get(i));
                    if(mResistance.find()){
                        bottomLeft = inReadingOrder.get(i).getBoundingBox().left;
                        strResistance = strInReadingOrder.get(i).substring(mResistance.start(),mResistance.end());
                        String newString = strInReadingOrder.get(i).replace(strResistance,"");
                        strInReadingOrder.remove(i);
                        strInReadingOrder.add(i,newString);
                        break;
                    }
                    else{
                        mResistance = resistanceEN.matcher(strInReadingOrder.get(i));
                        if(mResistance.find()){
                            bottomLeft = inReadingOrder.get(i).getBoundingBox().left;
                            strResistance = strInReadingOrder.get(i).substring(mResistance.start(),mResistance.end());
                            String newString = strInReadingOrder.get(i).replace(strResistance,"");
                            strInReadingOrder.remove(i);
                            strInReadingOrder.add(i,newString);
                            break;
                        }
                    }

                }
                //on clean les textBlocks qui sont vides
                strInReadingOrder = removeEmpty(strInReadingOrder);
                //retreat


                //find matching textBlocks
                String attackPower = "";
                String attackName = "";
                String attackDescription = "";
                ArrayList<Text.TextBlock> middleBlocks = new ArrayList<>();
                ArrayList<String> attackStrings = new ArrayList<>();
                for(int i = 0; i < inReadingOrder.size();i++){
                    int boxLeft = inReadingOrder.get(i).getBoundingBox().left;
                    if(boxLeft > middleLeft + 50 && boxLeft < bottomLeft-50){
                        middleBlocks.add(inReadingOrder.get(i));
                    }
                }
                while(!middleBlocks.isEmpty()){
                    attackStrings.clear();
                    attackPower = "";
                    attackName = "";
                    attackDescription = "";
                    ArrayList<Integer> indexes = new ArrayList<>();
                    int left = middleBlocks.get(0).getBoundingBox().left;
                    for(int i = 0; i < middleBlocks.size();i++){
                        int boxLeft = middleBlocks.get(i).getBoundingBox().left;
                        if(boxLeft < left + 100 && boxLeft > left - 100){
                            strInReadingOrder.remove(i);
                            strInReadingOrder.add(i,"");
                            attackStrings.add(middleBlocks.get(i).getText());
                            indexes.add(i);
                        }
                    }
                    for (int i = indexes.size() -1; i>= 0;i--) {
                        middleBlocks.remove((int)indexes.get(i));
                    }
                    indexes.clear();
                    for(int i = 0; i < attackStrings.size();i++){
                        if(attackStrings.get(i).matches(".*\\d.*") && attackStrings.get(i).length() < 4){
                            attackPower = attackStrings.get(i);
                        }

                        if(attackStrings.get(i).length() > attackDescription.length()){
                            attackName = attackDescription;
                            attackDescription = attackStrings.get(i);
                        }
                    }
                    //si l'attaque à du damage, mais qu'il n'y a que deux strings, ça veut dire qu'il n'y a pas de description
                    //donc la description c'est le nom de l'attaque
                    if(attackPower != "" && attackStrings.size() < 3){
                        attackName = attackDescription;
                        attackDescription = "";
                    }
                    newCarteModel.attacks.put(attackName,attackPower+"|"+attackDescription);

                    //on clean les textBlocks qui sont vides
                    strInReadingOrder = removeEmpty(strInReadingOrder);
                }



                strInReadingOrder.add("test");
                inReadingOrder.size();

                newCarteModel.setAlolan(isAlolan);
                newCarteModel.setStage(evolutionText);
                if(evolvesFrom){
                    newCarteModel.setEvolvesFrom(strEvolvesFrom);
                }
                //process la pv string
                //newCarteModel.setPv();
                newCarteModel.setNumero(strNO);
                newCarteModel.setPokemonType(strPokeType);
                newCarteModel.setHeight(strHeight);
                newCarteModel.setWeight(strWeight);
                newCarteModel.setNom(strNom);

            }
        });

        //FrameLayout frame  = new FrameLayout(this);
    }
    ArrayList<String> removeEmpty(ArrayList<String> inArray){
        ArrayList<String> newStrArray = new ArrayList<>();
        for (int i = 0; i < inArray.size(); i++){
            String trimmedText = inArray.get(i).trim();
            if(!trimmedText.isEmpty()){
                newStrArray.add(inArray.get(i));
            }
            else{
                inReadingOrder.remove(i);
            }
        }
        return newStrArray;
    }
}