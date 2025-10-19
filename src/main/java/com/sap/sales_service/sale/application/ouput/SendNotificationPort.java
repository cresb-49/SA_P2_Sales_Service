package com.sap.sales_service.sale.application.ouput;

import java.util.UUID;

public interface SendNotificationPort {
    void sendNotification(UUID userId, String message);
}
