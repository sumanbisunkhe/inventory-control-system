package com.example.inventory_control_system.service;


import com.example.inventory_control_system.utils.CustomEmailMessage;

public interface EmailService {
    void sendEmail(CustomEmailMessage emailMessage);
}
