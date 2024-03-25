import { Role } from './role.model';

export interface SignupRequest {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  password: string;
  roles?: Role[];
}
