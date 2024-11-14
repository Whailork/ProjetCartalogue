package uqac.dim.projetcartalogue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
    List<CarteModel> listCarte = Collections.emptyList();
    Context context;

    public CardAdapter(List<CarteModel> list, Context context){
        this.listCarte = list;
        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View inflatedView = layoutInflater.inflate(R.layout.recycler_view_row,parent,false);
        CardViewHolder viewHolder = new CardViewHolder(inflatedView);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder viewHolder, final int position) {
        //viewHolder.imageCarte.setBackground(); trouver image par nom
        viewHolder.numero.setText(listCarte.get(position).numero);
        viewHolder.nomCarte.setText(listCarte.get(position).nom);
        viewHolder.typePokemon.setText(listCarte.get(position).type);;
    }

    @Override
    public int getItemCount() {
        return listCarte.size();
    }
    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
