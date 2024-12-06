package uqac.dim.projetcartalogue;

import static android.graphics.Color.argb;
import static android.graphics.Color.rgb;
import static android.graphics.Color.valueOf;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.android.datatransport.BuildConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {


    Button btnCamera;
    Bitmap imgBitmap;
    String currentPhotoPath;
    ArrayList<Text.TextBlock> sortHolder;
    ArrayList<OCRDataBlock> inReadingOrder;
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int REQUEST_IMAGES_CODE = 110;
    private static final int REQUEST_WRITE_STORAGE_CODE = 120;
    private static final int REQUEST_READ_STORAGE_CODE = 130;
    public final double colorMargin = 0.3;
    ActionBarDrawerToggle toggle;
    List<CarteModel> deckList;

    //TOP
    Pattern basicEn = Pattern.compile("Basic", Pattern.CASE_INSENSITIVE); // testé et fonctionnel
    Pattern basicFR = Pattern.compile("Base", Pattern.CASE_INSENSITIVE); // testé et fonctionnel

    Pattern stageEN = Pattern.compile("Stage\\s?[12]", Pattern.CASE_INSENSITIVE); // testé et fonctionnel
    Pattern stageFR = Pattern.compile("niveau\\s?[12]", Pattern.CASE_INSENSITIVE); // testé et fonctionnel

    Pattern megaEN = Pattern.compile("mega", 2); //testé et fonctionnel
    Pattern megaFR = Pattern.compile("m.ga", 2); //testé et fonctionnel

    Pattern alolanEN = Pattern.compile("Alolan", Pattern.CASE_INSENSITIVE); //testé et fonctionnel
    Pattern alolanFR = Pattern.compile("d'alola", 2); //testé et fonctionnel

    Pattern evolvesFromEN = Pattern.compile("Evolves\\s?f?r?o?m?\\s?.{3,13}", 2); //testé et fonctionnel
    Pattern evolvesFromFR = Pattern.compile(".volution\\s?d?e?\\s?.{3,13}", 2); //testé et fonctionnel

    Pattern pvEN = Pattern.compile("^[HW]?.?\\s?\\d{2,3}", Pattern.CASE_INSENSITIVE);//testé et fonctionnel
    Pattern pvFR = Pattern.compile("^P?.?\\s?\\d{2,3}", 2);//testé et fonctionnel

    //MIDDLE
    Pattern numberEN = Pattern.compile("^N?O?\\.?\\s*\\d{3}", Pattern.CASE_INSENSITIVE); //testé et fonctionnel
    Pattern numberFR = Pattern.compile("^N?°?\\s*\\d{3}", Pattern.CASE_INSENSITIVE); //testé et fonctionnel

    Pattern pokemonTypeEN = Pattern.compile("[\\w\\s]{2,16}pok.mon\\s?", 2); //testé et fonctionnel
    Pattern pokemonTypeFR = Pattern.compile("^\\s?pok.mon[\\w\\s]{5,}", 2); //testé et fonctionnel

    Pattern heightEN = Pattern.compile("H?T?\\s*:?\\s*\\d{1,3}['\\s]?\\d{2}", 2); // testé et fonctionnel
    Pattern heightFR = Pattern.compile("Taille\\s?:?\\s?(\\d+[\\.,]\\d+)\\s?m?", 2); // testé et fonctionnel

    Pattern weightEN = Pattern.compile("WT\\s*:?\\s*\\d{1,4}\\.?\\d\\s?l?b?s?", 2); // testé et fonctionnel
    Pattern weightFR = Pattern.compile("Poids\\s?:?\\s?(\\d+[\\.,]\\d+)\\s?k?g?"); // testé et fonctionnel

    //BOTTOM
    Pattern weaknessEN = Pattern.compile("weakness", 2);
    Pattern weaknessFR = Pattern.compile("faib[li]esse", 2);

    Pattern resistanceEN = Pattern.compile("resistance", 2);
    Pattern resistanceFR = Pattern.compile("R.sistance", 2);

    Pattern retreatEN = Pattern.compile("retreat cost", 2);
    Pattern retreatFR = Pattern.compile("Retraite", 2);


    Pattern noSpecialChar = Pattern.compile("[^\\w\\s]", Pattern.CASE_INSENSITIVE);
    Pattern digitsOnly = Pattern.compile("\\d{2,3}",2);

    //des variables spéciales pour le scan des cartes
    boolean evolvesFrom = false;
    int middleLeft = 0;
    int bottomLeft = 0;

    // les variables de BD
    private CarteBD cbd;
    private CarteModel carteModel;
    private CarteDao carteDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        deckList = new ArrayList<>();
        evolvesFrom = false;
        middleLeft = 0;
        bottomLeft = 0;
        //on get les views
        btnCamera = findViewById(R.id.CameraBtn);

        //permission pour la camera
        if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.CAMERA"}, REQUEST_CAMERA_CODE);
        }
        //permissions pour les photos du stockage
        if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.READ_MEDIA_IMAGES") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_MEDIA_IMAGES"}, REQUEST_IMAGES_CODE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_WRITE_STORAGE_CODE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, REQUEST_READ_STORAGE_CODE);
        }
        //pour la bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.VISIBLE);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

    }
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {

        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.item_ouverture_fragment_1){
            Toast.makeText(MainActivity.this, "Open User", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            try{
                startActivity(intent);
            }
            catch (Exception e){
                System.out.println(e);
            }
        }

        //get une photo from stockage
        else if (itemId == R.id.item_ouverture_fragment_2)
        {
            Intent getPhoto = new Intent(MediaStore.ACTION_PICK_IMAGES);
            startActivityForResult(getPhoto, 1);
        }
        else if (itemId == R.id.item_ouverture_fragment_3){

            /*CarteModel carteModel1 = new CarteModel(1,R.drawable.pikachu,"1","Pikachu","Electric");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cbd.carteDao().delete(carteModel1);
                    cbd.carteDao().addCarte(carteModel1);
                }
            }).start();*/

            Intent intent = new Intent(MainActivity.this, Cartalogue.class);
            //on transfer les infos de connexion aux cartalogue
            intent.putExtra("userId",getIntent().getIntExtra("userId",-1));
            intent.putExtra("username",getIntent().getStringExtra("username"));
            intent.putExtra("password",getIntent().getStringExtra("password"));

            try{
                startActivity(intent);
            }
            catch (Exception e){
                System.out.println(e);
            }

            Toast.makeText(MainActivity.this, "Open Collection", Toast.LENGTH_SHORT).show();
        }
        return true;
    };
    public void StartCameraActivity(View view){
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            photoFile = createImageFile();

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.navigationdrawerfinal.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 2);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;  // ActionBarDrawerToggle gère cet item
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // on va loader les cartes du deck pour savoir si le deck est plein
            Context context = getApplicationContext();
            cbd = CarteBD.getDataBase(context);
            CarteDao carteDao = cbd.carteDao();
            carteDao.getAllCarte().observe(this, new Observer<List<CarteModel>>() {

                @Override
                public void onChanged(List<CarteModel> ca) {
                    // Clear the existing list and add the new data from the database
                    deckList.clear();
                    ArrayList<CarteModel> carteList = new ArrayList<>();
                    // Clear previous data
                    if (ca != null) {
                        //on ajoute à carteList juste les cartes qui sont à notre user
                        for (CarteModel carteModel:ca){
                            if(!carteModel.idUtilisateur.isEmpty()){
                                String[] user = carteModel.idUtilisateur.split("\\|");
                                if (!user[0].isEmpty()) {
                                    int id = Integer.parseInt(user[0]);
                                    if (id == getIntent().getIntExtra("userId",-1)) {
                                        carteList.add(carteModel);
                                    }
                                }
                            }
                        }
                    }

                    // on load les cartes du deck
                    for (CarteModel carteModel:carteList){
                        if (carteModel.isDeck()){
                            if(!deckList.contains(carteModel)){
                                deckList.add(carteModel);
                            }
                        }
                    }
                }
            });

            if (requestCode == 1) {
                Uri imageUri = data.getData();

                try {

                    imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                    ExtractText(imgBitmap);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (requestCode == 2) {
                try {

                    File f = new File(currentPhotoPath);
                    Uri contentUri = Uri.fromFile(f);
                    ImageDecoder.Source source =  ImageDecoder.createSource(this.getContentResolver(), contentUri);
                    imgBitmap = ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true);
                    imgBitmap = rotateBitmap(imgBitmap,270);
                    ExtractText(imgBitmap);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private void ExtractText(Bitmap bitmap) {

        TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> task = textRecognizer.process(bitmap, 0);
        task.addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                CarteModel newCarteModel = new CarteModel();
                evolvesFrom = false;
                middleLeft = 0;
                bottomLeft = 0;

                //on scan la couleur
                int size = bitmap.getWidth() * bitmap.getHeight();
                int[] pixelsMatchingToType = new int[PokemonTypeColors.values().length];
                int[] allPixels = new int[size];
                bitmap.getPixels(allPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                for (int i = 0; i < size; i += 300) {
                    Color color = Color.valueOf(allPixels[i]);
                    int index = 0;
                    for (PokemonTypeColors type : PokemonTypeColors.values()) {
                        Color typeColor = Color.valueOf(Color.rgb(type.getR(), type.getG(), type.getB()));
                        double contrast = ColorUtils.calculateContrast(allPixels[i], rgb(type.getR(), type.getG(), type.getB()));
                        float[] HCT1 = new float[3];
                        float[] HCT2 = new float[3];
                        //ColorUtils.colorToLAB(allPixels[i],lab1);
                        ColorUtils.colorToM3HCT(allPixels[i], HCT1);
                        ColorUtils.colorToM3HCT(Color.rgb(type.getR(), type.getG(), type.getB()), HCT2);
                        //ColorUtils.colorToLAB(Color.rgb(type.getR(),type.getG(),type.getB()),lab2);
                        double differenceHUE = Math.abs(HCT1[0] - HCT2[0]);
                        double differenceC = Math.abs(HCT1[1] - HCT2[1]);
                        double differenceT = Math.abs(HCT1[2] - HCT2[2]);
                        //int difference = Math.abs(rgb(type.getR(),type.getG(),type.getB()) - allPixels[i] );

                        if (differenceHUE < 5 && differenceT < 10 /*&& differenceC < 10*/) {
                            //if((color.red() >typeColor.red() -colorMargin && color.red() < typeColor.red() +colorMargin) && (color.green() > typeColor.green() -colorMargin && color.green() < typeColor.green() +colorMargin) && (color.blue() > typeColor.blue() - colorMargin && color.blue() < typeColor.blue() + colorMargin)){
                            pixelsMatchingToType[index]++;
                            break;
                        }
                        index++;

                    }

                }
                int highestMatchNb = 0;
                int highestMatchIndex = 0;

                for (int x = 0; x < pixelsMatchingToType.length; x++) {
                    if (highestMatchNb < pixelsMatchingToType[x]) {
                        highestMatchNb = pixelsMatchingToType[x];
                        highestMatchIndex = x;
                    }
                }

                PokemonTypeColors matchingType = PokemonTypeColors.values()[highestMatchIndex];

                newCarteModel.type = matchingType.name();

                List<Text.TextBlock> result = task.getResult().getTextBlocks();
                sortHolder = new ArrayList<Text.TextBlock>();
                inReadingOrder = new ArrayList<>();


                // on trie les textblocks en ordre de lecture
                for (int x = 0; x < result.size(); x++) {
                    if (sortHolder.isEmpty()) {
                        sortHolder.add(result.get(x));
                    } else {
                        for (int y = 0; y < sortHolder.size(); y++) {
                            if (result.get(x).getBoundingBox().left < sortHolder.get(y).getBoundingBox().left) {
                                sortHolder.add(y, result.get(x));
                                break;
                            }

                        }
                        if (!sortHolder.contains(result.get(x))) {
                            sortHolder.add(result.get(x));
                        }
                    }
                }
                for (Text.TextBlock textBlock:sortHolder) {
                    inReadingOrder.add(new OCRDataBlock(textBlock.getText(),textBlock.getBoundingBox()));
                }

                //TOP--------------------------------------------------------------------------------
                //check pour l'evolution(basic ou stage1/stage2) ou mega
                String evolutionText = ScanForEvolution();
                //on clean les textBlocks qui sont vides
                inReadingOrder = removeEmpty(inReadingOrder);

                //ensuite on check si alolan
                boolean isAlolan = ScanForAlolan();
                //on clean les textBlocks qui sont vides
                inReadingOrder = removeEmpty(inReadingOrder);

                // si c'est pas une base on check evolves from
                String strEvolvesFrom = ScanForEvolvesFrom();
                // on clean les textBlocks qui sont vides
                inReadingOrder = removeEmpty(inReadingOrder);

                // on get les pv
                String strPv = ScanForPV();
                //on clean les textBlocks qui sont vides
                inReadingOrder = removeEmpty(inReadingOrder);

                //MIDDLE------------------------------------------------------------------------------------
                // get le numero
                String strNO = ScanForNumber();
                //on clean les textBlocks qui sont vides
                inReadingOrder = removeEmpty(inReadingOrder);

                // get le height
                String strHeight = ScanForHeight();
                //on clean les textBlocks qui sont vides
                inReadingOrder = removeEmpty(inReadingOrder);

                // get le weigth
                String strWeight = ScanForWeight();
                //on clean les textBlocks qui sont vides
                inReadingOrder = removeEmpty(inReadingOrder);

                // get le pokemon type
                String strPokeType = ScanForPokemonType();
                //on clean les textBlocks qui sont vides
                inReadingOrder = removeEmpty(inReadingOrder);

                //on récupere le nom qui devrait être le premier qui n'as pas de chiffre
                int index = 0;
                for (int i = 0; i < inReadingOrder.size(); i++) {
                    if (!inReadingOrder.get(i).text.matches(".*\\d.*")) {
                        index = i;
                        break;
                    }
                }
                String strNom = inReadingOrder.get(index).text;
                inReadingOrder.remove(index);

                //BOTTOM------------------------------------------------------
                //weakness
                String strWeakness = ScanForWeakness();
                //on clean les textBlocks qui sont vides
                inReadingOrder = removeEmpty(inReadingOrder);

                //resistance
                String strResistance = ScanForResistance();
                //on clean les textBlocks qui sont vides
                inReadingOrder = removeEmpty(inReadingOrder);
                //retreat
                String strRetreat = ScanForRetreat();
                //on clean les textBlocks qui sont vides
                inReadingOrder = removeEmpty(inReadingOrder);


                //on cherche pour les attaques
                //find matching textBlocks
                String attackPower = "";
                String attackName = "";
                String attackDescription = "";
                // on ramasse tous les blocks qui sont au milieu
                ArrayList<OCRDataBlock> middleBlocks = new ArrayList<>();
                ArrayList<String> attackStrings = new ArrayList<>();
                for(int i = 0; i < inReadingOrder.size();i++){
                    int boxLeft = inReadingOrder.get(i).bounds.left;
                    if(boxLeft > middleLeft + 50 && boxLeft < bottomLeft-50){
                        middleBlocks.add(inReadingOrder.get(i));
                    }
                }
                // on prends les blocks qui sont très proche horizontalement (sur la même ligne donc même attaque)
                for(int x = 0; x < 4; x++){
                    if(middleBlocks.isEmpty()){
                        break;
                    }
                    attackStrings.clear();
                    attackPower = "";
                    attackName = "";
                    attackDescription = "";
                    ArrayList<Integer> indexes = new ArrayList<>();
                    int left = middleBlocks.get(0).bounds.left;
                    for(int i = middleBlocks.size() -1; i >= 0 ;i--){
                        int boxLeft = middleBlocks.get(i).bounds.left;
                        if(boxLeft < left + 100 && boxLeft > left - 100){
                            attackStrings.add(middleBlocks.get(i).text);
                            inReadingOrder.get(i).text = "";
                            indexes.add(i);
                        }
                    }
                    for (int i = indexes.size() -1; i>= 0;i--) {
                        middleBlocks.remove((int)indexes.get(i));
                    }
                    indexes.clear();
                    // on regarde c'est lequel le nom, la description ou la puissance
                    for(int i = 0; i < attackStrings.size();i++){
                        if(attackStrings.get(i).matches(".*\\d.*") && attackStrings.get(i).length() < 4){
                            attackPower = attackStrings.get(i);
                        }

                        if(attackStrings.get(i).length() > attackDescription.length()){
                            attackName = attackDescription;
                            attackDescription = attackStrings.get(i);
                        }
                        else{
                            attackName = attackStrings.get(i);
                        }
                    }
                    //si l'attaque à du damage, mais qu'il n'y a que deux strings, ça veut dire qu'il n'y a pas de description
                    //donc la description c'est le nom de l'attaque
                    if(attackPower != "" && attackStrings.size() < 3){
                        attackName = attackDescription;
                        attackDescription = "";
                    }
                    switch (x){
                        case 0 : newCarteModel.attack1 = attackName+"|"+attackPower+"|"+attackDescription; break;
                        case 1 : newCarteModel.attack2 = attackName+"|"+attackPower+"|"+attackDescription; break;
                        case 2 : newCarteModel.attack3 = attackName+"|"+attackPower+"|"+attackDescription; break;
                        case 3 : newCarteModel.attack4 = attackName+"|"+attackPower+"|"+attackDescription; break;
                    }


                    //on clean les textBlocks qui sont vides
                    inReadingOrder = removeEmpty(inReadingOrder);
                }

                //on fetch la description
                String strDescription =strPokeType + ": \n";
                int longestIndex = -1;
                int longestCharNumber = -1;
                for(int x = 0; x< inReadingOrder.size(); x++){

                    if(inReadingOrder.get(x).text.length() > longestCharNumber){
                        longestIndex = x;
                        longestCharNumber = inReadingOrder.get(x).text.length();
                    }
                }
                if(longestIndex > -1){
                    strDescription = strDescription + inReadingOrder.get(longestIndex).text;
                    inReadingOrder.remove(longestIndex);
                }

                //création de la carte à partir des infos récupérés
                /// set idUtilisateur pour les cartes
                newCarteModel.setAlolan(isAlolan);
                newCarteModel.setStage(evolutionText);
                newCarteModel.setEvolvesFrom(strEvolvesFrom);

                //process la pv string
                Matcher mDigits = digitsOnly.matcher(strPv);
                if(mDigits.find()){
                    String strNumber = strPv.substring(mDigits.start(),mDigits.end());
                    newCarteModel.setPv(Integer.parseInt(strNumber));
                };
                newCarteModel.setNumero(strNO);
                newCarteModel.setPokemonType(strPokeType);
                newCarteModel.setHeight(strHeight);
                newCarteModel.setWeight(strWeight);
                newCarteModel.setNom(strNom);
                newCarteModel.setDescription(strDescription);
                //on fait tourner le bitmap pour que l'image soit dans le bon sens
                if(imgBitmap.getHeight() < imgBitmap.getWidth()){
                    newCarteModel.setImgBitmap(rotateBitmap(imgBitmap,90));
                }
                else{
                    newCarteModel.setImgBitmap(imgBitmap);
                }
                //on fait un popup pour permettre a l'utilisateur d'editer les infos avant de créer la carte
                EditCarteModelPopup(newCarteModel);


            }
        });

    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getPath();
        return image;
    }


    ArrayList<OCRDataBlock> removeEmpty(ArrayList<OCRDataBlock> inArray) {
        ArrayList<OCRDataBlock> newStrArray = new ArrayList<>();
        for (int i = inArray.size() -1; i >= 0; i--) {
            String trimmedText = inArray.get(i).text.trim();
            String withoutSpecialChars =trimmedText.replaceAll("\\W","");
            if (!withoutSpecialChars.isEmpty()) {
                newStrArray.add(0,inArray.get(i));
            }
        }
        return newStrArray;
    }

    String ScanForEvolution(){
        String evolutionText = "";
        Matcher mBasicFR;
        Matcher mBasicEN;
        for (int i = 0; i < inReadingOrder.size(); i++) {

            mBasicFR = basicFR.matcher(inReadingOrder.get(i).text);
            mBasicEN = basicEn.matcher(inReadingOrder.get(i).text);
            if(mBasicFR.find()){
                evolutionText = inReadingOrder.get(i).text.substring(mBasicFR.start(),mBasicFR.end());
                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(evolutionText,"");
                break;

            } else {
                if (mBasicEN.find()) {
                    evolutionText = inReadingOrder.get(i).text.substring(mBasicEN.start(), mBasicEN.end());
                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(evolutionText,"");
                    break;
                } else {
                    // c'est un pokemon deja evolue
                    mBasicFR = stageFR.matcher(inReadingOrder.get(i).text);
                    mBasicEN = stageEN.matcher(inReadingOrder.get(i).text);
                    if(mBasicFR.find()){
                        evolvesFrom = true;
                        evolutionText = inReadingOrder.get(i).text.substring(mBasicFR.start(), mBasicFR.end());
                        inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(evolutionText,"");

                        break;
                    } else {
                        if (mBasicEN.find()) {
                            evolvesFrom = true;
                            evolutionText = inReadingOrder.get(i).text.substring(mBasicEN.start(), mBasicEN.end());
                            inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(evolutionText,"");

                            break;
                        } else {
                            //c'est une mega evolution
                            mBasicFR = megaFR.matcher(inReadingOrder.get(i).text);
                            mBasicEN = megaEN.matcher(inReadingOrder.get(i).text);
                            if(mBasicFR.find()){
                                evolvesFrom = true;
                                evolutionText = inReadingOrder.get(i).text.substring(mBasicFR.start(), mBasicFR.end());
                                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(evolutionText,"");

                                break;
                            } else {
                                if (mBasicEN.find()) {
                                    evolvesFrom = true;
                                    evolutionText = inReadingOrder.get(i).text.substring(mBasicEN.start(), mBasicEN.end());
                                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(evolutionText,"");

                                    break;
                                }
                            }
                        }
                    }

                }
            }

        }
        return evolutionText;
    }
    boolean ScanForAlolan(){
        Matcher mAlolan;
        boolean isAlolan = false;
        for (int i = 0; i < inReadingOrder.size(); i++) {

            mAlolan = alolanFR.matcher(inReadingOrder.get(i).text);
            if (mAlolan.find()) {
                isAlolan = true;
                String toRemove = inReadingOrder.get(i).text.substring(mAlolan.start(), mAlolan.end());
                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(toRemove, "");
                break;
            } else {
                mAlolan = alolanEN.matcher(inReadingOrder.get(i).text);
                if (mAlolan.find()) {
                    isAlolan = true;
                    String toRemove = inReadingOrder.get(i).text.substring(mAlolan.start(), mAlolan.end());
                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(toRemove, "");
                    break;
                }
            }
        }
        return isAlolan;
    }
    String ScanForEvolvesFrom(){
        String strEvolvesFrom = "";
        Matcher mEvolvesFrom;
        for (int i = 0; i < inReadingOrder.size(); i++) {

            mEvolvesFrom = evolvesFromFR.matcher(inReadingOrder.get(i).text);
            if (mEvolvesFrom.find()) {
                strEvolvesFrom = inReadingOrder.get(i).text.substring(mEvolvesFrom.start(), mEvolvesFrom.end());
                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strEvolvesFrom, "");
                break;
            } else {
                mEvolvesFrom = evolvesFromEN.matcher(inReadingOrder.get(i).text);
                if (mEvolvesFrom.find()) {
                    strEvolvesFrom = inReadingOrder.get(i).text.substring(mEvolvesFrom.start(), mEvolvesFrom.end());
                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strEvolvesFrom, "");
                    break;
                }
            }

        }
        return strEvolvesFrom;
    }
    String ScanForPV(){
        Matcher mPv;
        String strPv = "";
        for (int i = 0; i < inReadingOrder.size(); i++) {

            mPv = pvFR.matcher(inReadingOrder.get(i).text);
            if (mPv.find()) {
                strPv = inReadingOrder.get(i).text.substring(mPv.start(), mPv.end());
                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strPv, "");
                break;
            } else {
                mPv = pvEN.matcher(inReadingOrder.get(i).text);
                if (mPv.find()) {
                    strPv = inReadingOrder.get(i).text.substring(mPv.start(), mPv.end());
                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strPv, "");
                    break;
                }
            }
        }
        return strPv;
    }
    String ScanForNumber(){
        Matcher mNO;
        String strNO = "";
        for (int i = 0; i < inReadingOrder.size(); i++) {
            mNO = numberFR.matcher(inReadingOrder.get(i).text);
            if(mNO.find()){
                middleLeft = inReadingOrder.get(i).bounds.left;
                strNO = inReadingOrder.get(i).text.substring(mNO.start(),mNO.end());
                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strNO,"");
                strNO = strNO.substring(mNO.end() -3, mNO.end());

                break;
            } else {
                mNO = numberEN.matcher(inReadingOrder.get(i).text);
                if(mNO.find()){
                    middleLeft = inReadingOrder.get(i).bounds.left;
                    strNO = inReadingOrder.get(i).text.substring(mNO.start(),mNO.end());
                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strNO,"");
                    strNO = strNO.substring(mNO.end() -3, mNO.end());

                    break;
                }
            }

        }
        return strNO;
    }
    String ScanForPokemonType(){
        Matcher mPokeType;
        String strPokeType = "";
        for (int i = 0; i < inReadingOrder.size(); i++) {

            mPokeType = pokemonTypeFR.matcher(inReadingOrder.get(i).text);
            if (mPokeType.find()) {
                strPokeType = inReadingOrder.get(i).text.substring(mPokeType.start(), mPokeType.end());
                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strPokeType, "");
                break;
            } else {
                mPokeType = pokemonTypeEN.matcher(inReadingOrder.get(i).text);
                if (mPokeType.find()) {
                    strPokeType = inReadingOrder.get(i).text.substring(mPokeType.start(), mPokeType.end());
                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strPokeType, "");
                    break;
                }
            }

        }
        return strPokeType;
    }
    String ScanForHeight(){
        Matcher mHeight;
        String strHeight = "";
        for (int i = 0; i < inReadingOrder.size(); i++) {
            mHeight = heightFR.matcher(inReadingOrder.get(i).text);
            if (mHeight.find()) {
                strHeight = inReadingOrder.get(i).text.substring(mHeight.start(), mHeight.end());
                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strHeight, "");
                break;
            } else {
                mHeight = heightEN.matcher(inReadingOrder.get(i).text);
                if (mHeight.find()) {
                    strHeight = inReadingOrder.get(i).text.substring(mHeight.start(), mHeight.end());
                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strHeight, "");
                    break;
                }
            }

        }
        return strHeight;
    }
    String ScanForWeight(){
        Matcher mWeight;
        String strWeight = "";
        for (int i = 0; i < inReadingOrder.size(); i++) {
            mWeight = weightFR.matcher(inReadingOrder.get(i).text);
            if (mWeight.find()) {
                strWeight = inReadingOrder.get(i).text.substring(mWeight.start(), mWeight.end());
                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strWeight, "");
                break;
            } else {
                mWeight = weightEN.matcher(inReadingOrder.get(i).text);
                if (mWeight.find()) {
                    strWeight = inReadingOrder.get(i).text.substring(mWeight.start(), mWeight.end());
                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strWeight, "");
                    break;
                }
            }

        }
        return strWeight;
    }
    String ScanForWeakness(){
        Matcher mWeakness;
        String strWeakness = "";
        for(int i = 0; i < inReadingOrder.size(); i++){
            mWeakness = weaknessFR.matcher(inReadingOrder.get(i).text);
            if(mWeakness.find()){
                bottomLeft = inReadingOrder.get(i).bounds.left;
                strWeakness = inReadingOrder.get(i).text.substring(mWeakness.start(),mWeakness.end());
                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strWeakness,"");
                break;
            } else {
                mWeakness = weaknessEN.matcher(inReadingOrder.get(i).text);
                if(mWeakness.find()){
                    bottomLeft = inReadingOrder.get(i).bounds.left;
                    strWeakness = inReadingOrder.get(i).text.substring(mWeakness.start(),mWeakness.end());
                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strWeakness,"");
                    break;
                }
            }

        }
        return strWeakness;
    }
    String ScanForResistance(){
        Matcher mResistance;
        String strResistance = "";
        for(int i = 0; i < inReadingOrder.size(); i++){
            mResistance = resistanceFR.matcher(inReadingOrder.get(i).text);
            if(mResistance.find()){
                bottomLeft = inReadingOrder.get(i).bounds.left;
                strResistance = inReadingOrder.get(i).text.substring(mResistance.start(),mResistance.end());
                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strResistance,"");
                break;
            } else {
                mResistance = resistanceEN.matcher(inReadingOrder.get(i).text);
                if(mResistance.find()){
                    bottomLeft = inReadingOrder.get(i).bounds.left;
                    strResistance = inReadingOrder.get(i).text.substring(mResistance.start(),mResistance.end());
                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strResistance,"");
                    break;
                }
            }

        }
        return strResistance;
    }
    String ScanForRetreat(){
        Matcher mRetreat;
        String strRetreat = "";
        for(int i = 0; i < inReadingOrder.size(); i++){
            mRetreat = resistanceFR.matcher(inReadingOrder.get(i).text);
            if( mRetreat.find()){
                bottomLeft = inReadingOrder.get(i).bounds.left;
                strRetreat = inReadingOrder.get(i).text.substring( mRetreat.start(), mRetreat.end());
                inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strRetreat,"");
                break;
            } else {
                mRetreat = resistanceEN.matcher(inReadingOrder.get(i).text);
                if( mRetreat.find()){
                    bottomLeft = inReadingOrder.get(i).bounds.left;
                    strRetreat = inReadingOrder.get(i).text.substring( mRetreat.start(), mRetreat.end());
                    inReadingOrder.get(i).text = inReadingOrder.get(i).text.replace(strRetreat,"");
                    break;
                }
            }

        }
        return strRetreat;
    }

    void EditCarteModelPopup(CarteModel modelToEdit){
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.edit_card_layout, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(btnCamera, Gravity.CENTER, 0, 0);

        //on get les views pour editer et on leur set les bonnes valeurs
        ImageView pokemonImg = popupView.findViewById(R.id.pokemonImg);
        EditText numberEdit = popupView.findViewById(R.id.numberEdit);
        EditText nameEdit = popupView.findViewById(R.id.nameEdit);
        Spinner typeSpinner = popupView.findViewById(R.id.typeSpinner);
        EditText pvEdit = popupView.findViewById(R.id.PvEdit);
        CheckBox chkIsAlolan = popupView.findViewById(R.id.isAlolan);
        CheckBox chkInDeck = popupView.findViewById(R.id.isDeck);
        Spinner stageSpinner = popupView.findViewById(R.id.stageSpinner);
        EditText evolvesFromEdit = popupView.findViewById(R.id.evolvesFromEdit);
        EditText heightEdit = popupView.findViewById(R.id.heightEdit);
        EditText weightEdit = popupView.findViewById(R.id.weightEdit);

        //Attaque 1 :
        LinearLayout attack1 = popupView.findViewById(R.id.attack1);
        EditText attack1Name = popupView.findViewById(R.id.attack1Name);
        EditText attack1Desc = popupView.findViewById(R.id.attack1Desc);
        EditText attack1Power = popupView.findViewById(R.id.attack1Power);

        //Attaque 2 :
        LinearLayout attack2 = popupView.findViewById(R.id.attack2);
        EditText attack2Name = popupView.findViewById(R.id.attack2Name);
        EditText attack2Desc = popupView.findViewById(R.id.attack2Desc);
        EditText attack2Power = popupView.findViewById(R.id.attack2Power);

        //Attaque 3 :
        LinearLayout attack3 = popupView.findViewById(R.id.attack3);
        EditText attack3Name = popupView.findViewById(R.id.attack3Name);
        EditText attack3Desc = popupView.findViewById(R.id.attack3Desc);
        EditText attack3Power = popupView.findViewById(R.id.attack3Power);

        //Attaque 4 :
        LinearLayout attack4 = popupView.findViewById(R.id.attack4);
        EditText attack4Name = popupView.findViewById(R.id.attack4Name);
        EditText attack4Desc = popupView.findViewById(R.id.attack4Desc);
        EditText attack4Power = popupView.findViewById(R.id.attack4Power);

        EditText descriptionEdit = popupView.findViewById(R.id.descriptionEdit);
        Button saveBtn = popupView.findViewById(R.id.saveBtn);
        Button cancelBtn = popupView.findViewById(R.id.cancelBtn);


        // on set les valeurs des views
        pokemonImg.setImageBitmap(modelToEdit.getImgBitmap());
        numberEdit.setText(modelToEdit.getNumero());
        nameEdit.setText(modelToEdit.getNom());
        //on set le selected item du spinner
        typeSpinner.setSelection(PokemonTypeColors.valueOf(modelToEdit.type).ordinal());

        pvEdit.setText(modelToEdit.getPv() + "");
        chkIsAlolan.setChecked(modelToEdit.isAlolan());
        chkInDeck.setChecked(modelToEdit.deck);
        chkInDeck.setText("Deck (" + deckList.size() +"/60)");
        //on set le selected item du spinner
        List<String> stages = Arrays.asList(getResources().getStringArray(R.array.stages_array));
        if(modelToEdit.getStage().matches("(?i)base") || modelToEdit.getStage().matches("(?i)basic")  ){
            modelToEdit.setStage(stages.get(0));
        }
        if(modelToEdit.getStage().matches("(?i)niveau\\s?1") || modelToEdit.getStage().matches("(?i)stage\\s?1")){
            modelToEdit.setStage(stages.get(1));
        }
        if(modelToEdit.getStage().matches("niveau\\s?2") || modelToEdit.getStage().matches("(?i)stage\\s?2")){
            modelToEdit.setStage(stages.get(2));
        }
        stageSpinner.setSelection(stages.indexOf(modelToEdit.getStage().toLowerCase()));

        evolvesFromEdit.setText(modelToEdit.getEvolvesFrom());
        heightEdit.setText(modelToEdit.getHeight());
        weightEdit.setText(modelToEdit.getWeight());


        //on process l'attaque 1
        String[] split;
        split = modelToEdit.attack1.split("\\|");
        if(split.length > 0){
            attack1Name.setText(split[0]);
        }
        if(split.length > 1){
            attack1Power.setText(split[1]);
        }

        if(split.length > 2){
            attack1Desc.setText(split[2]);

        }


        //on process l'attaque 2
        split = modelToEdit.attack2.split("\\|");

        if(split.length > 0){
            attack2Name.setText(split[0]);
        }

        if(split.length > 1){
            attack2Power.setText(split[1]);
        }

        if(split.length > 2){
            attack2Desc.setText(split[2]);

        }

        //on process l'attaque 3
        split = modelToEdit.attack3.split("\\|");

        if(split.length > 0){
            attack3Name.setText(split[0]);
        }

        if(split.length > 1){
            attack3Power.setText(split[1]);
        }

        if(split.length > 2){
            attack3Desc.setText(split[2]);

        }

        //on process l'attaque 4
        split = modelToEdit.attack4.split("\\|");

        if(split.length > 0){
            attack4Name.setText(split[0]);
        }

        if(split.length > 1){
            attack4Power.setText(split[1]);
        }

        if(split.length > 2){
            attack4Desc.setText(split[2]);

        }


        descriptionEdit.setText(modelToEdit.getDescription());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //on fait la sauvegarde des données

                //modelToEdit.idDeck = "";
                //modelToEdit.idUtilisateur = "";

                modelToEdit.setNumero(numberEdit.getText().toString());
                modelToEdit.setNom(nameEdit.getText().toString());
                modelToEdit.setType(typeSpinner.getSelectedItem().toString());
                modelToEdit.setPv(Integer.parseInt(pvEdit.getText().toString()));
                modelToEdit.setAlolan(chkIsAlolan.isChecked());
                modelToEdit.setDeck(chkInDeck.isChecked());
                modelToEdit.setStage(stageSpinner.getSelectedItem().toString());
                modelToEdit.setEvolvesFrom(evolvesFromEdit.getText().toString());
                modelToEdit.setHeight(heightEdit.getText().toString());
                modelToEdit.setWeight(weightEdit.getText().toString());
                modelToEdit.setDescription(descriptionEdit.getText().toString());

                //on load les attaques
                modelToEdit.attack1 = attack1Name.getText().toString() + "|" + attack1Power.getText().toString()+"|"+attack1Desc.getText().toString();

                modelToEdit.attack2 = attack2Name.getText().toString() + "|" + attack2Power.getText().toString()+"|"+attack2Desc.getText().toString();

                modelToEdit.attack3 = attack3Name.getText().toString() + "|" + attack3Power.getText().toString()+"|"+attack3Desc.getText().toString();

                modelToEdit.attack4 = attack4Name.getText().toString() + "|" + attack4Power.getText().toString()+"|"+attack4Desc.getText().toString();

                // on crée l'idUtilisateur à partir des infos de connexion
                modelToEdit.idUtilisateur = getIntent().getIntExtra("userId",-1) + "|" + getIntent().getStringExtra("username") + "|" + getIntent().getStringExtra("password");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            cbd.carteDao().addCarte(modelToEdit);
                        }
                        catch(Exception e){
                            System.out.println(e);
                        }

                    }
                }).start();

                popupWindow.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();

            }
        });

        chkInDeck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(deckList.size() >= 60){
                        chkInDeck.setChecked(false);
                    }
                    else{
                        deckList.add(modelToEdit);

                    }

                }
                else{
                    deckList.remove(modelToEdit);
                }
                chkInDeck.setText("Deck (" + deckList.size() +"/60)");
            }
        });

    }
    public Bitmap rotateBitmap(Bitmap original, float degrees) {
        int width = original.getWidth();
        int height = original.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, width, height, true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        return rotatedBitmap;
    }
}

class OCRDataBlock{
    public String text;
    public Rect bounds;
    public OCRDataBlock(String text, Rect bounds){
        this.text = text;
        this.bounds = bounds;
    }
}