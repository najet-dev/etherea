export interface SignupRequest {
  firstName: string;
  lastName: string;
  username: string;
  password: string;
  roles?: string[];
}
