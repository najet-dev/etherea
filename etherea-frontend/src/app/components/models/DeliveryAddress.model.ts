import { SignupRequest } from './SignupRequest.model';

export interface DeliveryAddress {
  id: number;
  address: string;
  zipCode: number;
  city: string;
  country: string;
  user: SignupRequest;
}
