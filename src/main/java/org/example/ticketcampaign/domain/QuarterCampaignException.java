package org.example.ticketcampaign.domain;

public class QuarterCampaignException extends RuntimeException {
    public QuarterCampaignException(String message) {
        super(message);
    }

    public QuarterCampaignException(String message, Throwable cause) {
        super(message, cause);
    }
}
