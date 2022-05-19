package org.sang.service;

import javax.mail.MessagingException;

public interface EmailService {
    String toUserEmailWithCode(String email) throws MessagingException, javax.mail.MessagingException;

}
