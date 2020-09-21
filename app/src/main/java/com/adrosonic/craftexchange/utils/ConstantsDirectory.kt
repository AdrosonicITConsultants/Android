package com.adrosonic.craftexchange.utils

enum class Instances{
    DEV,UAT,PROD
}
object ConstantsDirectory {
    //////////////////////////////////////////////////////////////////////
    const val BASE_URL_DEV = "http://101.53.153.96:8090/"
    const val BASE_URL_UAT = "http://164.52.192.15:8090/"//"http://164.52.192.98:8090/"
    const val IMAGE_LOAD_BASE_URL_DEV = "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/"
    const val IMAGE_LOAD_BASE_URL_UAT = "https://tatacrftexchangeuat.objectstore.e2enetworks.net/"
    const val BUYER_DASHBOARD_URL = "https://datastudio.google.com/embed/reporting/0ede1d26-5dbf-4564-a7c4-4f850493a89f/page/i56cB?params="
    const val ARTISAN_DASHBOARD_URL = "https://datastudio.google.com/embed/reporting/cef7a3b2-e37f-48a2-9f28-0c3f45a07585/page/RJ8dB?params="

    //////////////////////////////////////////////////////////////////////
    const val CHANNEL_ID: String = "Enquiry status"
    const val CHANNEL_NAME: String = "TTCE Notification"
    const val PERMISSION_REQUEST_CODE = 20
    const val EDIT_IMAGE_POSITION = "position"
    const val EDIT_PATH = "location"
    const val EDIT_IMAGE = 2
    const val PICK_IMAGE = 1
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

    const val PRODUCT_CATEGORY = "productcategory"
    const val PRODUCT_CATEGORY_ID = "productcategoryid"
    const val PRODUCT_ID = "productid"

    const val ENQUIRY_ID = "enquiryid"
    const val ENQUIRY_CODE = "enquirycode"
    const val CUSTOM_PRODUCT = "Custom Product"

    const val ENQUIRY_STATUS_FLAG = "enquiry_status_flag"
}