import { Role } from './role.model';

export interface SigninRequest {
  username: string;
  roles: Role[];
  accessToken: string;
}
