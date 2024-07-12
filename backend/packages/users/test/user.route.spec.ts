import Fastify, { FastifyInstance } from "fastify"
import UserRoute from "@logisty/users/adapter/user.route"
import { UserService } from "@logisty/users/domain/port/user.service"

const FakeUserService = (): UserService => {
  const getUsers = async () => [
    { id: "1", name: "John Doe", email: "fake@gmail.com" },
  ]

  return { getUsers }
}

describe("GET `/users` route", () => {
  let fastify: FastifyInstance

  beforeAll(() => {
    fastify = Fastify()
    const userService = FakeUserService()
    fastify.register(UserRoute, { prefix: "/v1", userService })
  })

  afterAll(async () => {
    await fastify.close()
  })

  it("should return status code 200", async () => {
    const res = await fastify.inject({ method: "GET", url: "/v1/users" })
    expect(res.statusCode).toBe(200)
  })
})
