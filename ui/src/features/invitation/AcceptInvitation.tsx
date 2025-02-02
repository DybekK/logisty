import { useMutation } from "@tanstack/react-query"
import { useEffect } from "react"
import { Controller, useForm } from "react-hook-form"
import { Trans, useTranslation } from "react-i18next"
import { useNavigate, useParams } from "react-router-dom"

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

import { AxiosBackendError, patternErrors } from "@/common"
import {
  acceptInvitation,
  useFetchInvitation,
} from "@/features/invitation/invitation.api"
import {
  AcceptInvitationErrorTypes,
  InvitationStatus,
} from "@/features/invitation/invitation.types"
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

  const { t: tApi } = useTranslation("api")
  const { t, ready: tReady } = useTranslation("invitation")
  const { invitationId } = useParams<{ invitationId: string }>()

  const invitationSchema = z.object({
    password: z
      .string()
      .min(8, t("acceptInvitation.inputs.errors.passwordMinLength")),
  })
  type InvitationFormData = z.infer<typeof invitationSchema>

  const { data: invitation, error } = useFetchInvitation(invitationId!)

  useEffect(() => {
    if (error) {
      navigate(Routes.PENDING_ORDERS)
    }
  }, [error, navigate])

  useEffect(() => {
    if (!tReady) return

    if (invitation && invitation.status !== InvitationStatus.PENDING) {
      message.error(t("acceptInvitation.fallbacks.invalidInvitation"))
      navigate(Routes.LOGIN)
    }
  }, [tReady, t, invitation, navigate])

  const {
    mutateAsync: acceptInvitationMutate,
    isPending: isAcceptingInvitation,
  } = useMutation({
    mutationFn: (data: InvitationFormData) =>
      acceptInvitation(invitationId!, data.password),
    onSuccess: () => {
      message.success(t("acceptInvitation.success"))
      navigate(Routes.LOGIN)
    },
    onError: (error: AxiosBackendError) => {
      match(error.response?.data)
        .with(patternErrors(AcceptInvitationErrorTypes), () => {
          message.error(t("acceptInvitation.fallbacks.invalidInvitation"))
        })
        .otherwise(() => {
          message.error(tApi("fallback"))
        })
    },
  })

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<InvitationFormData>({
    resolver: zodResolver(invitationSchema),
    defaultValues: {
      password: "",
    },
  })

  return (
    <Layout style={{ ...layoutStyle, background: colorBgLayout }}>
      <div style={formContainerStyle}>
        <Card style={formCardStyle}>
          <div>
            <Typography.Title level={3} style={formTitleStyle}>
              <Trans
                t={t}
                i18nKey="acceptInvitation.title"
                values={{ inviter: invitation?.fleetName }}
                components={[
                  <Typography.Text
                    key="span"
                    style={{ ...titleStyle, color: colorPrimary }}
                  />,
                ]}
              />
            </Typography.Title>
          </div>
          <Form
            layout="vertical"
            onFinish={handleSubmit(validated =>
              acceptInvitationMutate(validated),
            )}
          >
            <Form.Item label={t("acceptInvitation.email")}>
              <Input
                type="email"
                placeholder={invitation?.email}
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
                loading={isAcceptingInvitation}
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
