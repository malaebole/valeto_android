package ae.valeto.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.valeto.R;
import ae.valeto.adapters.ClosedTicketsAdapter;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityCloseTicketBinding;
import ae.valeto.models.CarManufacturer;
import ae.valeto.models.ClosedTickets;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        getUserClosedTickets(true);

        LinearLayoutManager closedTicketsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.closeTicketsRecyclerVu.setLayoutManager(closedTicketsLayoutManager);
        closedTicketsAdapter = new ClosedTicketsAdapter(getContext(), closedTicketsList);
        closedTicketsAdapter.setItemClickListener(itemClickListener);
        binding.closeTicketsRecyclerVu.setAdapter(closedTicketsAdapter);

        binding.btnBack.setOnClickListener(this);


    }

    ClosedTicketsAdapter.ItemClickListener itemClickListener = new ClosedTicketsAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), ClosedTicketDetailActivity.class);
            intent.putExtra("ticket_id", closedTicketsList.get(pos).getId());
            startActivity(intent);

        }
    };

    private void getUserClosedTickets(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserClosedTickets();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray data = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {
                                closedTicketsList.add(gson.fromJson(data.get(i).toString(), ClosedTickets.class));
                            }
                            closedTicketsAdapter.notifyDataSetChanged();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }

    }
}