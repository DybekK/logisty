import {
  CloseCircleOutlined,
  InfoCircleOutlined,
  WarningOutlined,
} from "@ant-design/icons"

import { NotificationType } from "@/features/notification"

export const getNotificationIcon = (type: NotificationType) => {
  switch (type) {
    case NotificationType.INFO:
      return <InfoCircleOutlined style={{ color: "#1890ff" }} />
    case NotificationType.WARNING:
      return <WarningOutlined style={{ color: "#faad14" }} />
    case NotificationType.ERROR:
      return <CloseCircleOutlined style={{ color: "#ff4d4f" }} />
  }
}
