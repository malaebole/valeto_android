package ae.valeto.dialogs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.valeto.R;
import ae.valeto.activities.CustomerMainTabsActivity;
import ae.valeto.databinding.FragmentDurationDialogBinding;
import ae.valeto.databinding.FragmentEditCarDialogBinding;
import ae.valeto.models.CarManufacturer;
import ae.valeto.models.Cars;
import ae.valeto.models.UserInfo;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCarDialogFragment extends DialogFragment implements View.OnClickListener{

    private FragmentEditCarDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private Boolean isUpdate;
    private List<CarManufacturer> carManufacturerList = new ArrayList<>();
    private List<Cars> carsList = new ArrayList<>();

    private int brandListItemId;
    private int pos;
    private final String photoFilePath = "";




    public EditCarDialogFragment() {
        // Required empty public constructor
    }

    public EditCarDialogFragment(Boolean isUpdate, int pos, List<CarManufacturer> carManufacturerList, List<Cars> carsList) {
        this.isUpdate = isUpdate;
        this.carManufacturerList = carManufacturerList;
        this.carsList = carsList;
        this.pos = pos;
    }

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditCarDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        if (isUpdate){
            binding.etCarName.setText(carsList.get(pos).getName());
            binding.etCarNumber.setText(carsList.get(pos).getPlateNumber());
            binding.etCarModel.setText(carsList.get(pos).getBrand().getName());
        }


        binding.etCarModel.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    @Override
    public void onClick(View v) {
        if (v == binding.etCarModel){
            CarManuFacturerSelectionListDialog dialog = new CarManuFacturerSelectionListDialog(getContext(), "Select Car Manufacturer");
            dialog.setLists(carManufacturerList);
            dialog.setShowSearch(true);
            dialog.setOnItemSelected(new CarManuFacturerSelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<CarManufacturer> expenseLists) {
                    CarManufacturer item = expenseLists.get(0);
                    binding.etCarModel.setText(item.getName());
                    brandListItemId =  item.getId();
                }
            });
            dialog.show();
        }

        else if (v == binding.btnConfirm) {
            if (binding.etCarNumber.getText().toString().isEmpty()){
                Functions.showToast(getActivity(), "Please enter car number plate", FancyToast.ERROR);
                return;
            }

            if (isUpdate){
                updateUserCar(true, String.valueOf(carsList.get(pos).getId()), binding.etCarName.getText().toString(), binding.etCarNumber.getText().toString(), String.valueOf(brandListItemId));

            }else{
                addUserCar(true, binding.etCarName.getText().toString(), binding.etCarNumber.getText().toString(), String.valueOf(brandListItemId));
            }

        }

    }


    private void updateUserCar(boolean isLoader, String carId, String name, String plateNumber, String brand) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        MultipartBody.Part filePart = null;
        if (!photoFilePath.isEmpty()) {
            File file = new File(photoFilePath);
            RequestBody fileReqBody = RequestBody.create(file, MediaType.parse("image/*"));
            filePart = MultipartBody.Part.createFormData("picture", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateUserCar(filePart,
                RequestBody.create(carId, MediaType.parse("multipart/form-data")),
                RequestBody.create(name, MediaType.parse("multipart/form-data")),
                RequestBody.create(plateNumber, MediaType.parse("multipart/form-data")),
                RequestBody.create(brand, MediaType.parse("multipart/form-data")),
                RequestBody.create("", MediaType.parse("multipart/form-data")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                            dialogCallback.didSubmitResult(EditCarDialogFragment.this, isUpdate);

                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
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
    private void addUserCar(boolean isLoader, String name, String plateNumber, String brand) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        MultipartBody.Part filePart = null;
        if (!photoFilePath.isEmpty()) {
            File file = new File(photoFilePath);
            RequestBody fileReqBody = RequestBody.create(file, MediaType.parse("image/*"));
            filePart = MultipartBody.Part.createFormData("picture", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addUserCar(filePart,
                RequestBody.create(name, MediaType.parse("multipart/form-data")),
                RequestBody.create(plateNumber, MediaType.parse("multipart/form-data")),
                RequestBody.create(brand, MediaType.parse("multipart/form-data")),
                RequestBody.create("", MediaType.parse("multipart/form-data")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                            dialogCallback.didSubmitResult(EditCarDialogFragment.this, isUpdate);

                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
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

    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, Boolean isUpdate);
    }

}