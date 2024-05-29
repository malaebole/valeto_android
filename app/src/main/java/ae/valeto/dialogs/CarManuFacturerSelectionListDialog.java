package ae.valeto.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

import ae.valeto.R;
import ae.valeto.adapters.BrandsSelectionListAdapter;
import ae.valeto.databinding.FragmentCarManuFacturerSelectionListDialogBinding;
import ae.valeto.models.CarManufacturer;


public class CarManuFacturerSelectionListDialog extends Dialog {

    private FragmentCarManuFacturerSelectionListDialogBinding binding;

    private final Context context;
    private OnItemSelected onItemSelected;
    private List<CarManufacturer> lists;
    private String title = "";
    private BrandsSelectionListAdapter adapter;
    private Boolean isShowSearch = false;
    private final List<CarManufacturer> filtered = new ArrayList<>();
    private boolean isSearchActive = false;

    public CarManuFacturerSelectionListDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CarManuFacturerSelectionListDialog(Context context, String title) {
        super(context);
        this.context = context;
        this.title = title;

    }

    public void setShowSearch(Boolean showSearch) {
        isShowSearch = showSearch;
    }

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    public void setLists(List<CarManufacturer> lists) {
        this.lists = lists;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = FragmentCarManuFacturerSelectionListDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (isShowSearch) {
            binding.searchVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.searchVu.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);

        adapter = new BrandsSelectionListAdapter(context, lists);
        adapter.setOnItemClickListener(new BrandsSelectionListAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                CarManufacturer item;
                if (isSearchActive) {
                    item = filtered.get(position);
                }
                else {
                    item = lists.get(position);
                }

                List<CarManufacturer> carManufacturerList = new ArrayList<>();
                carManufacturerList.add(item);
                onItemSelected.selectedItem(carManufacturerList);
                dismiss();

            }
        });
        binding.recyclerVu.setAdapter(adapter);
        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = binding.searchVu.getQuery().toString();
                if (query.equalsIgnoreCase("")) {
                    isSearchActive = false;
                    adapter.setDatasource(lists);
                }
                else {
                    filtered.clear();
                    for (int i = 0; i < lists.size(); i++) {
                        if (lists.get(i).getName().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(lists.get(i));
                        }
                    }
                    isSearchActive = true;
                    adapter.setDatasource(filtered);
                }
                return true;
            }
        });
        adapter.notifyDataSetChanged();


        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    public interface OnItemSelected {
        void selectedItem(List<CarManufacturer> carManufacturerList);
    }
}
