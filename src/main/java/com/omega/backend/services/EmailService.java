package com.omega.backend.services;

import org.springframework.mail.SimpleMailMessage;

import com.omega.backend.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);

	void sendEmail(SimpleMailMessage msg);
}
