import { Role } from './role.enum';

export interface SignupRequest {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  password: string;
  roles: Role[];
}
