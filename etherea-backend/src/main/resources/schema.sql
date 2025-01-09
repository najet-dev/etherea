DELIMITER $$

CREATE PROCEDURE ChangeDeliveryMode(
    IN deliveryId BIGINT,
    IN newDeliveryOption VARCHAR(255),
    IN userAddressId BIGINT,
    IN pickupPointAddress VARCHAR(255),
    IN pickupPointLatitude DOUBLE,
    IN pickupPointLongitude DOUBLE,
    IN pickupPointName VARCHAR(255)
)
BEGIN
    DECLARE currentOption VARCHAR(255);

    -- Récupérer l'option de livraison actuelle
    SELECT delivery_option INTO currentOption FROM delivery_method WHERE id = deliveryId;

    -- Mise à jour selon le nouveau type
    IF newDeliveryOption = 'HOME_STANDARD' THEN
        UPDATE delivery_method
        SET delivery_option = newDeliveryOption,
            delivery_address_id = userAddressId
        WHERE id = deliveryId;

    ELSEIF newDeliveryOption = 'HOME_EXPRESS' THEN
        UPDATE delivery_method
        SET delivery_option = newDeliveryOption,
            delivery_address_id = userAddressId
        WHERE id = deliveryId;

    ELSEIF newDeliveryOption = 'PICKUP_POINT' THEN
        UPDATE delivery_method
        SET delivery_option = newDeliveryOption
        WHERE id = deliveryId;

        -- Insérer les informations dans PickupPointDelivery
        INSERT INTO pickup_point_delivery (id, pickup_point_name, pickup_point_address, pickup_point_latitude, pickup_point_longitude)
        VALUES (deliveryId, pickupPointName, pickupPointAddress, pickupPointLatitude, pickupPointLongitude);
    END IF;
END$$

DELIMITER ;
