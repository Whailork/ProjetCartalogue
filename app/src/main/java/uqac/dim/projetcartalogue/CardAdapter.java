package uqac.dim.projetcartalogue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
    List<CarteModel> listCarte = Collections.emptyList();
    Context context;
    private OnClickListener onClickListener;

    public CardAdapter(List<CarteModel> list, Context context){
        this.listCarte = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View inflatedView = layoutInflater.inflate(R.layout.recycler_view_row,parent,false);


        return new CardViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder viewHolder, final int position) {
        viewHolder.imageCarte.setImageResource(listCarte.get(position).imageId);
        viewHolder.numero.setText(listCarte.get(position).numero);
        viewHolder.nomCarte.setText(listCarte.get(position).nom);
        viewHolder.typePokemon.setText(listCarte.get(position).type);
        viewHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(context,context.getResources().getIdentifier(viewHolder.typePokemon.getText().toString(), "color", context.getPackageName())));
        viewHolder.itemView.setOnClickListener(view->{
            if(onClickListener != null){
                onClickListener.onClick(position,listCarte.get(position));
            }
        });

    }


    @Override
    public int getItemCount() {
        return listCarte.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Setter for the click listener
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    // Interface for the click listener
    public interface OnClickListener {
        void onClick(int position, CarteModel model);
    }
}
