import { DeliveryAddress } from './DeliveryAddress.model';
import { DeliveryType } from './DeliveryType.model';
import { PickupPointDetails } from './pickupPointDetails.model';

export interface DeliveryMethod {
  id: number;
  deliveryType: DeliveryType;
  userId: number;
  deliveryAddress?: DeliveryAddress;
  pickupPointDetails?: PickupPointDetails;
}
