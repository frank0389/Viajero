package ec.viajero.IdentityServer.util;

import java.util.HashMap;
import java.util.Map;

import ec.viajero.IdentityServer.model.EmailTemplate;

public class EmailConstants {
    public static final String EMAIL_USER_PARAM = "$firstName";
    public static final String EMAIL_CODE = "$code";
    public static final String EMAIL_ACCOUNT = "$userName";
    public static final String EMAIL_DATE ="$date";
    public static final String EMAIL_FOOTER_PARAM = "$footerText";

    public static Map<EmailTemplate.Type, String> templateTypes;
    static {
        templateTypes = new HashMap<>();
        templateTypes.put(EmailTemplate.Type.AccountActivation, "AccountActivation");
        templateTypes.put(EmailTemplate.Type.AccountConfirmation, "AccountConfirmation");
        templateTypes.put(EmailTemplate.Type.PasswordReset, "PasswordReset");
        templateTypes.put(EmailTemplate.Type.AccountRecovery, "AccountRecovery");
        templateTypes.put(EmailTemplate.Type.AccountLogin, "AccountLogin");

    }


}