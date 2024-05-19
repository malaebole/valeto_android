package ae.valeto.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.transition.Hold;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.valeto.R;
import ae.valeto.models.ClosedTickets;
import ae.valeto.models.Facilities;
import co.lujun.androidtagview.TagContainerLayout;

public class ClosedTicketsAdapter extends RecyclerView.Adapter<ClosedTicketsAdapter.ViewHolder> {

    private final Context context;
    private final List<ClosedTickets> list;
    private ItemClickListener itemClickListener;

    public ClosedTicketsAdapter(Context context, List<ClosedTickets> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.closed_tickets, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClosedTickets ticket = list.get(position);

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy hh:mma", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date startTime = inputFormat.parse(ticket.getStartTime());
            String formattedDate = outputFormat.format(startTime);

            holder.tvDate.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tvParkingName.setText(ticket.getParking().getName());
        holder.tvTime.setText(ticket.getDuration());
        holder.tvCarNumber.setText(ticket.getCar().getPlateNumber());
        holder.tvPrice.setText(ticket.getCurrency() + " " +ticket.getPaidAmount());
        if (ticket.getDiscount() != 0){
            holder.tvDiscount.setVisibility(View.VISIBLE);
            holder.tvDiscount.setText("Discount: "+ticket.getDiscount() + " " +ticket.getCurrency());
        }else{
            holder.tvDiscount.setVisibility(View.GONE);
        }

//        holder.tvStatus.setText("Closed");
//        holder.cardView.setStrokeColor(Color.parseColor("#C82333"));
//        holder.cardView.setCardBackgroundColor(Color.parseColor("#FADDE0"));
//        holder.tvStatus.setTextColor(Color.parseColor("#C82333"));


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
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvParkingName, tvTime, tvDate, tvCarNumber, tvPrice, tvStatus, tvDiscount;

        RelativeLayout layout;
        MaterialCardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvParkingName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCarNumber = itemView.findViewById(R.id.tv_car_number);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            layout = itemView.findViewById(R.id.ticket_vu);
            cardView = itemView.findViewById(R.id.card_vu_status);
            tvDiscount = itemView.findViewById(R.id.tv_discount);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}