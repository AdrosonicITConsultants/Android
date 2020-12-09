package com.adrosonic.craftexchange.utils

import com.pixplicity.easyprefs.library.Prefs

//////////////////////////////////////////////////////////////////////
//const val BASE_URL = "http://101.53.153.96:8090/"
//////////////////////////////////////////////////////////////////////

const val PREFS_NAME = "craft_exchange"
const val ARTISAN = "Artisan"
const val BUYER = "Buyer"

const val VIDEO_ARTISAN = "videoartisan"
const val VIDEO_BUYER = "videobuyer"

//const val ADMIN = "Administrator"
const val PROFILE = "profile"
const val REF_ROLE_ID = "roleId"
const val ACC_TOKEN = "accesstoken"
const val USER_ID = "userid"
const val RATING_QUESTIONS = "rating_questions"
const val SHOW_BUYER_RATING = "show_buyer_rating"
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
const val BRAND_LOGO = "buyerbrandlogo"
const val BRAND_IMG_NAME = "buyerimagename"
const val PROFILE_PHOTO = "profilephoto"
const val PROFILE_PHOTO_NAME = "profilephotoname"
const val DELIVERY = "Delivery"
const val REGISTERED = "Registered"
const val VIEW_PROD_OF = "viewproductof"
const val CATEGORY_PRODUCTS = "CategoryProducts"
const val CLUSTER_PRODUCTS = "ClusterProducts"
const val BRAND_PRODUCTS = "BrandProducts"
const val AVAILABLE_IN_STOCK = "Available In Stock"
const val MADE_TO_ORDER = "Made To Order"
const val IMAGE_URL = "imageurl"
const val FILTER = "filter"
const val PRODUCT_CATEGORY = "productcategory"
const val PRODUCT_CATEGORY_ID = "productcategoryid"
const val DEVICE_NAME = "deviceName"
const val KEY_DEVICE_REGISTRATION_TOKEN = "device_registration_token"
const val NOTI_BADGE_COUNT = "noti_badge_count"
///////////////////ProductTemplateParams///////////////////////
const val PRODUCT_UPLOAD_JSON = "product_upload_json"
const val WARP_DYE_ID = "warpDyeId"
const val WARP_YARN_COUNT = "warpYarnCount"
const val WARP_YARN_ID = "warpYarnId"
const val WEFT_DYE_ID = "weftDyeId"
const val WEFT_YARN_COUNT = "weftYarnCount"
const val WEFT_YARN_ID = "weftYarnId"
const val EXTRA_WEFT_DYE_ID = "extraWeftDyeId"
const val EXTRA_WEFT_YARN_COUNT = "extraWeftYarnCount"
const val EXTRA_WEFT_YARN_ID = "extraWeftYarnId"

////////////////////////Enquiry/////////////////////////////
const val MOQ_DELIVERY_DATES = "moq_delivery_dates"
const val ENQUIRY_STAGE_DATA = "enquiry_stage_data"
const val INNER_ENQUIRY_STAGE_DATA = "inner_enquiry_stage_data"
const val ENQUIRY_AVAI_PROD_STAGE_DATA = "enquiry_available_product_stage_data"
const val PROGRESS_TIMELINE_DATA = "progress_timeline_data"
const val TRANSACTION_STATUS_DATA ="transaction_status_data"
const val CR_STATUS_DATA ="cr_status_data"
const val ARTISAN_FAULT_REVIEW = "artisan_fault_review"
const val BUYER_FAULT_REVIEW = "buyer_fault_review"
////
const val REGION_CMS_DATA = "region_cms_data"
const val CATEGORY_CMS_DATA = "category_cms_data"
const val PAGE_CMS_DATA = "page_cms_data"
///QC
const val QC_STAGE_DATA ="qc_stage_data"
const val QC_QUESTION_DATA ="qc_question_data"

const val ESCALATION_DATA ="escalation_data"

const val IS_ANTRAN_CODESIGN ="is_antran_codesign"

class UserConfig {

    private object Holder { val INSTANCE = UserConfig() }

    companion object {
        val shared: UserConfig by lazy { Holder.INSTANCE }
    }

    fun clearPreferences() {
        val editor =Prefs.edit()
        editor.clear()
        editor.commit()
    }
    var moqDeliveryDates = ""
        get() = Prefs.getString(MOQ_DELIVERY_DATES, "")
        set(value) {
            Prefs.putString(MOQ_DELIVERY_DATES, value)
            field = value
        }
    var notiBadgeCount = 0L
        get() = Prefs.getLong(NOTI_BADGE_COUNT, 0L)
        set(value) {
            Prefs.putLong(NOTI_BADGE_COUNT, value)
            field = value
        }
    var deviceRegistrationToken = ""
        get() = Prefs.getString(KEY_DEVICE_REGISTRATION_TOKEN, "")
        set(value) {
            Prefs.putString(KEY_DEVICE_REGISTRATION_TOKEN, value)
            field = value
        }

    var artisan: String? = ""
        get() = Prefs.getString(ARTISAN,"")
        set(value) {
            Prefs.putString(ARTISAN, value)
            field = value
        }

    var buyer: String? = ""
        get() = Prefs.getString(BUYER,"")
        set(value) {
            Prefs.putString(BUYER, value)
            field = value
        }

    var videoBuyer: String? = ""
        get() = Prefs.getString(VIDEO_BUYER,"")
        set(value) {
            Prefs.putString(VIDEO_BUYER, value)
            field = value
        }
    var videoArtisan: String? = ""
    get() = Prefs.getString(VIDEO_ARTISAN,"")
    set(value) {
        Prefs.putString(VIDEO_ARTISAN, value)
        field = value
    }

    var refRoleId: String? = ""
        get() = Prefs.getString(REF_ROLE_ID,"")
        set(value) {
            Prefs.putString(REF_ROLE_ID, value)
            field = value
        }

    var authToken: String? = ""
        get() = Prefs.getString(ACC_TOKEN,"")
        set(value) {
            Prefs.putString(ACC_TOKEN, value)
            field = value
        }

    var userProfile: String? = ""
        get() = Prefs.getString(PROFILE,"")
        set(value) {
            Prefs.putString(PROFILE, value)
            field = value
        }

    var userEmail: String? = ""
        get() = Prefs.getString(USER_EMAIL,"")
        set(value) {
            Prefs.putString(USER_ID, value)
            field = value
        }

    var userPassword: String? = ""
        get() = Prefs.getString(USER_PWD,"")
        set(value) {
            Prefs.putString(USER_PWD, value)
            field = value
        }

    var userId: String? = ""
    get() = Prefs.getString(USER_ID,"")
    set(value) {
        Prefs.putString(USER_ID, value)
        field = value
    }

    var ratingQuestions: String? = ""
    get() = Prefs.getString(RATING_QUESTIONS,"")
    set(value) {
        Prefs.putString(RATING_QUESTIONS, value)
        field = value
    }

    var showBuyerRating: String? = ""
        get() = Prefs.getString(SHOW_BUYER_RATING,"")
        set(value) {
            Prefs.putString(SHOW_BUYER_RATING, value)
            field = value
        }

    var isUserLoggedIn: String? = ""
        get() = Prefs.getString(IS_LOGGED_IN,"")
        set(value) {
            Prefs.putString(IS_LOGGED_IN, value)
            field = value
        }

    var isProfileEditable: String? = ""
        get() = Prefs.getString(IS_EDITTABLE,"")
        set(value) {
            Prefs.putString(IS_EDITTABLE, value)
            field = value
        }

    var firstname: String? = ""
        get() = Prefs.getString(FIRST_NAME,"")
        set(value) {
            Prefs.putString(FIRST_NAME, value)
            field = value
        }

    var lastname: String? = ""
        get() = Prefs.getString(LAST_NAME,"")
        set(value) {
            Prefs.putString(LAST_NAME, value)
            field = value
        }

    var primaryMobile: String? = ""
        get() = Prefs.getString(MOBILE,"")
        set(value) {
            Prefs.putString(MOBILE, value)
            field = value
        }

    var alternateMobile: String? = ""
        get() = Prefs.getString(ALT_MOBILE,"")
        set(value) {
            Prefs.putString(ALT_MOBILE, value)
            field = value
        }

    var designation: String? = ""
        get() = Prefs.getString(DESIGNATION,"")
        set(value) {
            Prefs.putString(DESIGNATION, value)
            field = value
        }

    var companyname: String? = ""
        get() = Prefs.getString(COMP_NAME,"")
        set(value) {
            Prefs.putString(COMP_NAME, value)
            field = value
        }

    var gst: String? = ""
        get() = Prefs.getString(GST,"")
        set(value) {
            Prefs.putString(GST, value)
            field = value
        }

    var cin: String? = ""
        get() = Prefs.getString(CIN,"")
        set(value) {
            Prefs.putString(CIN, value)
            field = value
        }

    var pan: String? = ""
        get() = Prefs.getString(PAN,"")
        set(value) {
            Prefs.putString(PAN, value)
            field = value
        }

    var pocname: String? = ""
        get() = Prefs.getString(POC_NAME,"")
        set(value) {
            Prefs.putString(POC_NAME, value)
            field = value
        }

    var pocemail: String? = ""
        get() = Prefs.getString(POC_EMAIL,"")
        set(value) {
            Prefs.putString(POC_EMAIL, value)
            field = value
        }

    var poccontact: String? = ""
        get() = Prefs.getString(POC_CONTACT,"")
        set(value) {
            Prefs.putString(POC_CONTACT, value)
            field = value
        }

    var addressLine1: String? = ""
        get() = Prefs.getString(ADDR_LINE1,"")
        set(value) {
            Prefs.putString(ADDR_LINE1, value)
            field = value
        }

    var addressLine2: String? = ""
        get() = Prefs.getString(ADDR_LINE2,"")
        set(value) {
            Prefs.putString(ADDR_LINE2, value)
            field = value
        }

    var street: String? = ""
        get() = Prefs.getString(STREET,"")
        set(value) {
            Prefs.putString(STREET, value)
            field = value
        }

    var landmark: String? = ""
        get() = Prefs.getString(LANDMARK,"")
        set(value) {
            Prefs.putString(LANDMARK, value)
            field = value
        }

    var city: String? = ""
        get() = Prefs.getString(CITY,"")
        set(value) {
            Prefs.putString(CITY, value)
            field = value
        }

    var district: String? = ""
        get() = Prefs.getString(DISTRICT,"")
        set(value) {
            Prefs.putString(DISTRICT, value)
            field = value
        }

    var pincode: String? = ""
        get() = Prefs.getString(PINCODE,"")
        set(value) {
            Prefs.putString(PINCODE, value)
            field = value
        }

    var state: String? = ""
        get() = Prefs.getString(STATE,"")
        set(value) {
            Prefs.putString(STATE, value)
            field = value
        }

    var countryId: String? = ""
        get() = Prefs.getString(COUNTRY_ID,"")
        set(value) {
            Prefs.putString(COUNTRY_ID, value)
            field = value
        }

    var country: String? = ""
        get() = Prefs.getString(COUNTRY,"")
        set(value) {
            Prefs.putString(COUNTRY, value)
            field = value
        }

    var sociallink: String? = ""
        get() = Prefs.getString(SOCIAL_LINK,"")
        set(value) {
            Prefs.putString(SOCIAL_LINK, value)
            field = value
        }

    var websitelink: String? = ""
        get() = Prefs.getString(WEB_LINK,"")
        set(value) {
            Prefs.putString(WEB_LINK, value)
            field = value
        }

    var clusterId: String? = ""
        get() = Prefs.getString(CLUSTER_ID,"")
        set(value) {
            Prefs.putString(CLUSTER_ID, value)
            field = value
        }

    var brandLogoPath: String? = ""
        get() = Prefs.getString(BRAND_LOGO,"")
        set(value) {
            Prefs.putString(BRAND_LOGO, value)
            field = value
        }

    var profileImgPath: String? = ""
        get() = Prefs.getString(PROFILE_PHOTO,"")
        set(value) {
            Prefs.putString(PROFILE_PHOTO, value)
            field = value
        }

    var brandLogoName: String? = ""
        get() = Prefs.getString(BRAND_IMG_NAME,"")
        set(value) {
            Prefs.putString(BRAND_IMG_NAME, value)
            field = value
        }

    var ProfileImgName: String? = ""
        get() = Prefs.getString(PROFILE_PHOTO_NAME,"")
        set(value) {
            Prefs.putString(PROFILE_PHOTO_NAME, value)
            field = value
        }

    var deliveryAddr: String? = ""
        get() = Prefs.getString(DELIVERY,"")
        set(value) {
            Prefs.putString(DELIVERY, value)
            field = value
        }

    var registeredAddr: String? = ""
        get() = Prefs.getString(REGISTERED,"")
        set(value) {
            Prefs.putString(REGISTERED, value)
            field = value
        }

    var categoryTag: String? = ""
        get() = Prefs.getString(CATEGORY_PRODUCTS,"")
        set(value) {
            Prefs.putString(CATEGORY_PRODUCTS, value)
            field = value
        }

    var clusterTag: String? = ""
        get() = Prefs.getString(CLUSTER_PRODUCTS,"")
        set(value) {
            Prefs.putString(CLUSTER_PRODUCTS, value)
            field = value
        }

    var brandTag: String? = ""
        get() = Prefs.getString(BRAND_PRODUCTS,"")
        set(value) {
            Prefs.putString(BRAND_PRODUCTS, value)
            field = value
        }

    var prodCategory: String? = ""
        get() = Prefs.getString(PRODUCT_CATEGORY,"")
        set(value) {
            Prefs.putString(PRODUCT_CATEGORY, value)
            field = value
        }

    var prodCategoryId: String? = ""
        get() = Prefs.getString(PRODUCT_CATEGORY_ID,"")
        set(value) {
            Prefs.putString(PRODUCT_CATEGORY_ID, value)
            field = value
        }

    var productUploadJson: String? = ""
        get() = Prefs.getString(PRODUCT_UPLOAD_JSON,"")
        set(value) {
            Prefs.putString(PRODUCT_UPLOAD_JSON, value)
            field = value
        }

    var regionCMS: String? = ""
        get() = Prefs.getString(REGION_CMS_DATA,"")
        set(value) {
            Prefs.putString(REGION_CMS_DATA, value)
            field = value
        }

    var categoryCMS: String? = ""
        get() = Prefs.getString(CATEGORY_CMS_DATA,"")
        set(value) {
            Prefs.putString(CATEGORY_CMS_DATA, value)
            field = value
        }

    var pageCMS: String? = ""
        get() = Prefs.getString(PAGE_CMS_DATA,"")
        set(value) {
            Prefs.putString(PAGE_CMS_DATA, value)
            field = value
        }

    var imageUrlList : String? = ""
        get() = Prefs.getString(IMAGE_URL,"")
        set(value) {
            Prefs.putString(IMAGE_URL, value)
            field = value
        }
    var warpDyeId : Long? = 0
        get() = Prefs.getLong(WARP_DYE_ID,0)
        set(value) {
            Prefs.putLong(WARP_DYE_ID, value?:0)
            field = value
        }
    var warpYarnCount : String? = ""
        get() = Prefs.getString(WARP_YARN_COUNT,"")
        set(value) {
            Prefs.putString(WARP_YARN_COUNT, value)
            field = value
        }
    var warpYarnId : Long? = 0
        get() = Prefs.getLong(WARP_YARN_ID,0)
        set(value) {
            Prefs.putLong(WARP_YARN_ID, value?:0)
            field = value
        }
    var weftDyeId : Long? = 0
        get() = Prefs.getLong(WEFT_DYE_ID,0)
        set(value) {
            Prefs.putLong(WEFT_DYE_ID, value?:0)
            field = value
        }
    var weftYarnCount : String? = ""
        get() = Prefs.getString(WEFT_YARN_COUNT,"")
        set(value) {
            Prefs.putString(WEFT_YARN_COUNT, value)
            field = value
        }
    var weftYarnId : Long? = 0
        get() = Prefs.getLong(WEFT_YARN_ID,0)
        set(value) {
            Prefs.putLong(WEFT_YARN_ID, value?:0)
            field = value
        }
    var extraWeftDyeId : Long? = 0
        get() = Prefs.getLong(EXTRA_WEFT_DYE_ID,0)
        set(value) {
            Prefs.putLong(EXTRA_WEFT_DYE_ID, value?:0)
            field = value
        }
    var extraWeftYarnCount : String? = ""
        get() = Prefs.getString(EXTRA_WEFT_YARN_COUNT,"")
        set(value) {
            Prefs.putString(EXTRA_WEFT_YARN_COUNT, value)
            field = value
        }
    var extraWeftYarnId :Long?=0
        get() = Prefs.getLong(EXTRA_WEFT_YARN_ID,0)
        set(value) {
            Prefs.putLong(EXTRA_WEFT_YARN_ID, value?:0)
            field = value
        }

    var deviceName : String? = ""
        get() = Prefs.getString(DEVICE_NAME,"")
        set(value) {
            Prefs.putString(DEVICE_NAME, value)
            field = value
        }

    var enquiryStageData : String? = ""
        get() = Prefs.getString(ENQUIRY_STAGE_DATA,"")
        set(value) {
            Prefs.putString(ENQUIRY_STAGE_DATA, value)
            field = value
        }

    var innerEnquiryStageData : String? = ""
    get() = Prefs.getString(INNER_ENQUIRY_STAGE_DATA,"")
    set(value) {
        Prefs.putString(INNER_ENQUIRY_STAGE_DATA, value)
        field = value
    }

    var qcStageData : String? = ""
        get() = Prefs.getString(QC_STAGE_DATA,"")
        set(value) {
            Prefs.putString(QC_STAGE_DATA, value)
            field = value
        }

    var qcQuestionData : String? = ""
        get() = Prefs.getString(QC_QUESTION_DATA,"")
        set(value) {
            Prefs.putString(QC_QUESTION_DATA, value)
            field = value
        }

    var escalationData : String? = ""
        get() = Prefs.getString(ESCALATION_DATA,"")
        set(value) {
            Prefs.putString(ESCALATION_DATA, value)
            field = value
        }

    var progressTimeData : String? = ""
        get() = Prefs.getString(PROGRESS_TIMELINE_DATA,"")
        set(value) {
            Prefs.putString(PROGRESS_TIMELINE_DATA, value)
            field = value
        }

    var enquiryAvaProdStageData : String? = ""
        get() = Prefs.getString(ENQUIRY_AVAI_PROD_STAGE_DATA,"")
        set(value) {
            Prefs.putString(ENQUIRY_AVAI_PROD_STAGE_DATA, value)
            field = value
        }

    var transactionStatusData : String? = ""
        get() = Prefs.getString(TRANSACTION_STATUS_DATA,"")
        set(value) {
            Prefs.putString(TRANSACTION_STATUS_DATA, value)
            field = value
        }

    var crStatusData : String? = ""
        get() = Prefs.getString(CR_STATUS_DATA,"")
        set(value) {
            Prefs.putString(CR_STATUS_DATA, value)
            field = value
        }

    var artisanFaultReviewData : String? = ""
        get() = Prefs.getString(ARTISAN_FAULT_REVIEW,"")
        set(value) {
            Prefs.putString(ARTISAN_FAULT_REVIEW, value)
            field = value
        }

    var buyerFaultReviewData : String? = ""
        get() = Prefs.getString(BUYER_FAULT_REVIEW,"")
        set(value) {
            Prefs.putString(BUYER_FAULT_REVIEW, value)
            field = value
        }

    var isAntranCoDesign : Boolean = false
        get() = Prefs.getBoolean(IS_ANTRAN_CODESIGN,false)
        set(value) {
            Prefs.putBoolean(IS_ANTRAN_CODESIGN, value)
            field = value
        }

}

