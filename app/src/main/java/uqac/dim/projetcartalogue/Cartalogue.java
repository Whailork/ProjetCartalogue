package uqac.dim.projetcartalogue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.AllPermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class Cartalogue extends AppCompatActivity {
    CarteBD cbd;
    List<CarteModel> carteList;
    List<CarteModel> carteActuel;
    List<CarteModel> deckList;
    CarteDao carteDao;
    CardAdapter adapter;
    CardAdapter adapterDeck;
    EditText searchBar;
    Button btnAToZ, btnType, btnNo, btnStage, btnReset;
    ImageView imgArrow;
    boolean inversedFilter = false;
    Button currentFilterBtn = null;
    String currentSearch = "";
    int userId;
    RecyclerView recyclerView;


    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        recyclerView = findViewById(R.id.recyclerViewCarte);

        if (savedInstanceState != null) {
            // Récupérer les valeurs enregistrées
            userId = savedInstanceState.getInt("userId");
        }



        carteActuel = new ArrayList<>();
        cbd = CarteBD.getDataBase(getApplicationContext());
        carteDao = cbd.carteDao();
        carteList = new ArrayList<>();

        //on fetch les boutons des filtres, la search bar et le recycler view
        btnAToZ = findViewById(R.id.btnAToZ);
        btnAToZ.setOnClickListener(this::btnAToZClicked);
        btnType = findViewById(R.id.btnType);
        btnType.setOnClickListener(this::btnTypeClicked);
        btnNo = findViewById(R.id.btnNo);
        btnNo.setOnClickListener(this::btnNoClicked);
        btnStage = findViewById(R.id.btnStage);
        btnStage.setOnClickListener(this::btnStageClicked);
        searchBar = findViewById(R.id.searchBar);
        imgArrow = findViewById(R.id.imgArrow);
        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this::btnResetClicked);
        Button btnDeck = findViewById(R.id.btnDeck);

        btnDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CarteModel carteModel:carteActuel){
                    if (carteModel.isDeck()){
                        deckList.add(carteModel);
                    }
                }
                adapterDeck = new CardAdapter(deckList, getApplicationContext());
                recyclerView.setAdapter(adapterDeck);
            }
        });
        //set le listener de la search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                currentSearch = charSequence.toString();
                if(charSequence.toString().isEmpty() && currentFilterBtn == null){
                    btnReset.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.circle_background));
                }
                else{
                    btnReset.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.circle_filter_background));

                }
                //on check le filtre qui est entré et on fait la recherche selon le string
                if(currentFilterBtn.equals(btnAToZ)){
                    for (CarteModel c: carteList) {
                        
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        adapter = new CardAdapter(carteActuel, this);
        adapter.setOnClickListener(new CardAdapter.OnClickListener() {
            @Override
            public void onClick(int position, CarteModel model) {
                // copier collé de la fonction editCartePopup, c'est juste que je pouvais pas la rendre statci donc je la copy paste ici
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.edit_card_layout, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(searchBar, Gravity.CENTER, 0, 0);

                //on get les views pour editer et on leur set les bonnes valeurs
                ImageView pokemonImg = popupView.findViewById(R.id.pokemonImg);
                EditText numberEdit = popupView.findViewById(R.id.numberEdit);
                EditText nameEdit = popupView.findViewById(R.id.nameEdit);
                Spinner typeSpinner = popupView.findViewById(R.id.typeSpinner);
                EditText pvEdit = popupView.findViewById(R.id.PvEdit);
                CheckBox chkIsAlolan = popupView.findViewById(R.id.isAlolan);
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
                Button deleteBtn = popupView.findViewById(R.id.deleteBtn);


                // on set les valeurs des views
                pokemonImg.setImageBitmap(model.getImgBitmap());
                numberEdit.setText(model.getNumero());
                nameEdit.setText(model.getNom());
                //on set le selected item du spinner
                typeSpinner.setSelection(PokemonTypeColors.valueOf(model.type).ordinal());

                pvEdit.setText(model.getPv() + "");
                chkIsAlolan.setChecked(model.isAlolan());
                //on set le selected item du spinner
                List<String> stages = Arrays.asList(getResources().getStringArray(R.array.stages_array));
                if(model.getStage().matches("(?i)base") || model.getStage().matches("(?i)basic")  ){
                    model.setStage(stages.get(0));
                }
                if(model.getStage().matches("(?i)niveau\\s?1") || model.getStage().matches("(?i)stage\\s?1")){
                    model.setStage(stages.get(1));
                }
                if(model.getStage().matches("niveau\\s?2") || model.getStage().matches("(?i)stage\\s?2")){
                    model.setStage(stages.get(2));
                }
                stageSpinner.setSelection(stages.indexOf(model.getStage().toLowerCase()));

                evolvesFromEdit.setText(model.getEvolvesFrom());
                heightEdit.setText(model.getHeight());
                weightEdit.setText(model.getWeight());

                //on process l'attaque 1
                String[] split;
                split = model.attack1.split("\\|");
                if(split.length > 0){
                    attack1Name.setText(split[0]);
                }
                if(split.length > 1){
                    attack1Power.setText(split[1]);
                }

                if(split.length > 2){
                    attack1Desc.setText(split[1]);

                }


                //on process l'attaque 2
                split = model.attack2.split("\\|");

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
                split = model.attack3.split("\\|");

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
                split = model.attack4.split("\\|");

                if(split.length > 0){
                    attack4Name.setText(split[0]);
                }

                if(split.length > 1){
                    attack4Power.setText(split[1]);
                }

                if(split.length > 2){
                    attack4Desc.setText(split[2]);

                }

                descriptionEdit.setText(model.getDescription());

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //on fait la sauvegarde des données
                        //modelToEdit.setId(carteModel.hashCode());
                        model.setNumero(numberEdit.getText().toString());
                        model.setNom(nameEdit.getText().toString());
                        model.setType(typeSpinner.getSelectedItem().toString());
                        model.setPv(Integer.parseInt(pvEdit.getText().toString()));
                        model.setAlolan(chkIsAlolan.isActivated());
                        model.setStage(stageSpinner.getSelectedItem().toString());
                        model.setEvolvesFrom(evolvesFromEdit.getText().toString());
                        model.setHeight(heightEdit.getText().toString());
                        model.setWeight(weightEdit.getText().toString());
                        model.setDescription(descriptionEdit.getText().toString());

                        //on load les attaques
                        model.attack1 = attack1Name.getText().toString() + "|" + attack1Power.getText().toString()+"|"+attack1Desc.getText().toString();

                        model.attack2 = attack2Name.getText().toString() + "|" + attack2Power.getText().toString()+"|"+attack2Desc.getText().toString();

                        model.attack3 = attack3Name.getText().toString() + "|" + attack3Power.getText().toString()+"|"+attack3Desc.getText().toString();

                        model.attack4 = attack4Name.getText().toString() + "|" + attack4Power.getText().toString()+"|"+attack4Desc.getText().toString();


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                cbd.carteDao().delete(model);
                                cbd.carteDao().addCarte(model);
                            }
                        }).start();

                        popupWindow.dismiss();
                    }
                });

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cbd.carteDao().delete(model);
                        carteActuel.remove(model);
                        carteList.remove(model);
                        popupWindow.dismiss();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();

                    }
                });


            }
        });
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        carteDao.getAllCarte().observe(this, new Observer<List<CarteModel>>() {

            @Override
            public void onChanged(List<CarteModel> ca) {
                // Clear the existing list and add the new data from the database
                carteList.clear();  // Clear previous data
                if (ca != null) {
                    carteList.addAll(ca);  // Add new data
                }

                // Set the images for the cards
                for (CarteModel carteModel : carteList) {
                    Resources resources = getApplicationContext().getResources();
                    final int resourceId = resources.getIdentifier(carteModel.nom.toLowerCase(), "drawable", getApplicationContext().getPackageName());
                    if (resourceId == 0) {
                        // If image not found, set a default image (pokeball)
                        carteModel.imageId = resources.getIdentifier("poke", "drawable", getApplicationContext().getPackageName());
                    } else {
                        carteModel.imageId = resourceId;  // Set the resource image
                    }
                }
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }
        });

        // Aller chercher les carte qui on le même id de l'utilisateur et les mettres dans un nouvelle liste

        for (CarteModel carteModel:carteList){
            String[] user = carteModel.idUtilisateur.split("\\|");
            if (user[0].isEmpty()) {
                int id = Integer.parseInt(user[0]);
                if (id == userId) {
                    carteActuel.add(carteModel);
                }
            }
        }


    }

    public void btnAToZClicked(View view){
        Button btn = (Button)view;
        if(btn.equals(currentFilterBtn)){
            inversedFilter = !inversedFilter;
            //on toggle le sens du filtre
            if(inversedFilter){
                imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.downwards_arrow));
                carteList.sort(new Comparator(){

                    public int compare(Object o1, Object o2) {
                        CarteModel c1 = (CarteModel) o1;
                        CarteModel c2 = (CarteModel) o2;
                        return c2.getNom().compareToIgnoreCase(c1.getNom());
                    }
                });
            }
            else{
                imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.upwards_arrow));
                carteList.sort(new Comparator(){

                    public int compare(Object o1, Object o2) {
                        CarteModel c1 = (CarteModel) o1;
                        CarteModel c2 = (CarteModel) o2;
                        return c1.getNom().compareToIgnoreCase(c2.getNom());
                    }
                });
            }
        }
        else{
            inversedFilter = false;
            imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.upwards_arrow));
            if(currentFilterBtn != null){
                currentFilterBtn.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_unclicked));
            }
            currentFilterBtn = btn;
            btn.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_upwards));
            carteList.sort(new Comparator(){

                public int compare(Object o1, Object o2) {
                    CarteModel c1 = (CarteModel) o1;
                    CarteModel c2 = (CarteModel) o2;
                    return c1.getNom().compareToIgnoreCase(c2.getNom());
                }
            });
        }
        btnReset.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.circle_filter_background));
        adapter.notifyDataSetChanged();
    }

    public void btnTypeClicked(View view){
        Button btn = (Button)view;
        if(btn.equals(currentFilterBtn)){
            //on toggle le sens du filtre
            inversedFilter = !inversedFilter;
            if(inversedFilter){
                imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.downwards_arrow));
                carteList.sort(new Comparator(){

                    public int compare(Object o1, Object o2) {
                        CarteModel c1 = (CarteModel) o1;
                        CarteModel c2 = (CarteModel) o2;
                        return c2.getType().compareToIgnoreCase(c1.getType());
                    }
                });
            }
            else{
                imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.upwards_arrow));
                carteList.sort(new Comparator(){

                    public int compare(Object o1, Object o2) {
                        CarteModel c1 = (CarteModel) o1;
                        CarteModel c2 = (CarteModel) o2;
                        return c1.getType().compareToIgnoreCase(c2.getType());
                    }
                });
            }
        }
        else{
            if(currentFilterBtn != null){
                currentFilterBtn.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_unclicked));
            }
            inversedFilter = false;
            imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.upwards_arrow));
            currentFilterBtn = btn;
            btn.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_upwards));
            carteList.sort(new Comparator(){

                public int compare(Object o1, Object o2) {
                    CarteModel c1 = (CarteModel) o1;
                    CarteModel c2 = (CarteModel) o2;
                    return c1.getType().compareToIgnoreCase(c2.getType());
                }
            });
        }
        btnReset.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.circle_filter_background));
        adapter.notifyDataSetChanged();
    }

    public void btnNoClicked(View view){
        Button btn = (Button)view;
        if(btn.equals(currentFilterBtn)){
            //on toggle le sens du filtre
            inversedFilter = !inversedFilter;
            if(inversedFilter){
                imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.downwards_arrow));
                carteList.sort(new Comparator(){

                    public int compare(Object o1, Object o2) {
                        CarteModel c1 = (CarteModel) o1;
                        CarteModel c2 = (CarteModel) o2;
                        return c2.getNumero().compareToIgnoreCase(c1.getNumero());
                    }
                });
            }
            else{
                imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.upwards_arrow));
                carteList.sort(new Comparator(){

                    public int compare(Object o1, Object o2) {
                        CarteModel c1 = (CarteModel) o1;
                        CarteModel c2 = (CarteModel) o2;
                        return c1.getNumero().compareToIgnoreCase(c2.getNumero());
                    }
                });
            }

        }
        else{
            if(currentFilterBtn != null){
                currentFilterBtn.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_unclicked));
            }
            inversedFilter = false;
            imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.upwards_arrow));
            currentFilterBtn = btn;
            btn.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_upwards));
            carteList.sort(new Comparator(){

                public int compare(Object o1, Object o2) {
                    CarteModel c1 = (CarteModel) o1;
                    CarteModel c2 = (CarteModel) o2;
                    return c1.getNumero().compareToIgnoreCase(c2.getNumero());
                }
            });
        }
        btnReset.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.circle_filter_background));
        adapter.notifyDataSetChanged();
    }

    public void btnStageClicked(View view){
        Button btn = (Button)view;
        if(btn.equals(currentFilterBtn)){
            //on toggle le sens du filtre
            inversedFilter = !inversedFilter;
            if(inversedFilter){
                imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.downwards_arrow));
                carteList.sort(new Comparator(){

                    public int compare(Object o1, Object o2) {
                        CarteModel c1 = (CarteModel) o1;
                        CarteModel c2 = (CarteModel) o2;
                        return c2.getStage().compareToIgnoreCase(c1.getStage());
                    }
                });
            }
            else{
                imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.upwards_arrow));
                carteList.sort(new Comparator(){

                    public int compare(Object o1, Object o2) {
                        CarteModel c1 = (CarteModel) o1;
                        CarteModel c2 = (CarteModel) o2;
                        return c1.getStage().compareToIgnoreCase(c2.getStage());
                    }
                });
            }

        }
        else{
            if(currentFilterBtn != null){
                currentFilterBtn.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_unclicked));
            }
            inversedFilter = false;
            imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.upwards_arrow));
            currentFilterBtn = btn;
            btn.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_upwards));
            carteList.sort(new Comparator(){

                public int compare(Object o1, Object o2) {
                    CarteModel c1 = (CarteModel) o1;
                    CarteModel c2 = (CarteModel) o2;
                    return c1.getStage().compareToIgnoreCase(c2.getStage());
                }
            });
        }
        btnReset.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.circle_filter_background));
        adapter.notifyDataSetChanged();
    }

    public void btnResetClicked(View view){
        searchBar.setText("");
        currentSearch = "";

        inversedFilter = false;
        imgArrow.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.upwards_arrow));

        currentFilterBtn = null;
        btnAToZ.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_unclicked));
        btnType.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_unclicked));
        btnNo.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_unclicked));
        btnStage.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.btn_unclicked));

        btnReset.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.circle_background));
    }
}

