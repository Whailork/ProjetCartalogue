package uqac.dim.projetcartalogue;

import android.os.Bundle;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Cartalogue extends AppCompatActivity {
    CardAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        List<CarteModel> listCarte = new ArrayList<>();
        listCarte = getData();
    }
}
// Sample data for RecyclerView
/*
private List<CarteModel> getData()
{

    List<CarteModel> list = new ArrayList<>();
    list.add(new CarteModel("First Exam",
            "May 23, 2015",
            "Best Of Luck"));
    list.add(new CarteModel("Second Exam",
            "June 09, 2015",
            "b of l"));
    list.add(new CarteModel("My Test Exam",
            "April 27, 2017",
            "This is testing exam .."));

    return list;
}*/