package ae.valeto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

//        List<String> tags = list.getTags();
//        holder.tagContainerLayout.setTags(tags);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}