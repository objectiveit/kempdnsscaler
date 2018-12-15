package de.objectiveit.kempdnsscaler.util;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * Just a wrapper around {@link com.amazonaws.services.lambda.runtime.LambdaLogger}, which additionally collects logs
 * in the buffer for further usage (to send notification e-mail in our case).
 */
public class ApplicationLogger {

    private final LambdaLogger logger;
    private final StringBuffer buffer;

    public ApplicationLogger(LambdaLogger logger) {
        this.logger = logger;
        this.buffer = new StringBuffer();
    }

    public void log(String message) {
        logger.log(message);
        buffer.append(message);
    }

    public String getResultLogs() {
        return buffer.toString();
    }

}
