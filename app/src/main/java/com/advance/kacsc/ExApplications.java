package com.advance.kacsc;


import android.app.Application;

import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.PaymentButtonIntent;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.config.UIConfig;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class ExApplications extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PayPalCheckout.setConfig(new CheckoutConfig(
                this,
                "AYfjcfzur4zmFUBNWy_8Uq6RXi6768dQr1bX_SieX1_Uj7KIdAtWXrmC1-9hIAELiFiTPn0VYkuBT5Kp",
                Environment.SANDBOX,
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                PaymentButtonIntent.CAPTURE,
                new SettingsConfig(true, false),
                new UIConfig(true),
                "com.advance.kacsc://paypalpay"
        ));
    }
}

