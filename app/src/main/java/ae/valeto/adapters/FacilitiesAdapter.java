package ae.valeto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import ae.valeto.R;
import ae.valeto.models.Facilities;
import co.lujun.androidtagview.TagContainerLayout;

public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.ViewHolder> {

    private final Context context;
    private final List<Facilities> list;
    private ItemClickListener itemClickListener;

    public FacilitiesAdapter(Context context, List<Facilities> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facilities_chips, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        TagContainerLayout tagContainerLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagContainerLayout = itemView.findViewById(R.id.tag);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}