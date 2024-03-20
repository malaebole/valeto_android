package ae.valeto.adapters;

import android.content.Context;
import android.graphics.Typeface;
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
import ae.valeto.models.NotificationList;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private final Context context;
    private final List<NotificationList> list;
    private ItemClickListener itemClickListener;

    public NotificationListAdapter(Context context, List<NotificationList> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_ist, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationList notification = list.get(position);


        holder.tvTime.setText(notification.getCreatedAt());
        holder.tvTitle.setText(notification.getMessage());

        if (notification.getIsRead()) {
//            holder.tvTitle.setTypeface(holder.tvTitle.getTypeface(), Typeface.NORMAL);
            holder.isRead.setBackground(context.getResources().getDrawable(R.drawable.offline_circle));
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.subTextColor));
            holder.tvTime.setTextColor(context.getResources().getColor(R.color.subTextColor));
        }
        else {
//            holder.tvTitle.setTypeface(holder.tvTitle.getTypeface(), Typeface.BOLD);
            holder.isRead.setBackground(context.getResources().getDrawable(R.drawable.online_circle));
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.blackColor));
            holder.tvTime.setTextColor(context.getResources().getColor(R.color.blackColor));

        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.itemClicked(view, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvTime;
        CardView layout;
        ImageView isRead;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            layout = itemView.findViewById(R.id.rel_main);
            isRead = itemView.findViewById(R.id.is_read);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}