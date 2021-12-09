package com.aariyan.memo_app.Networking;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIs {

    @GET("getmessages")
    Call<ResponseBody> getMessageList();

    @GET("GetCustomers")
    Call<ResponseBody> getCustomerList();

    @GET("GetCustomerLastVisit?")
    Call<ResponseBody> getCustomerVisitMessage(@Query("customerCode") String customerCode);

//    @FormUrlEncoded
//    @POST("Logvisit")
//    Call<String> submitLogVisit(@Field("CustomerCode") String customerCode, @Field("notes") String notes,
//                                      @Field("catchupnotes") String catchUpNotes, @Field("nextvisit") String nextVisitDate,
//                                      @Field("userid") int userId
//    );

    @FormUrlEncoded
    @POST("Logvisit")
    Call<String> submitLogVisit(@Field("CustomerCode") String customerCode, @Field("notes") String notes,
                                @Field("catchupnotes") String catchUpNotes, @Field("nextvisit") String nextVisitDate,
                                @Field("userid") int userId, @Field("Lat") double latitude, @Field("Lon") double longitude
    );

    @FormUrlEncoded
    @POST("Logreminder")
    Call<String> createMemo(@Field("CustomerCode") String customerCode, @Field("notes") String notes,
                            @Field("dates") String dates,
                            @Field("userid") int userId
    );

    @GET("GetVisits.php?")
    Call<ResponseBody> getVisits(@Query("from") String startDate, @Query("to") String endDate, @Query("userId") int userId);

    @GET("GetReminderswebview.php?")
    Call<ResponseBody> getMemo(@Query("from") String startDate, @Query("to") String endDate);

}
