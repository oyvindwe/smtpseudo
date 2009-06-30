package smtpseudo.smtp.message.exception;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/08
 * Time: 12:59:18
 * To change this template use File | Settings | File Templates.
 */
public class ValidationException extends Exception {

    private Collection validationExceptions;

    public ValidationException(Collection errors) {
        this.validationExceptions= Collections.unmodifiableCollection(errors);
    }

    public Collection getValidationExceptions() {
        return validationExceptions;
    }

    public String getMessage() {
        if (getValidationExceptions().size() == 0) { return super.getMessage(); }

        StringBuffer sb = new StringBuffer();

        boolean first = true;

        for (Iterator iter = getValidationExceptions().iterator(); iter.hasNext(); ) {
            Exception e = (Exception) iter.next();

            if (!first) {
                sb.append('\n');
                first = false;
            }

            sb.append(e.getMessage());
        }

        return sb.toString();
    }
}

