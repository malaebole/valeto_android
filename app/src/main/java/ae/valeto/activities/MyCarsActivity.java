package ae.valeto.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ae.valeto.R;
import ae.valeto.adapters.CarsAdapter;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityMyCarsBinding;
import ae.valeto.dialogs.ChangePassDialogFragment;
import ae.valeto.dialogs.EditCarDialogFragment;
import ae.valeto.models.CarManufacturer;
import ae.valeto.models.Cars;
import ae.valeto.models.MyTicket;
import ae.valeto.models.Parking;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCarsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMyCarsBinding binding;
    private CarsAdapter carsAdapter;
    private final List<Cars> carsList = new ArrayList<>();
    private Boolean isUpdate;
    private List<CarManufacturer> carManufacturerList = new ArrayList<>();
    private int position;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyCarsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        LinearLayoutManager carsListLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.carsRecyclerVu.setLayoutManager(carsListLayoutManager);
        carsAdapter = new CarsAdapter(getContext(), carsList);
        carsAdapter.setItemClickListener(itemClickListener);
        binding.carsRecyclerVu.setAdapter(carsAdapter);

        binding.btnBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);

        getCarManufacturerList();
    }


    CarsAdapter.ItemClickListener itemClickListener = new CarsAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            position = pos;
            AddCarClicked(true);

        }

        @Override
        public void deleteClicked(View view, int pos) {

            deleteUserCar(true, carsList.get(pos).getId());

        }

    };


    private void getUserCars(boolean isLoader) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        call = AppManager.getInstance().apiInterface.getUserCars();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            carsList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject carObject = arr.getJSONObject(i);
                                Cars car = gson.fromJson(carObject.toString(), Cars.class);
                                carsList.add(car);
                                carsAdapter.notifyDataSetChanged();
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
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
        else if (v == binding.btnAdd) {
            AddCarClicked(false);
        }
    }

    private void AddCarClicked(Boolean isUpdate) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("EditCarDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        EditCarDialogFragment dialogFragment = new EditCarDialogFragment(isUpdate, position, carManufacturerList, carsList);
        dialogFragment.setDialogCallback((df, update) -> {
            df.dismiss();
            if (update){
                Functions.showToast(getContext(),"Car updated Successfully!", FancyToast.SUCCESS);
                getUserCars(carsList.isEmpty());
            }else{
                Functions.showToast(getContext(),"Car Added Successfully!", FancyToast.SUCCESS);
                getUserCars(carsList.isEmpty());
            }

        });
        dialogFragment.show(fragmentTransaction, "EditCarDialogFragment");

    }


    @Override
    protected void onResume() {
        super.onResume();

        getUserCars(carsList.isEmpty());

    }

    private void getCarManufacturerList() {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getCarManufacturerList();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray data = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {
                                carManufacturerList.add(gson.fromJson(data.get(i).toString(), CarManufacturer.class));
                            }
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
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });

    }

    private void deleteUserCar(boolean isLoader, int carId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteUserCar(carId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(),"Car Deleted Successfully!", FancyToast.SUCCESS);
                            removeCarFromList(carId);


                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
            }
        });
    }

    private void removeCarFromList(int carId) {
        for (Iterator<Cars> iterator = carsList.iterator(); iterator.hasNext(); ) {
            Cars car = iterator.next();
            if (car.getId() == carId) {
                iterator.remove();
                carsAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

}