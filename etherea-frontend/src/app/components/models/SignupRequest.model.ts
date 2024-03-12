import { Role } from './role.model';

export interface SignupRequest {
  firstName: string;
  lastName: string;
  username: string;
  password: string;
  roles?: Role[];
}
