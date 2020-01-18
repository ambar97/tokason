package com.pratamatechnocraft.tokason.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pratamatechnocraft.tokason.DetailUserActivity;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemDataUser;
import com.pratamatechnocraft.tokason.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecycleViewDataUser extends RecyclerView.Adapter<AdapterRecycleViewDataUser.ViewHolder> implements Filterable {

    private List<ListItemDataUser> listItemDataUsers;
    private List<ListItemDataUser> listItemDataUserFull;
    private Context context;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();

    public AdapterRecycleViewDataUser(List<ListItemDataUser> listItemDataUsers, Context context) {
        this.listItemDataUsers = listItemDataUsers;
        this.context = context;
        listItemDataUserFull = new ArrayList<>( listItemDataUsers );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_item_data_user,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListItemDataUser listItemDataUser = listItemDataUsers.get(position);

        holder.txtNamaUser.setText(listItemDataUser.getNamaUser());
        holder.txtNoTelp.setText(listItemDataUser.getNoTelp());
        holder.txtLevelUser.setText(listItemDataUser.getLevelUser());

        holder.cardViewDataUser.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailUserActivity.class);
                i.putExtra("kdUser", listItemDataUser.getKdUser());
                context.startActivity(i);
            }
        });

        if (!listItemDataUser.getFotoUser().equals("")){
            holder.adaGambarUser.setVisibility( View.VISIBLE );
            holder.tidakAdaGambarUser.setVisibility( View.GONE );
            Glide.with(context)
                    // LOAD URL DARI INTERNET
                    .load(baseUrl+listItemDataUser.getFotoUser())
                    // LOAD GAMBAR AWAL SEBELUM GAMBAR UTAMA MUNCUL, BISA DARI LOKAL DAN INTERNET
                    .into(holder.fotoDataUser1);
        }else {
            holder.adaGambarUser.setVisibility( View.GONE );
            holder.tidakAdaGambarUser.setVisibility( View.VISIBLE );
        }
        String namaDepan=listItemDataUser.getNamaUser();
        holder.hurufDepanDataUser.setText(namaDepan.substring( 0,1 ));

        int color=0;

        if (holder.hurufDepanDataUser.getText().equals( "A" ) || holder.hurufDepanDataUser.getText().equals( "a" )){
            color=R.color.amber_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "B" ) || holder.hurufDepanDataUser.getText().equals( "b" )){
            color=R.color.blue_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "C" ) || holder.hurufDepanDataUser.getText().equals( "c" )){
            color=R.color.blue_grey_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "D" ) || holder.hurufDepanDataUser.getText().equals( "d" )){
            color=R.color.brown_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "E" ) || holder.hurufDepanDataUser.getText().equals( "e" )){
            color=R.color.cyan_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "F" ) || holder.hurufDepanDataUser.getText().equals( "f" )){
            color=R.color.deep_orange_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "G" ) || holder.hurufDepanDataUser.getText().equals( "g" )){
            color=R.color.deep_purple_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "H" ) || holder.hurufDepanDataUser.getText().equals( "h" )){
            color=R.color.green_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "I" ) || holder.hurufDepanDataUser.getText().equals( "i" )){
            color=R.color.grey_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "J" ) || holder.hurufDepanDataUser.getText().equals( "j" )){
            color=R.color.indigo_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "K" ) || holder.hurufDepanDataUser.getText().equals( "k" )){
            color=R.color.teal_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "L" ) || holder.hurufDepanDataUser.getText().equals( "l" )){
            color=R.color.lime_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "M" ) || holder.hurufDepanDataUser.getText().equals( "m" )){
            color=R.color.red_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "N" ) || holder.hurufDepanDataUser.getText().equals( "n" )){
            color=R.color.light_blue_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "O" ) || holder.hurufDepanDataUser.getText().equals( "o" )){
            color=R.color.light_green_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "P" ) || holder.hurufDepanDataUser.getText().equals( "p" )){
            color=R.color.orange_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "Q" ) || holder.hurufDepanDataUser.getText().equals( "q" )){
            color=R.color.pink_500;
        }else if(holder.hurufDepanDataUser.getText().equals( "R" ) || holder.hurufDepanDataUser.getText().equals( "r" )){
            color=R.color.red_600;
        }else if(holder.hurufDepanDataUser.getText().equals( "S" ) || holder.hurufDepanDataUser.getText().equals( "s" )){
            color=R.color.yellow_600;
        }else if(holder.hurufDepanDataUser.getText().equals( "T" ) || holder.hurufDepanDataUser.getText().equals( "t" )){
            color=R.color.blue_600;
        }else if(holder.hurufDepanDataUser.getText().equals( "U" ) || holder.hurufDepanDataUser.getText().equals( "u" )){
            color=R.color.cyan_600;
        }else if(holder.hurufDepanDataUser.getText().equals( "V" ) || holder.hurufDepanDataUser.getText().equals( "v" )){
            color=R.color.green_600;
        }else if(holder.hurufDepanDataUser.getText().equals( "W" ) || holder.hurufDepanDataUser.getText().equals( "w" )){
            color=R.color.purple_600;
        }else if(holder.hurufDepanDataUser.getText().equals( "X" ) || holder.hurufDepanDataUser.getText().equals( "x" )){
            color=R.color.pink_600;
        }else if(holder.hurufDepanDataUser.getText().equals( "Y" ) || holder.hurufDepanDataUser.getText().equals( "y" )){
            color=R.color.lime_600;
        }else if(holder.hurufDepanDataUser.getText().equals( "Z" ) || holder.hurufDepanDataUser.getText().equals( "z" )){
            color=R.color.orange_600;
        }

        holder.fotoDataUser.setImageResource(color);
    }

    @Override
    public int getItemCount() {
        return listItemDataUsers.size();
    }

    @Override
    public Filter getFilter() {
        return listItemFilter;
    }

    private Filter listItemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ListItemDataUser> filteredList = new ArrayList<>(  );

            if (charSequence == null || charSequence.length()==0){
                filteredList.addAll( listItemDataUserFull );
            }else{
              String filterPattern = charSequence.toString().toLowerCase().trim();

              for (ListItemDataUser itemDataUser : listItemDataUserFull){
                  if (itemDataUser.getNamaUser().toLowerCase().contains( filterPattern ) || itemDataUser.getLevelUser().toLowerCase().contains( filterPattern )){
                      filteredList.add( itemDataUser );
                  }
              }
            }

            FilterResults results = new FilterResults();
            results.values=filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listItemDataUsers.clear();
            listItemDataUsers.addAll((List) filterResults.values );
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNamaUser, txtNoTelp, txtLevelUser, hurufDepanDataUser;
        public CardView cardViewDataUser;
        public CircleImageView fotoDataUser, fotoDataUser1;
        public RelativeLayout adaGambarUser, tidakAdaGambarUser;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNamaUser = (TextView) itemView.findViewById(R.id.txtNamaUser);
            txtNoTelp = (TextView) itemView.findViewById(R.id.txtNoTelp);
            txtLevelUser = (TextView) itemView.findViewById(R.id.txtLevelUser);
            cardViewDataUser = (CardView) itemView.findViewById(R.id.cardViewDataUser);
            hurufDepanDataUser = (TextView) itemView.findViewById(R.id.hurufDepanUser);
            fotoDataUser = (CircleImageView) itemView.findViewById( R.id.fotoDataUser );
            fotoDataUser1 = (CircleImageView) itemView.findViewById( R.id.fotoDataUser1 );
            adaGambarUser = (RelativeLayout) itemView.findViewById( R.id.adaGambarUser );
            tidakAdaGambarUser = (RelativeLayout) itemView.findViewById( R.id.tidakAdaGambarUser );
        }
    }



}
