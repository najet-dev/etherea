import { DeliveryAddress } from './DeliveryAddress.model';
import { DeliveryType } from './DeliveryType.enum';
import { PickupPointDetails } from './pickupPointDetails.model';

export interface DeliveryMethod {
  id: number | null;
  type: DeliveryType;
  deliveryDays: string;
  cost: number;
  deliveryAddress?: DeliveryAddress;
  pickupPointDetails?: PickupPointDetails;
}
