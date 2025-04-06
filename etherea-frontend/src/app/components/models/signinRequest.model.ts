import { Role } from './role.enum';

export interface SigninRequest {
  id: number;
  username: string;
  roles: Role;
  accessToken: string;
}
