import { Controller, useForm } from "react-hook-form"
import { Trans, useTranslation } from "react-i18next"
import { useNavigate, useParams } from "react-router-dom"

import { Button, Card, Form, Input, Layout, Typography, theme } from "antd"

import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"

import { Routes } from "@/router"

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

const inputStyle: React.CSSProperties = {
  borderRadius: "5px",
}

const buttonStyle: React.CSSProperties = {
  width: "100%",
  borderRadius: "5px",
}

const titleStyle: React.CSSProperties = {
  fontWeight: "bold",
  fontSize: "2rem",
}

export const AcceptInvitation = () => {
  const {
    token: { colorBgLayout, colorPrimary },
  } = theme.useToken()
  const navigate = useNavigate()

  const { t } = useTranslation("invitation")
  const { invitationId } = useParams<{ invitationId: string }>()

  const invitationSchema = z.object({
    password: z
      .string()
      .min(8, t("acceptInvitation.inputs.errors.passwordMinLength")),
  })
  type InvitationFormData = z.infer<typeof invitationSchema>

  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<InvitationFormData>({
    resolver: zodResolver(invitationSchema),
    defaultValues: {
      password: "",
    },
  })

  const onSubmit = async ({ password }: InvitationFormData) => {
    navigate(Routes.LOGIN)
  }

  return (
    <Layout style={{ ...layoutStyle, background: colorBgLayout }}>
      <div style={formContainerStyle}>
        <Card style={formCardStyle}>
          <div>
            <Typography.Title level={3} style={formTitleStyle}>
              <Trans
                t={t}
                i18nKey="acceptInvitation.title"
                values={{ inviter: "Cargo Express" }}
                components={[
                  <Typography.Text
                    key="span"
                    style={{ ...titleStyle, color: colorPrimary }}
                  />,
                ]}
              />
            </Typography.Title>
          </div>
          <Form layout="vertical" onFinish={handleSubmit(onSubmit)}>
            <Form.Item label={t("acceptInvitation.email")}>
              <Input
                type="email"
                placeholder={t("acceptInvitation.email")}
                disabled
                style={inputStyle}
              />
            </Form.Item>
            <Controller
              name="password"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("acceptInvitation.password")}
                  validateStatus={errors.password ? "error" : undefined}
                  help={errors.password?.message}
                >
                  <Input.Password
                    {...field}
                    placeholder={t("acceptInvitation.inputs.password")}
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
                {t("acceptInvitation.accept")}
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </div>
    </Layout>
  )
}
