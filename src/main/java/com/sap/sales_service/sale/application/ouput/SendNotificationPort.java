package com.sap.sales_service.sale.application.ouput;


import com.sap.sales_service.sale.domain.dtos.events.NotificationDTO;

public interface SendNotificationPort {
    void sendNotification(NotificationDTO notificationDTO);
}
