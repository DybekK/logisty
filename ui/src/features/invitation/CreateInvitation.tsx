import { Controller, useForm } from "react-hook-form"
import { useTranslation } from "react-i18next"

import {
  Button,
  Card,
  Col,
  DatePicker,
  Form,
  Input, message,
  Row,
  Select,
} from "antd"

import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"

import { AxiosBackendError, useAppSelector, UserRole } from "@/common"
import { useMutation } from "@tanstack/react-query"
import { createInvitation } from "./invitation.api"

const inputStyle: React.CSSProperties = {
  borderRadius: "5px",
}

const buttonStyle: React.CSSProperties = {
  borderRadius: "5px",
}

interface CreateInvitationProps {
  onCloseModal?: () => void
}

export const CreateInvitation = ({ onCloseModal }: CreateInvitationProps) => {
  const { t: tApi } = useTranslation("api")
  const { t } = useTranslation("invitation")

  const { fleetId } = useAppSelector(state => state.auth.user!)

  const invitationSchema = z.object({
    email: z.string().email(t("createInvitation.inputs.errors.invalidEmail")),
    role: z.nativeEnum(UserRole),
    firstName: z.string().min(1, t("createInvitation.inputs.errors.required")),
    lastName: z.string().min(1, t("createInvitation.inputs.errors.required")),
    phoneNumber: z
      .string()
      .min(1, t("createInvitation.inputs.errors.required")),
    dateOfBirth: z.date(),
    street: z.string().min(1, t("createInvitation.inputs.errors.required")),
    streetNumber: z
      .string()
      .min(1, t("createInvitation.inputs.errors.required")),
    apartmentNumber: z.string().optional(),
    city: z.string().min(1, t("createInvitation.inputs.errors.required")),
    stateProvince: z
      .string()
      .min(1, t("createInvitation.inputs.errors.required")),
    postalCode: z.string().min(1, t("createInvitation.inputs.errors.required")),
  })
  type InvitationFormData = z.infer<typeof invitationSchema>

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<InvitationFormData>({
    resolver: zodResolver(invitationSchema),
    defaultValues: {
      email: "",
      role: UserRole.DRIVER,
      firstName: "",
      lastName: "",
      phoneNumber: "",
      street: "",
      streetNumber: "",
      apartmentNumber: "",
      city: "",
      stateProvince: "",
      postalCode: "",
    },
  })

  const { mutateAsync: createInvitationMutate, isPending: isCreatingInvitation } =
    useMutation({
      mutationFn: (data: InvitationFormData) => createInvitation(fleetId, data),
      onSuccess: () => {
        message.success(t("createInvitation.success"))
        onCloseModal?.()
      },
      onError: (error: AxiosBackendError) => {
        message.error(tApi("fallback"))
      },
    })

  return (
    <Card>
      <Form layout="vertical" size="large">
        <Row gutter={16}>
          <Col span={12}>
            <Controller
              name="email"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.email")}
                  validateStatus={errors.email ? "error" : undefined}
                  help={errors.email?.message}
                >
                  <Input {...field} style={inputStyle} />
                </Form.Item>
              )}
            />
          </Col>
          <Col span={12}>
            <Controller
              name="role"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.role")}
                  validateStatus={errors.role ? "error" : undefined}
                  help={errors.role?.message}
                >
                  <Select
                    {...field}
                    style={inputStyle}
                    options={[
                      {
                        value: UserRole.DRIVER,
                        label: t("createInvitation.roles.driver"),
                      },
                      {
                        value: UserRole.DISPATCHER,
                        label: t("createInvitation.roles.dispatcher"),
                      },
                    ]}
                  />
                </Form.Item>
              )}
            />
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Controller
              name="firstName"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.firstName")}
                  validateStatus={errors.firstName ? "error" : undefined}
                  help={errors.firstName?.message}
                >
                  <Input {...field} style={inputStyle} />
                </Form.Item>
              )}
            />
          </Col>
          <Col span={12}>
            <Controller
              name="lastName"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.lastName")}
                  validateStatus={errors.lastName ? "error" : undefined}
                  help={errors.lastName?.message}
                >
                  <Input {...field} style={inputStyle} />
                </Form.Item>
              )}
            />
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Controller
              name="phoneNumber"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.phoneNumber")}
                  validateStatus={errors.phoneNumber ? "error" : undefined}
                  help={errors.phoneNumber?.message}
                >
                  <Input {...field} style={inputStyle} />
                </Form.Item>
              )}
            />
          </Col>
          <Col span={12}>
            <Controller
              name="dateOfBirth"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.dateOfBirth")}
                  validateStatus={errors.dateOfBirth ? "error" : undefined}
                  help={
                    errors.dateOfBirth?.message &&
                    t("createInvitation.inputs.errors.required")
                  }
                >
                  <DatePicker
                    {...field}
                    placeholder={t("createInvitation.inputs.dateOfBirth")}
                    style={{ ...inputStyle, width: "100%" }}
                  />
                </Form.Item>
              )}
            />
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={8}>
            <Controller
              name="street"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.street")}
                  validateStatus={errors.street ? "error" : undefined}
                  help={errors.street?.message}
                >
                  <Input {...field} style={inputStyle} />
                </Form.Item>
              )}
            />
          </Col>
          <Col span={8}>
            <Controller
              name="streetNumber"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.streetNumber")}
                  validateStatus={errors.streetNumber ? "error" : undefined}
                  help={errors.streetNumber?.message}
                >
                  <Input {...field} style={inputStyle} />
                </Form.Item>
              )}
            />
          </Col>
          <Col span={8}>
            <Controller
              name="apartmentNumber"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.apartmentNumber")}
                  validateStatus={
                    errors.apartmentNumber ? "error" : undefined
                  }
                  help={errors.apartmentNumber?.message}
                >
                  <Input {...field} style={inputStyle} />
                </Form.Item>
              )}
            />
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={8}>
            <Controller
              name="city"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.city")}
                  validateStatus={errors.city ? "error" : undefined}
                  help={errors.city?.message}
                >
                  <Input {...field} style={inputStyle} />
                </Form.Item>
              )}
            />
          </Col>
          <Col span={8}>
            <Controller
              name="stateProvince"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.stateProvince")}
                  validateStatus={
                    errors.stateProvince ? "error" : undefined
                  }
                  help={errors.stateProvince?.message}
                >
                  <Input {...field} style={inputStyle} />
                </Form.Item>
              )}
            />
          </Col>
          <Col span={8}>
            <Controller
              name="postalCode"
              control={control}
              render={({ field }) => (
                <Form.Item
                  label={t("createInvitation.postalCode")}
                  validateStatus={errors.postalCode ? "error" : undefined}
                  help={errors.postalCode?.message}
                >
                  <Input {...field} style={inputStyle} />
                </Form.Item>
              )}
            />
          </Col>
        </Row>
        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            style={buttonStyle}
            loading={isCreatingInvitation}
            onClick={handleSubmit(data => createInvitationMutate(data))}
          >
            {t("createInvitation.submit")}
          </Button>
        </Form.Item>
      </Form>
    </Card>
  )
}
