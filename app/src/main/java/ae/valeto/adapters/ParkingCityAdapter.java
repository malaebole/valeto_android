package ae.valeto.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.valeto.R;
import ae.valeto.models.ParkingCity;

public class ParkingCityAdapter extends RecyclerView.Adapter<ParkingCityAdapter.ViewHolder> {

    private final Context context;
    private final List<ParkingCity> list;
    private ItemClickListener itemClickListener;
    private int selectedId;
    private int selectedIndex;

    public ParkingCityAdapter(Context context, List<ParkingCity> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedId(int selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_city_name_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParkingCity parkingCity = list.get(position);
        holder.tvName.setText(parkingCity.getName());

        if (parkingCity.getId() == selectedId) {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.yellowColor) );
            holder.cardView.setCardBackgroundColor(Color.parseColor("#25A297"));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.yellowColor));
        }
        else {
            holder.cardView.setStrokeColor(Color.parseColor("#005F56"));
            holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);
            holder.tvName.setTextColor(Color.parseColor("#25A297"));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            cardView = itemView.findViewById(R.id.card_vu);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}