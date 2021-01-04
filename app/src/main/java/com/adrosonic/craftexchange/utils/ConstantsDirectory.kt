package com.adrosonic.craftexchange.utils

enum class Instances{
    DEV,UAT,PROD
}
object ConstantsDirectory {
    //////////////////////////////////////////////////////////////////////
//    const val BASE_URL_DEV = "http://101.53.153.96:8090/api/" //qa
//    const val BASE_URL_DEV = "http://164.52.192.15:8090/api/"//uat
    const val BASE_URL_DEV = "http://164.52.206.168:8090/api/"//prod

//    const val IMAGE_LOAD_BASE_URL_DEV = "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/" //qa
//    const val IMAGE_LOAD_BASE_URL_DEV = "https://tatacrftexchangeuat.objectstore.e2enetworks.net/"//uat
       const val IMAGE_LOAD_BASE_URL_DEV = "https://tatatrustcraftxchangelive.objectstore.e2enetworks.net/"//prod

//    const val CMS_URL_DEV = "http://101.53.158.227/wordpress/index.php/wp-json/wp/v2/"//qa uat
    const val CMS_URL_DEV = "https://craftxchangecms.antaran.in/index.php/wp-json/wp/v2/"//prod

//    const val BUYER_DASHBOARD_URL = "https://datastudio.google.com/embed/reporting/1ff96a35-a386-4d11-9e64-0d0883bdd7ee/page/iTZoB?params=" //qa
    const val BUYER_DASHBOARD_URL ="https://datastudio.google.com/embed/reporting/22dd8e4d-ca54-4a5a-8084-571f9b776457/page/iJ7cB?params="//uat
//    const val ARTISAN_DASHBOARD_URL = "https://datastudio.google.com/embed/reporting/5fe0ea7a-f3c9-4192-8cc4-5f4dd2e1b995/page/imZoB?params=" //qa
    const val ARTISAN_DASHBOARD_URL = "https://datastudio.google.com/embed/reporting/0c128fc7-917e-4030-b7b4-370628de1996/page/CEweB?params="//uat

    const val ADMIN_DASHBOARD_URL = "https://datastudio.google.com/embed/reporting/00758bf8-9835-4bc1-aa7d-c2fb328332ab/page/wVxhB?params="
    const val VIEW_PI_URL = "http://101.53.153.96:8090/enquiry/getPreviewPiHTML?enquiryId="
    const val VERSION = "22-12-20 V-1.3"
    //////////////////////////////////////////////////////////////////////
    const val PI_PDF_PATH: String = "PiPdfs/"
    const val TI_PDF_PATH: String = "TiPdfs/"
    const val CHAT_MEDIA: String = "ChatMedia/"
    const val CHANNEL_ID: String = "Enquiry status"
    const val CHANNEL_NAME: String = "TTCE Notification"
    const val PERMISSION_REQUEST_CODE = 20
    const val EDIT_IMAGE_POSITION = "position"
    const val EDIT_PATH = "location"
    const val EDIT_IMAGE = 2
    const val PICK_IMAGE = 1
    const val RESULT_PI = 123
    const val RESULT_CONFIRM_ORDER = 125
    const val RESULT_TI = 1333
    const val PREFS_NAME = "craft_exchange"
    const val ARTISAN = "Artisan"
    const val BUYER = "Buyer"
    const val MARKETING = "Marketing"
    const val PROFILE = "profile"
    const val REF_ROLE_ID = "roleId"
    const val ACC_TOKEN = "accesstoken"

    const val USER_ID = "userid"

    const val IS_LOGGED_IN = "checklogin"
    const val IS_EDITTABLE = "iseditable"


    const val USER_EMAIL = "useremail"
    const val USER_PWD = "password"
    const val ARTISAN_ID = "artisanid"

    const val FIRST_NAME = "firstname"
    const val LAST_NAME = "lastname"
    const val MOBILE = "mobile"
    const val ALT_MOBILE = "altmobile"
    const val DESIGNATION = "designation"

    const val COMP_NAME = "companyname"
    const val CIN = "cin"
    const val GST = "gst"
    const val PAN = "pan"
    const val POC_NAME = "pocname"

    //    const val POC_LNAME = "poclname"
    const val POC_CONTACT = "poccontact"
    const val POC_EMAIL = "pocemail"

    const val ADDR_LINE1 = "addrline1"
    const val ADDR_LINE2 = "addrline2"
    const val STREET = "street"
    const val LANDMARK = "landmark"
    const val DISTRICT = "district"
    const val CITY = "city"
    const val STATE = "state"
    const val COUNTRY = "country"
    const val COUNTRY_ID = "countryid"
    const val PINCODE = "pincode"

    const val SOCIAL_LINK = "sociallink"
    const val WEB_LINK = "weblink"

    const val IS_FIRST_TIME = "isfirsttime"

    const val CLUSTER_ID = "clusterid"
    const val CLUSTER_NAME = "clustername"

    const val BRAND_LOGO = "buyerbrandlogo"
    const val BRAND_IMG_NAME = "buyerimagename"

    const val PROFILE_PHOTO = "profilephoto"
    const val PROFILE_PHOTO_NAME = "profilephotoname"

    const val DELIVERY = "Delivery"
    const val REGISTERED = "Registered"

    const val VIEW_PROD_OF = "viewproductof"

    const val CATEGORY_PRODUCTS = "Category"
    const val CLUSTER_PRODUCTS = "Cluster"
    const val BRAND_PRODUCTS = "Brand"

    const val AVAILABLE_IN_STOCK = "Available In Stock"
    const val MADE_TO_ORDER = "Made To Order"
    const val IMAGE_URL = "imageurl"

    const val FILTER = "filter"

    const val IS_MADE_WITH_ANTHARAN = "is_made_with_antharan"
    const val PRODUCT_CATEGORY = "productcategory"
    const val PRODUCT_CATEGORY_ID = "productcategoryid"
    const val PRODUCT_ID = "productid"

    const val ENQUIRY_ID = "enquiryid"
    const val ENQUIRY_CODE = "enquirycode"
    const val CUSTOM_PRODUCT = "Custom Product"

    const val ENQUIRY_STATUS_FLAG = "enquiry_status_flag"
    const val ORDER_STATUS_FLAG = "order_status_flag"


    const val PI_ID ="piid"

    const val TAX_INV_WEB_STRING = "tax_inv_web_string"
}