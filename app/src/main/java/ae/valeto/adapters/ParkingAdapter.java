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
import java.util.List;
import ae.valeto.R;
import ae.valeto.models.Parking;
import co.lujun.androidtagview.TagContainerLayout;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ViewHolder> {

    private final Context context;
    private final List<Parking> parkingList;
    private ItemClickListener itemClickListener;

    private String selectedId = "";

    private int selectedIndex = -1;

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

//
//        }else if (getItemViewType(position) == TYPE_Club_List){
//            CustomCLubNameHolder holder = (CustomCLubNameHolder)viewHolder;
//            Club club = clubList.get(position);
//            holder.tvName.setText(club.getName());
//            if (clubList.get(position).getId().equalsIgnoreCase("0")) {
//                holder.cardView.setStrokeColor(context.getResources().getColor(R.color.yellowColor));
//                holder.cardView.setCardBackgroundColor(Color.parseColor("#7A000000"));
//                holder.tvName.setTextColor(context.getResources().getColor(R.color.yellowColor));
//            }
//            else {
//                holder.cardView.setStrokeColor(Color.parseColor("#204334"));
//                holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);
//                holder.tvName.setTextColor(Color.parseColor("#204334"));
//            }
//            holder.cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clubNameClicked.clubNameClick(v, holder.getAdapterPosition());
//                }
//            });
//        }
//        else {
//        if (clubList.get(position).getId().equalsIgnoreCase(selectedId)) {
//            ClubViewHolder holder = (ClubViewHolder)viewHolder;
//            Club club = clubList.get(position);
//            if (!club.getCoverPath().isEmpty()) {
//                Glide.with(context).load(club.getCoverPath()).into(holder.imgBanner);
//            }
//            //holder.tvName.setText(club.getName());
//            holder.tvLoc.setText(club.getCity().getName());
//            if (club.getRating().isEmpty()) {
//                holder.tvRate.setText("0.0");
//            }
//            else {
//                holder.tvRate.setText(club.getRating());
//            }


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
        TextView tvPrice, tvLoc, tvRate, tvParkingNew;
        CardView layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBanner = itemView.findViewById(R.id.img_vu);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvLoc = itemView.findViewById(R.id.tv_loc);
            tvRate = itemView.findViewById(R.id.tv_rate);
            layout = itemView.findViewById(R.id.rel_main);
            tvParkingNew = itemView.findViewById(R.id.tv_parking_name);

        }
    }



        public interface ItemClickListener {
            void itemClicked(View view, int pos);
        }

}
