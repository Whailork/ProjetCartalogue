package uqac.dim.projetcartalogue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonWriter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Cartalogue extends AppCompatActivity {
    CarteBD cbd;
    List<CarteModel> carteList;
    CarteDao carteDao;
    CardAdapter adapter;
    EditText searchBar;
    Button btnAToZ, btnType, btnNo, btnStage, btnReset;
    ImageView imgArrow;
    boolean inversedFilter = false;
    Button currentFilterBtn = null;
    String currentSearch = "";


    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

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
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCarte);

        adapter = new CardAdapter(carteList, this);
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
                        carteModel.imageId = resources.getIdentifier("pikachu", "drawable", getApplicationContext().getPackageName());
                    } else {
                        carteModel.imageId = resourceId;  // Set the resource image
                    }
                }

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }
        });


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

