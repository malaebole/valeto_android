package ae.valeto.util;

import java.util.ArrayList;

import ae.valeto.api.APIClient;
import ae.valeto.api.APIInterface;


public class AppManager {
    private static final AppManager ourInstance = new AppManager();
    public static AppManager getInstance() {
        return ourInstance;
    }

    public APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);


//    public List<OleCountry> countries = new ArrayList<>();
//    public List<Club> clubs = new ArrayList<>();
//    public List<OleClubFacility> clubFacilities = new ArrayList<>();
//    public OleFieldData oleFieldData = null;
    public int notificationCount = 0;

    private AppManager() {
    }
}
