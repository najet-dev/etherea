import { SignupRequest } from './signupRequest.model';

export interface DeliveryAddress {
  id: number;
  address: string;
  zipCode: number;
  city: string;
  country: string;
  phoneNumber: string;
  user: SignupRequest;
  default: boolean;
}
