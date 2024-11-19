package uqac.dim.projetcartalogue;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cartalogue extends AppCompatActivity {
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCarte);
        List<CarteModel> listCarte = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            ArrayList<CarteModel> extra = getIntent().getParcelableExtra("carteList",ArrayList.class);
            System.out.println(extra);
            listCarte = extra;
        }
        //on set les images de pokemons
        for (CarteModel carteModel:listCarte) {
            Resources resources = getApplicationContext().getResources();
            final int resourceId = resources.getIdentifier(carteModel.nom.toLowerCase(), "drawable", getApplicationContext().getPackageName());
            if (resourceId == 0){
                //on set l'image par défaut
                carteModel.imageId = resources.getIdentifier("pokeball", "drawable", getApplicationContext().getPackageName());

            }
            else{
                carteModel.imageId = resourceId;
            }


        }


        //ne pas oublier de changer getData() pour la liste de cartes
        CardAdapter adapter = new CardAdapter(listCarte, this);
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

    }

    // carte test
    private List<CarteModel> getData()
    {

        List<CarteModel> list = new ArrayList<>();
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psy"));

        return list;
    }
}

