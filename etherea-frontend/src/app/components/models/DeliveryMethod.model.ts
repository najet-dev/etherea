import { DeliveryAddress } from './deliveryAddress.model';
import { DeliveryType } from './deliveryType.model';
import { PickupPointDetails } from './pickupPointDetails.model';

export interface DeliveryMethod {
  id: number;
  deliveryType: DeliveryType;
  userId: number;
  deliveryAddress?: DeliveryAddress;
  pickupPointDetails?: PickupPointDetails;
}
