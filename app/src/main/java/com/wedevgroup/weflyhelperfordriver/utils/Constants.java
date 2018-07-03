package com.wedevgroup.weflyhelperfordriver.utils;

/**
 * Created by admin on 21/03/2018.
 */

public class Constants {

    // APP NAME
    public static final String APP_NAME         = "WeflyHelperForDriver";
    public static final String PATH             = "weflyhelperfordriver";

    public static final String API_URL                              =  "http://192.168.1.224:8000/";
    public static final String API_URL_NO_SEPARATOR                 =  "http://192.168.1.224:8000";

    //public static final String API_URL =  "http://217.182.133.143:8004/";

    public static final String BASE_URL                             =  API_URL + "geoloc/";

    public static final String SCHEDULE_URL                         =  BASE_URL + "chaufdrone/";


    // REST API
    public static final String LOGIN_URL                            = API_URL +"dronepilote-auth/";

    public static final String USER_PROFILE                         = BASE_URL + "profile/";

    //GET
    public static final String PARCELLES_DOWNLOAD_LEFT_URL          = SCHEDULE_URL;
    public static final String PARCELLES_DOWNLOAD_RIGHT_URL         = "polygone/";

    //GET
    public static final String COMPANIES_DOWNLOAD_URL               = BASE_URL + "entreprise/";

    //POST
    public static final String POST_PARCELLES_ITEM_URL              = SCHEDULE_URL;

    //GET
    public static final String RECOVERY_LEFT_URL                    = SCHEDULE_URL;
    public static final String RECOVERY_RIGHT_URL                   = "recupschedule/";

    // File extension
    public static final String FILE_EXTENSION                         = ".mp4";
    public static final String VIDEO_TUTO_URL                         = "https://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4";





    //GET
    public static final String CULTURES_URL                         = BASE_URL + "culture/";

    //GET
    public static final String SEPARATOR                            =  "/";

    //GET
    public static final String CULTURE_TYPE_URL                     = BASE_URL + "type-culture/";

    //GET
    public static final String REGIONS_URL                          = BASE_URL + "region/";
    // GET
    public static final String CONSTANTS_UPDATE_URL                 = BASE_URL + "version/version_driver/";


    // NETWORK minimal accuracy
    public static final float  MIN_ACCURACY                  = 100;
    public static final String STATE_PARCELLE               = PATH +".state.parcelle" ;
    public static final String STATE_IS_EDIT_MODE           = PATH +".state.isedit" ;
    public static final String STATE_PARCELS_LIST           = PATH + ".state.parcels.list" ;
    public static final String STATE_COMPANIES_LIST         = PATH + ".state.company.list" ;
    public static final int    PREF_APP_VERSION             = 1 ;


    //Util
    public static final double DOUBLE_NULL = 0.0d;

    // Map
    public static final int MAP_MIN_ZOOM_PREFERENCE = 16;

    //Preference
    public static final String SAVE_PREFERENCE_NAME = "WeflyHelperfordriverSave";
    public static final String SAVE_DIRECTORY_NAME = "WeflyHelperForDriverCache";

    public static final String PREF_CULTURE_LIST                      = PATH+ ".cultures";
    public static final String PREF_TYPE_CULTURE_LIST                 = PATH +".typescultures";
    public static final String PREF_REGION                            = PATH+".regions";
    public static final String PREF_DATABASE_VERSION_DATE             = PATH+".dateloaded";
    public static final String PREF_TOKEN                             = PATH+".token2";
    public static final String PREF_LOADER_IS_LOADED                  = PATH+ ".loader";
    public static final String PREF_IS_FIRST_LAUNCH                   = PATH+".isfirstlaunch";
    public static final String PREF_USER_NAME                         = PATH + ".user.name";
    public static final String PREF_USER_PASSWORD                     = PATH + ".user.password";
    public static final String PREF_USER_LAST_NAME                    = PATH + ".user.last.name";
    public static final String PREF_USER_LAST_PASSWORD                = PATH + ".user.last.password";
    public static final String PREF_IS_SAME_USER                      = PATH + ".user.same";
    public static final String STATE_USER_NAME                        = PATH + ".state.user.name";
    public static final String STATE_PROFILE                          = PATH + ".state.profile";
    public static final String STATE_USER_PASSWORD                    = PATH + ".state.user.password";
    public static final String PREF_IS_LANGUAGE_FRENCH                = PATH + ".islanguagefrench";


    // Language
    public static final String LANGUAGE_FRENCH                        = "fr";
    public static final String LANGUAGE_ENGLISH                       = "en";
    public static final String LANGUAGE_IS_CHANGING                   = PATH + ".changing";

    //DB
    public static final String DATABASE_NAME = "weflyhelperfordriverdb";
    public static final int DATABASE_VERSION = 1;

    // BACKGROUND TASK
    public static final String PRESENTER_LOADER_STOP_TASK_IS_ENABLE         = PATH +".loader_presenter_gb";
    public static final String THREAD_AUTO_POST_IS_RUNNING                  = PATH+".thread_auto_post";
    public static final String THREAD_CALENDAR_NOTIF_IS_RUNNING             = PATH+".thread_notification";
    public static final String LAST_NOTIFICATION_DATE                       = PATH+".notif_last_date";


    //NetWork
    public static final String SERVER_ERROR = "error";
    public static final String RESPONSE_EMPTY = "{\"reponse\":[]}";
    public static final String RESPONSE_EMPTY_OTHER = "[]";
    public static final String RESPONSE_ERROR_HTML = "<html>";
    public static final String DEFAULT_DISTANCE = " Non disponible";
    public static final String RESPONSE_CODE_UPLOAD_OK = "up_ok";
    public static final String RESPONSE_CODE_ERROR_UPLOAD = "error_up";
    public static final String RESPONSE_CODE_ERROR_PERMISSION = "error_perm";
    public static final String RESPONSE_CODE_ERROR_NO_INTERNET = "error_net";
    public static final String RESPONSE_EMPTY_INPUT = "non_field_errors";

    // Animation Delai
    public static final int ANIMATION_RIPPLE_DELAI = 200;
    public static final long RIPPLE_DURATION = 250;

    //Permission
    public static final int PERMISSIONS_REQUEST = 100;
    public static final int ENABLE_LOCATION_SERVICES_REQUEST = 101;
    public static final int GOOGLE_PLAY_SERVICES_ERROR_DIALOG = 102;
    public static final int REQUEST_READ_SMS_PERMISSION = 3004;
    public static final int REQUEST_GROUP_PERMISSION = 425;
    public static final int REQUEST_APP_PERMISSION = 425;

    // Progress
    public static final int PROGRESS_CULTURE_TYPE_VALUE = 30;
    public static final int PROGRESS_CULTURE_VALUE = 80;
    public static final int PROGRESS_REGION_VALUE = 95;
    public static final int PROGRESS_DATE_VALUE = 100;
    public static final int PROGRESS_DEFAULT_VALUE = 0;

    //NOTIFICATION
    public static final int NOTIFICATION_SERVICE_AUTO_POST_ID = 98766;

    //State Restore
    public static final String PRESENTER_DRAWING                                = PATH +".drawing_presenter";
    public static final String PRESENTER_DRAWING_POINTS                         = PATH +".drawing_points";
    public static final String PRESENTER_LOADER_OLD_IS_CULTURE_LOADED           = PATH +".loader_is_culture";
    public static final String PRESENTER_LOADER_OLD_IS_CULT_TYPE_LOADED         = PATH +".loader_is_cult_type";
    public static final String PRESENTER_LOADER_OLD_IS_REGION_LOADED            = PATH +".loader_is_region";
    public static final String PRESENTER_LOADER_OLD_IS_DATE_LOADED              = PATH +".loader_is_date";

    //Zone
    public static final int ZONE_A = 2;
    public static final int ZONE_B = 4;
    public static final int ZONE_C = 6;
    public static final int ZONE_D = 8;
    public static final int ZONE_E = 9;

    public static final String ZONE_A_STRING = "Zone A";
    public static final String ZONE_B_STRING = "Zone B";
    public static final String ZONE_C_STRING = "Zone C";
    public static final String ZONE_D_STRING = "Zone D";
    public static final String ZONE_E_STRING = "Zone E";

    //Origin
    public static final double WEDEV_GROUP_LATITUDE = 5.3331297;
    public static final double WEDEV_GROUP_LONGITUDE = -3.9561894;




    //Volley
    public static final int VOLLEY_TIME_OUT = 300000; //5 min
    public static final int CLOSE_DELAY = 1200;
    public static final String TOKEN_HEADER_NAME = "Bearer ";

    // Drawer item size
    public static final int DRAWER_ICON_SIZE = 12;
    public static final int HAMBURGER_ICON_SIZE = 24;
    public static final int ITEM_ICON_SIZE = 24;
    public static final int DRAWER_ICON_PADDING = 2;
    public static final int ITEM_ICON_PADDING = 4;

    // Parcelle List
    public static final int LIST_ADD = 1;
    public static final int LIST_DELETE = 2;
    public static final int LIST_UPDATE = 3;


    //DB Table Parcelle
    public static final String TABLE_PARCELLE                           = "parcelle_tbl";
    public static final String TABLE_PARCELLE_KEY_ID                    = "_id";
    public static final String TABLE_PARCELLE_ZONE_NAME                 = "zone";
    public static final String TABLE_PARCELLE_DISTANCE_NAME             = "distance";
    public static final String TABLE_PARCELLE_COUCHE_NAME               = "couche";
    public static final String TABLE_PARCELLE_REGION_NAME               = "region";
    public static final String TABLE_PARCELLE_REGION_ID_NAME            = "region_id";
    public static final String TABLE_PARCELLE_ENTREPRISE_ID_NAME        = "entre";
    public static final String TABLE_PARCELLE_PERIMETRE_NAME            = "perimetre";
    public static final String TABLE_PARCELLE_SURFACE_NAME              = "surface";
    public static final String TABLE_PARCELLE_DATE_SOUMISSION_NAME      = "d_soummssion";
    public static final String TABLE_PARCELLE_DATE_CREATED_NAME         = "d_created";
    public static final String TABLE_PARCELLE_GUIDE_NOM_NAME            = "nom_guide";
    public static final String TABLE_PARCELLE_GUIDE_EMAIL_NAME          = "email_guide";
    public static final String TABLE_PARCELLE_GUIDE_TEL_NAME            = "tel_guide";
    public static final String TABLE_PARCELLE_IMAGE_URL                 = "img_url";
    public static final String TABLE_PARCELLE_IS_DELETE_NAME            = "is_delete";
    public static final String TABLE_PARCELLE_IS_NEW_NAME               = "is_new";
    public static final String TABLE_PARCELLE_ID_ON_SERVER_NAME         = "id_on_serv";
    public static final String TABLE_PARCELLE_IS_FLY_DONE_NAME          = "is_fl_done";
    public static final String TABLE_PARCELLE_DATE_SURVOL_NAME          = "survol_date";
    public static final String TABLE_PARCELLE_DATE_SURVOL_YEAR_NAME     = "d_year";
    public static final String TABLE_PARCELLE_DATE_SURVOL_MONTH_NAME    = "d_month";
    public static final String TABLE_PARCELLE_DATE_SURVOL_DAY_NAME      = "d_day";

    //DB Table Point
    public static final String TABLE_POINT                              = "point_tbl";
    public static final String TABLE_POINT_KEY                          = "_id";
    public static final String TABLE_POINT_PARCELLE_ID_NAME             = "parcel_id";
    public static final String TABLE_POINT_RANG_NAME                    =  "rang";
    public static final String TABLE_POINT_LATITUDE_NAME                = "latitude";
    public static final String TABLE_POINT_LONGITUDE_NAME               = "longitude";
    public static final String TABLE_POINT_IS_REFERNCE_NAME             = "ref";
    public static final String TABLE_POINT_IS_CENTER_NAME               = "center";


    //DB Table Culture
    public static final String TABLE_CULTURE                            = "culture_tbl";
    public static final String TABLE_CULTURE_KEY                        = "_id";
    public static final String TABLE_CULTURE_PARCELLE_ID_NAME           = "parcel_id";
    public static final String TABLE_CULTURE_ID_NAME                    = "cult_id";
    public static final String TABLE_CULTURE_TYPE_ID_NAME               = "type_id";
    public static final String TABLE_CULTURE_NAME                       = "name";
    public static final String TABLE_CULTURE_TYPE_NAME                  = "type";

    //DB Table Point
    public static final String TABLE_ENTREPRISE                         = "ent_tbl";
    public static final String TABLE_ENTREPRISE_KEY                     = "_id";
    public static final String TABLE_ENTREPRISE_PARCELLE_ID_NAME        = "ent_id";
    public static final String TABLE_ENTREPRISE_NAME_NAME               = "name";
    public static final String TABLE_ENTREPRISE_PHONE_NAME              = "phone";
    public static final String TABLE_ENTREPRISE_EMAIL_NAME              = "email";
    public static final String TABLE_ENTREPRISE_ADDRESS_NAME            = "address";
    public static final String TABLE_ENTREPRISE_REFERENCE_NAME          = "ref";
    public static final String TABLE_ENTREPRISE_WEB_SITE_NAME           = "web_site";


}
