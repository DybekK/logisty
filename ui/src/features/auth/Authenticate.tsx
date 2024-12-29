import { Controller, useForm } from "react-hook-form"

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
import { z } from "zod"

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

const loginSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z
    .string()
    .min(8, "Password must be at least 8 characters")
    .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
    .regex(/[a-z]/, "Password must contain at least one lowercase letter")
    .regex(/[0-9]/, "Password must contain at least one number"),
})

type LoginFormData = z.infer<typeof loginSchema>

export const Authenticate = () => {
  const {
    token: { colorBgLayout },
  } = theme.useToken()

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

  const onSubmit = async (data: LoginFormData) => {
    try {
      // TODO: Implement actual login logic here
      console.log("Form data:", data)
      message.success("Login successful!")
    } catch (error) {
      message.error("Login failed. Please try again.")
    }
  }

  return (
    <Layout style={{ ...layoutStyle, background: colorBgLayout }}>
      <div style={formContainerStyle}>
        <Card style={formCardStyle}>
          <div>
            <Typography.Title level={2} style={formTitleStyle}>
              Sign in to your account
            </Typography.Title>
          </div>
          <Form
            layout="vertical"
            onFinish={handleSubmit(onSubmit)}
            style={customFormStyle}
          >
            <Controller
              name="email"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label="Email"
                  validateStatus={errors.email ? "error" : undefined}
                  help={errors.email?.message}
                >
                  <Input
                    {...field}
                    type="text"
                    placeholder="Enter your email"
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
                  label="Password"
                  validateStatus={errors.password ? "error" : undefined}
                  help={errors.password?.message}
                >
                  <Input.Password
                    {...field}
                    placeholder="Enter your password"
                    style={inputStyle}
                  />
                </Form.Item>
              )}
            />
            <Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                loading={isSubmitting}
                style={buttonStyle}
              >
                Sign in
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </div>
    </Layout>
  )
}
