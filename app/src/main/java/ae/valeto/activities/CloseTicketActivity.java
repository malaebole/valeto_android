package ae.valeto.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ae.valeto.R;
import ae.valeto.adapters.ClosedTicketsAdapter;
import ae.valeto.adapters.FacilitiesAdapter;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityCloseTicketBinding;
import ae.valeto.models.ClosedTickets;
import ae.valeto.models.Facilities;

public class CloseTicketActivity extends BaseActivity implements View.OnClickListener {


    private ActivityCloseTicketBinding binding;

    private final List<ClosedTickets> closedTicketsList = new ArrayList<>();
    private ClosedTicketsAdapter closedTicketsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCloseTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();


        LinearLayoutManager closedTicketsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.closeTicketsRecyclerVu.setLayoutManager(closedTicketsLayoutManager);
        closedTicketsAdapter = new ClosedTicketsAdapter(getContext(), closedTicketsList);
        closedTicketsAdapter.setItemClickListener(itemClickListener);
        binding.closeTicketsRecyclerVu.setAdapter(closedTicketsAdapter);


    }

    ClosedTicketsAdapter.ItemClickListener itemClickListener = new ClosedTicketsAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }

    }
}