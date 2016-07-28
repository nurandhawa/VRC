package ca.sfu.teambeta.accounts.Responses;

import com.google.gson.annotations.Expose;

/**
 * Created by AlexLand on 2016-07-28.
 */
public class SecurityQuestionResponse {
    @Expose
    String voucherCode;

    public SecurityQuestionResponse(String voucherCode) {
        this.voucherCode = voucherCode;
    }
}
