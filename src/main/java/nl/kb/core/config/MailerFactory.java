package nl.kb.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.kb.core.mail.Mailer;
import nl.kb.core.mail.mailer.SmtpMailer;
import nl.kb.core.mail.mailer.StubbedMailer;

public class MailerFactory {
    @JsonProperty
    private String to;

    @JsonProperty
    private String from;

    @JsonProperty
    private String host;

    @JsonProperty
    private String type;

    @JsonProperty
    private Integer port = 25;

    public Mailer getMailer() {
        if (type.equalsIgnoreCase("smtp")) {
            return new SmtpMailer(host, from, to, port);
        }

        return new StubbedMailer();
    }

    public String getHost() {
        return host;
    }
}
