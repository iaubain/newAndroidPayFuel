package com.oltranz.pf.n_payfuel.config;

import com.oltranz.pf.n_payfuel.models.commonmodels.CommonBranch;
import com.oltranz.pf.n_payfuel.models.login.LoginRequest;
import com.oltranz.pf.n_payfuel.models.login.LoginResponse;
import com.oltranz.pf.n_payfuel.models.logout.LogoutRequest;
import com.oltranz.pf.n_payfuel.models.logout.LogoutResponse;
import com.oltranz.pf.n_payfuel.models.payments.PaymentsResponse;
import com.oltranz.pf.n_payfuel.models.pump.PumpResponse;
import com.oltranz.pf.n_payfuel.models.register.RegisterRequest;
import com.oltranz.pf.n_payfuel.models.register.RegisterResponse;
import com.oltranz.pf.n_payfuel.models.reserve.ReserveModel;
import com.oltranz.pf.n_payfuel.models.reserve.ReserveResponse;
import com.oltranz.pf.n_payfuel.models.sales.SalesRequest;
import com.oltranz.pf.n_payfuel.models.sales.SalesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * Created by Owner on 7/9/2016.
 */
public interface PrimeServices {
    //String BASE_URL="http://41.186.53.35:8080/"; //Link to MTN server
    String BASE_URL="http://sppos.oltranz.com/"; //Link to AOS server, production
    String LOGIN_URL="android/login";
    String REGISTER_URL="DeviceManagementService/device/register";
    String GET_PAYMENTS = "PaymentModeManagementService/paymentmodes/";
    String LOGOUT_URL = "android/logout";
    String SALES_URL = "android/sale";

    @POST(PrimeServices.LOGIN_URL)
    Call<LoginResponse> login(@Body LoginRequest login);

    @POST(PrimeServices.LOGOUT_URL)
    Call<LogoutResponse> logout(@Body LogoutRequest logoutRequest);

    @POST(PrimeServices.REGISTER_URL)
    Call<RegisterResponse> enrolDevice(@Body RegisterRequest registerRequest);

    @GET(PrimeServices.GET_PAYMENTS+"{userId}")
    Call<PaymentsResponse> getPaymentsMode(@Path("userId") String userId);

    @POST(PrimeServices.SALES_URL)
    Call<SalesResponse> postSales(@Body SalesRequest salesRequest);
}
