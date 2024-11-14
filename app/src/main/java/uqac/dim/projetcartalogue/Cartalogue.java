package uqac.dim.projetcartalogue;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Cartalogue extends AppCompatActivity {
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCarte);
        List<CarteModel> listCarte = new ArrayList<>();
        listCarte = getData();

        CardAdapter adapter = new CardAdapter(listCarte, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // carte test
    private List<CarteModel> getData()
    {

        List<CarteModel> list = new ArrayList<>();
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));
        list.add(new CarteModel(R.drawable.kadabra,"100", "Kadabra", "Psychic"));

        return list;
    }
}

