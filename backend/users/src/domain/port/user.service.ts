import { User } from "@/domain/model/user"

export interface UserService {
  getUsers(): Promise<User[]>
}

const UserService = (): UserService => {
  const getUsers = async () => [
    { id: "1", name: "John Doe", email: "johndoe@gmail.com" },
  ]

  return { getUsers }
}

export default UserService
