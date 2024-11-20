package uqac.dim.projetcartalogue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.JsonWriter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cartalogue extends AppCompatActivity {
    CarteBD cbd;
    List<CarteModel> carteList;
    CarteDao carteDao;
    CardAdapter adapter;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        cbd = CarteBD.getDataBase(getApplicationContext());
        carteDao = cbd.carteDao();
        carteList = new ArrayList<>();

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
}

