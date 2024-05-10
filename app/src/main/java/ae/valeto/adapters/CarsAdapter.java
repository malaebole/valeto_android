package ae.valeto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.valeto.R;
import ae.valeto.models.Cars;
import ae.valeto.models.Parking;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.ViewHolder> {

    private final Context context;
    private final List<Cars> carsList;
    private ItemClickListener itemClickListener;

    private final String selectedId = "";

    private final int selectedIndex = -1;

    public CarsAdapter(Context context, List<Cars> carsList) {
        this.context = context;
        this.carsList = carsList;
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cars_adapter, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Cars car = carsList.get(position);

            holder.tvCarNumber.setText(car.getPlateNumber());

            if (!car.getBrand().getName().isEmpty()){
                if (!car.getName().isEmpty()){
                    holder.tvName.setText(car.getBrand().getName() + " ("+car.getName()+")");
                }else{
                    holder.tvName.setText(car.getBrand().getName());
                }
            }

            if (!car.getPhoto().isEmpty()){
                Glide.with(context).load(car.getPhoto()).into(holder.img);
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.itemClicked(v, holder.getAdapterPosition());
                    }
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.deleteClicked(v, holder.getAdapterPosition());
                    }
                }
            });

    }

    @Override
    public int getItemCount () {
        return carsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        ImageView img,btnDelete;
        TextView tvName, tvCarNumber;
        CardView layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCarNumber = itemView.findViewById(R.id.tv_car_number);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            layout = itemView.findViewById(R.id.rel_main);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void deleteClicked(View view, int pos);
    }

}
