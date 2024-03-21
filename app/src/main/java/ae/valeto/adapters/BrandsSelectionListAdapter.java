package ae.valeto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ae.valeto.R;
import ae.valeto.models.CarManufacturer;

public class BrandsSelectionListAdapter extends RecyclerView.Adapter<BrandsSelectionListAdapter.ViewHolder>{


    private final Context context;
    private ItemClickListener onItemClickListener;
    private List<CarManufacturer> list;
    private final List<CarManufacturer> carManufacturerList = new ArrayList<>();

    public void setOnItemClickListener(ItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public BrandsSelectionListAdapter(Context context, List<CarManufacturer> list) {
        this.context = context;
        this.list = list;
    }

    public void setDatasource(List<CarManufacturer> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<CarManufacturer> getCarManufacturerList() {
        return carManufacturerList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.brands_selection_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CarManufacturer listItem = list.get(position);
        holder.tvTitle.setText(listItem.getName());


        holder.rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClickListener(v, holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        ImageView imgVu;
        RelativeLayout rel;

        ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            imgVu = itemView.findViewById(R.id.img_vu);
            rel = itemView.findViewById(R.id.rel);

        }
    }

    public interface ItemClickListener {
        void onItemClickListener(View view, int position);
    }
}
