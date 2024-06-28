package com.michael.document.constant;

public class Constants {
    public static final String[] PUBLIC_URLS = {
            "/user/reset_password/reset/**",
            "/user/verify/resetpassword/**",
            "/user/reset_password/**",
            "/user/reset_password/**",
            "/user/verify/qrcode/**",
            "/user/login/**",
            "/user/verify/account/**",
            "/user/register/**",
            "/user/new/password/**",
            "/user/verify/**",
            //  "document/download/**",

            //   "/user/image/**",
            "/user/verify/password/**"
            //     "/user/mfa/setup/**"
    };

    public static final String[] SWAGGER_PUBLIC_URLS = {
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };
    public static final int NINETY_DAYS = 90;
    public static final int STRENGTH = 12;
    public static final String BASE_PATH = "/**";
    public static final String FILE_NAME = "File-Name";
    public static final String LOGIN_PATH = "/user/login";
    public static final String[] PUBLIC_ROUTES = {"/user/reset_password/reset",
            "/user/verify/resetpassword",
            "/user/reset_password",
            "/user/verify/qrcode", "/user/stream", "/user/id", "/user/login", "/user/register", "/user/new/password",
            "/user/verify",
            "/user/refresh/token",
            //   "/user/image",
            "/user/verify/account", "/user/verify/password", "/user/verify/code"};
    public static final String AUTHORITIES = "authorities";
    public static final String MICHAEL_ROYF_LLC = "MICHAEL_ROYF_LLC";
    public static final String EMPTY_VALUE = "empty";
    public static final String ROLE = "role";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORITY_DELIMITER = ",";
    public static final String USER_AUTHORITIES = "document:create,document:read,document:update,document:delete";
    public static final String ADMIN_AUTHORITIES = "user:create,user:read,user:update,document:create,document:read,document:update,document:delete";
    public static final String SUPER_ADMIN_AUTHORITIES = "user:create,user:read,user:update,user:delete,document:create,document:read,document:update,document:delete";
    public static final String MANAGER_AUTHORITIES = "document:create,document:read,document:update,document:delete";




    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String NO_USER_FOUND_BY_USERNAME = "No user found by username: %s";
    public static final String NO_USER_FOUND_BY_EMAIL = "No user found by email: %s ";
    public static final String NO_USER_FOUND_BY_ID = "No user found by ID";
    public static final String NO_ROLE_FOUND_BY_NAME = "No role found by name: %s";
    public static final String NOT_FOUND = "Not found";
    public static final String INCORRECT_PASSWORD = "Incorrect password";
    public static final String USER_DELETED = "User with username: %s was deleted";
    public static final String USER_CREDENTIAL_NOT_FOUND = "Unable to find user credential";
    public static final String TOKEN_NOT_FOUND = "Unable to find token";
    public static final String INCORRECT_TOKEN = "Incorrect token";
    public static final String EXISTING_PASSWORD_INCORRECT = "Existing password is incorrect";
    public static final String INVALID_QR_CODE = "Invalid QR code. Please try again";
    public static final String CONFIRMATION_INFORMATION_NOT_FOUND = "Confirmation information not found";


    public static final String USER_IMAGE_PATH = "/user/image/";
    public static final String JPG_EXTENSION = "jpg";
    public static final String PNG_EXTENSION = ".png";
    public static final String DOT = ".";
    public static final String FORWARD_SLASH = "/";
    public static final String NOT_AN_IMAGE_FILE = "%s is not an image file. Please upload an image file";
    public static final String TEMP_PROFILE_IMAGE_BASE_URL = "https://robohash.org/";
    //   public static final String PATH_PREFIX = "/api/v1/";
    public static final String IMAGE_NOT_FOUND = "Profile image not found";
    public static final String NO_IMAGE_FOUND_BY_FILENAME = "No profile image found by filename: %s";
    public static final String NO_IMAGE_FOUND_BY_URL = "No profile image found by URL: %s";
    public static final String NO_IMAGE_FOUND_BY_USER = "No profile image found by user: %s";
    //"https://cdn-icons-png.flaticon.com/512/149/149071.png"




    //email
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String PASSWORD_RESET_REQUEST = "Reset Password Request";
    public static final String UNABLE_TO_SEND_EMAIL = "Unable to send email";



    //public static final String FILE_STORAGE = "C://Users/micha/Downloads/uploads/";
//    public static final String SELECT_DOCUMENTS_QUERY = "select doc.id, doc.document_id, doc.name, doc.description, doc.uri, doc.icon, doc.size, doc.formatted_size, doc.extension, doc.reference_id,  doc.created_at, doc.updated_at, CONCAT(owner.first_name, '', owner.last_name) AS owner_name, owner.email AS owner_email,owner.phone AS owner_phone, owner.last_login AS owner_last_login, CONCAT(updater.first_name, '', updater.last_name) AS updater_name FROM documents  doc JOIN users owner ON owner.id = doc.created_by JOIN users updater ON updater.id = doc.updated_by";
//    public static final String  SELECT_COUNT_DOCUMENTS_QUERY ="SELECT COUNT(*) FROM documents";
//    public static final String SELECT_DOCUMENTS_BY_NAME_QUERY = "select doc.id, doc.document_id, doc.name, doc.description, doc.uri, doc.icon, doc.size, doc.formatted_size, doc.extension, doc.reference_id,  doc.created_at, doc.updated_at, CONCAT(owner.first_name, '', owner.last_name) AS owner_name, owner.email AS owner_email,owner.phone AS owner_phone, owner.last_login AS owner_last_login, CONCAT(updater.first_name, '', updater.last_name) AS updater_name FROM documents  doc JOIN users owner ON owner.id = doc.created_by JOIN users updater ON updater.id = doc.updated_by WHERE name ~* :documentName";
//    public static final String  SELECT_COUNT_DOCUMENTS_BY_NAME_QUERY ="SELECT COUNT(*) FROM documents WHERE name ~* :documentName";
//    public static final String SELECT_DOCUMENT_QUERY = "select doc.id, doc.document_id, doc.name, doc.description, doc.uri, doc.icon, doc.size, doc.formatted_size, doc.extension, doc.reference_id,  doc.created_at, doc.updated_at, CONCAT(owner.first_name, '', owner.last_name) AS owner_name, owner.email AS owner_email,owner.phone AS owner_phone, owner.last_login AS owner_last_login, CONCAT(updater.first_name, '', updater.last_name) AS updater_name FROM documents  doc JOIN users owner ON owner.id = doc.created_by JOIN users updater ON updater.id = doc.updated_by WHERE doc.document_id = ?1";

}
