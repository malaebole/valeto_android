package ae.valeto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import ae.valeto.R;
import ae.valeto.models.Parking;
import co.lujun.androidtagview.TagContainerLayout;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ViewHolder> {

    private final Context context;
    private final List<Parking> parkingList;
    private ItemClickListener itemClickListener;

    private final String selectedId = "";

    private final int selectedIndex = -1;

    public ParkingAdapter(Context context, List<Parking> parkingList) {
        this.context = context;
        this.parkingList = parkingList;
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_parking_list_adapter, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Parking parking = parkingList.get(position);
        if (!parking.getPhoto().isEmpty()){
            Glide.with(context).load(parking.getPhoto()).into(holder.imgBanner);
        }
        if (String.valueOf(parking.getIsFixedPrice()).equalsIgnoreCase("1")){
            holder.tvPrice.setText(parking.getCurrency()+" "+ parking.getPrice() + " ");
        }else{
            holder.tvPrice.setText(parking.getCurrency()+" "+parking.getPrice() + "/hr");
        }
        holder.tvLoc.setText(parking.getDistance() +" "+ parking.getLocation());
        holder.tvParkingName.setText(parking.getName());

        if (parking.getRating() > 0){
            holder.rateVu.setVisibility(View.VISIBLE);
            holder.tvRate.setText(String.valueOf(parking.getRating()));
        }else{
            holder.rateVu.setVisibility(View.GONE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });


        }


        @Override
        public int getItemCount () {
            return parkingList.size();
        }


    class ViewHolder extends RecyclerView.ViewHolder{


        ImageView imgBanner;
        TextView tvPrice, tvLoc, tvRate, tvParkingName;
        CardView layout, rateVu;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBanner = itemView.findViewById(R.id.img_vu);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvLoc = itemView.findViewById(R.id.tv_loc);
            tvRate = itemView.findViewById(R.id.tv_rate);
            layout = itemView.findViewById(R.id.rel_main);
            tvParkingName = itemView.findViewById(R.id.tv_parking_name);
            rateVu = itemView.findViewById(R.id.rate_vu);

        }
    }



        public interface ItemClickListener {
            void itemClicked(View view, int pos);
        }

}
