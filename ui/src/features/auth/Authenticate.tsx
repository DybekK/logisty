import { useMutation } from "@tanstack/react-query"
import { useEffect } from "react"
import { Controller, useForm } from "react-hook-form"
import { useTranslation } from "react-i18next"
import { useNavigate } from "react-router-dom"

import {
  Button,
  Card,
  Form,
  Input,
  Layout,
  Typography,
  message,
  theme,
} from "antd"

import { zodResolver } from "@hookform/resolvers/zod"
import { match } from "ts-pattern"
import { z } from "zod"

import {
  AuthenticateResponse,
  AuthenticationErrorTypes,
  AxiosBackendError,
  patternErrors,
  useAppDispatch,
  useAppSelector,
} from "@/common"
import { useAuth } from "@/components"
import {
  authenticate,
  fetchCurrentUserAfterAuthentication,
} from "@/features/auth"
import { removeUser, setUser } from "@/features/auth"
import { getDefaultRedirect } from "@/router"

const layoutStyle: React.CSSProperties = {
  height: "100vh",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
}

const formContainerStyle: React.CSSProperties = {
  width: "100%",
  maxWidth: "400px",
  margin: "0 auto",
  padding: "20px",
}

const formCardStyle: React.CSSProperties = {
  borderRadius: "10px",
  boxShadow: "8px 0px 15px -5px rgba(0, 0, 0, 0.025)",
}

const formTitleStyle: React.CSSProperties = {
  textAlign: "center",
  marginBottom: "20px",
}

const customFormStyle: React.CSSProperties = {
  marginBottom: "16px",
}

const inputStyle: React.CSSProperties = {
  borderRadius: "5px",
}

const buttonStyle: React.CSSProperties = {
  width: "100%",
  borderRadius: "5px",
}

export const Authenticate = () => {
  const { t: tApi } = useTranslation("api")
  const { t: tAuth } = useTranslation("auth")
  const {
    token: { colorBgLayout },
  } = theme.useToken()

  const roles = useAppSelector(state => state.auth.user?.roles) ?? []

  const { setTokens, isUserAvailable } = useAuth()
  const dispatch = useAppDispatch()
  const navigate = useNavigate()

  const loginSchema = z.object({
    email: z.string().email(tAuth("authenticate.inputs.errors.email")),
    password: z
      .string()
      .min(8, tAuth("authenticate.inputs.errors.passwordMinLength"))
      .regex(/[A-Z]/, tAuth("authenticate.inputs.errors.passwordUppercase"))
      .regex(/[a-z]/, tAuth("authenticate.inputs.errors.passwordLowercase"))
      .regex(/[0-9]/, tAuth("authenticate.inputs.errors.passwordNumber")),
  })
  type LoginFormData = z.infer<typeof loginSchema>

  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  })

  const { mutateAsync: authenticateMutate, isPending: isAuthenticating } =
    useMutation({
      mutationFn: authenticate,
      onSuccess: tokens => {
        setTokens(tokens)
        fetchCurrentUserMutate(tokens)
      },
      onError: (error: AxiosBackendError) => {
        match(error.response?.data)
          .with(patternErrors(AuthenticationErrorTypes), () => {
            message.error(tAuth("authenticate.fallbacks.invalidCredentials"))
          })
          .otherwise(() => {
            message.error(tApi("fallback"))
          })
      },
    })

  const {
    mutateAsync: fetchCurrentUserMutate,
    isPending: isFetchingCurrentUser,
  } = useMutation({
    mutationFn: (tokens: AuthenticateResponse) =>
      fetchCurrentUserAfterAuthentication(tokens),
    onSuccess: user => {
      dispatch(setUser(user))
      navigate(getDefaultRedirect(user.roles))
    },
    onError: () => dispatch(removeUser()),
  })

  useEffect(() => {
    if (isUserAvailable()) {
      navigate(getDefaultRedirect(roles))
    }
  }, [isUserAvailable, navigate, roles])

  return (
    <Layout style={{ ...layoutStyle, background: colorBgLayout }}>
      <div style={formContainerStyle}>
        <Card style={formCardStyle}>
          <div>
            <Typography.Title level={2} style={formTitleStyle}>
              {tAuth("authenticate.title")}
            </Typography.Title>
          </div>
          <Form
            layout="vertical"
            size="large"
            onFinish={handleSubmit(validated => authenticateMutate(validated))}
            style={customFormStyle}
          >
            <Controller
              name="email"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={tAuth("authenticate.email")}
                  validateStatus={errors.email ? "error" : undefined}
                  help={errors.email?.message}
                >
                  <Input
                    {...field}
                    type="text"
                    placeholder={tAuth("authenticate.inputs.email")}
                    style={inputStyle}
                  />
                </Form.Item>
              )}
            />
            <Controller
              name="password"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={tAuth("authenticate.password")}
                  validateStatus={errors.password ? "error" : undefined}
                  help={errors.password?.message}
                >
                  <Input.Password
                    {...field}
                    placeholder={tAuth("authenticate.inputs.password")}
                    style={inputStyle}
                  />
                </Form.Item>
              )}
            />
            <Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                loading={
                  isSubmitting || isAuthenticating || isFetchingCurrentUser
                }
                style={buttonStyle}
              >
                {tAuth("authenticate.signIn")}
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </div>
    </Layout>
  )
}
