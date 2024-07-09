import Fastify, { FastifyInstance } from "fastify"
import { environment } from "@/application/env"
import UserRoute from "@/adapter/user.route"
import UserService from "@/domain/port/user.service"

const { port } = environment

export const initApplication = () => {
  const fastify = Fastify({
    logger: true,
  })

  const userService = UserService()

  fastify.register(UserRoute, { prefix: "/v1", userService })

  startFastify(fastify, port)
}

const startFastify = (fastify: FastifyInstance, port: number) =>
  fastify.listen({ port }, err => {
    if (err) {
      fastify.log.error(err)
      process.exit(1)
    }
  })
