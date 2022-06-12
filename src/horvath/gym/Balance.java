package horvath.gym;

import java.io.Serializable;
import java.util.Currency;

/**
 *  Class, which represents balance
 *  @author Marcel Horváth
 *
 *  @param currency
 *  @param amount
 **/

public record Balance(Currency currency, float amount) implements Serializable {
     public static final float  USD_CZK_RATE = 21;

    @Override
    public String toString() {
        return String.format("%.2f %s", amount, currency.getCurrencyCode());
    }

}

