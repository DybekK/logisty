import { FastifyPluginAsync } from "fastify"
import { UserService } from "@logisty/users/domain/port/user.service"

interface UsersRouteOptions {
  userService: UserService
}

const UserRoute: FastifyPluginAsync<UsersRouteOptions> = async (
  fastify,
  { userService }: UsersRouteOptions,
) => {
  fastify.get("/users", async () => userService.getUsers())
}

export default UserRoute
