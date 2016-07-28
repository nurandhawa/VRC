package ca.sfu.teambeta.accounts;

import java.util.Calendar;

/**
 * The PasswordResetVoucherMetadata class holds all the related
 * information (metadata) about a password reset voucher, such
 * as it's expiry.
 *
 * And yes, it has a long name.
 *
 */

public class PasswordResetVoucherMetadata {
    private static final int TIME_TO_LIVE = 15; // TTL is calculated in minutes
    private Calendar expiryDate;


    // MARK: Constructor
    public PasswordResetVoucherMetadata() {
        this.expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.MINUTE, TIME_TO_LIVE);
    }


    // MARK: Helper Methods
    public boolean isExpired() {
        Calendar currentTime = Calendar.getInstance();
        int value = currentTime.compareTo(expiryDate);
        boolean expired = (value == 1);

        return expired;
    }
}
