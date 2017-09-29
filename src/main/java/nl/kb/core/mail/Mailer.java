package nl.kb.core.mail;

import nl.kb.core.mail.mailer.Email;

public interface Mailer {

    void send(Email mail);
}
