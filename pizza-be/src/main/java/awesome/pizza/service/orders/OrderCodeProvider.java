package awesome.pizza.service.orders;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.regex.Pattern;

@UtilityClass
public class OrderCodeProvider {

    public static final String ORDER_CODE_REGEX="^AWSPZ-[A-Z0-9]{8}$";


    public String generateOrderCode() {
        String prefix = "AWSPZ-";
        int length = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(prefix);
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    public boolean orderCodeIsValid(String orderCode) {
        return orderCode != null && Pattern.compile(ORDER_CODE_REGEX).matcher(orderCode.trim().toUpperCase()).matches();
    }
}
