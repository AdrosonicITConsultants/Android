package com.adrosonic.craftexchangemarketing.repository

import com.adrosonic.craftexchangemarketing.repository.remote.*
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.google.gson.GsonBuilder
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object craftexchangemarketingRepository {
    private fun <T> builder(endpoint: Class<T>): T {
        var lists = listOf(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT)


        return Retrofit.Builder()
            .baseUrl(ConstantsDirectory.BASE_URL_UAT)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY))
//                .connectionSpecs(Collections.singletonList(spec))
                .connectionSpecs(lists)
                .connectTimeout(60,TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
        .build())

            .build()
            .create(endpoint)
    }

    fun getRegisterService(): RegisterDao {
        return builder(RegisterDao::class.java)
    }

    fun getIndividualUserService(): IndividualUserDao {
        return builder(IndividualUserDao::class.java)
    }

    fun getUserDatabaseService(): UserDatabaseDao{
        return builder(UserDatabaseDao::class.java)
    }

    fun getLoginService(): LoginDao {
        return builder(LoginDao::class.java)
    }

    fun getResetPwdService(): ResetPasswordDao {
        return builder(ResetPasswordDao::class.java)
    }


    fun getCrService(): ChangeRequestDao {
        return builder(ChangeRequestDao::class.java)
    }

    fun getOrderService(): OrderDao {
        return builder(OrderDao::class.java)
    }
    fun getClusterService(): ClusterDao {
        return builder(ClusterDao::class.java)
    }

    fun getUserService(): UserDao {
        return builder(UserDao::class.java)
    }

    fun getProductService(): ProductDao {
        return builder(ProductDao::class.java)
    }

    fun getWishlistService():WishlistDao{
        return builder(WishlistDao::class.java)
    }

    fun getEnquiryService():EnquiryDao{
        return builder(EnquiryDao::class.java)
    }

    fun getBuyerOwnDesignService(): BuyerOwnDesignDao {
        return builder(BuyerOwnDesignDao::class.java)
    }
    fun getQCService(): QCDao {
        return builder(QCDao::class.java)
    }
    fun getSearchService(): SearchDao {
        return builder(SearchDao::class.java)
    }

    fun getNotificationService(): NotificationDao {
        return builder(NotificationDao::class.java)
    }
    fun getMoqService(): MoqDao {
        return builder(MoqDao::class.java)
    }
    fun getPiService(): PIDao {
        return builder(PIDao::class.java)
    }
    fun getMarketingService(): MarketingDao {
        return builder(MarketingDao::class.java)
    }

    fun getTransactionService(): TransactionDao {
        return builder(TransactionDao::class.java)
    }
    fun getEnquiryOrderService() : EnquiryOrderDao{
        return builder(EnquiryOrderDao::class.java)
    }
    fun getTeamService() : TeamDao{
        return builder(TeamDao::class.java)
    }
    fun getProductCatService() : ProductCatalogueDao{
        return builder(ProductCatalogueDao::class.java)
    }
    fun getChatService(): ChatListDao {
        return builder(ChatListDao::class.java)
    }
    fun getTiService(): TIDao {
        return builder(TIDao::class.java)
    }
    fun getRedirectEnquiryService(): RedirectedEnquiryeDao {
        return builder(RedirectedEnquiryeDao::class.java)
    }
    fun getEscalationDataService(): EscalationDao {
        return builder(EscalationDao::class.java)
    }
}