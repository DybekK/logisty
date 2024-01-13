import React from "react";

import {Menu, MenuProps, Layout} from "antd";
import {BellOutlined, UserOutlined} from "@ant-design/icons";

const {Header} = Layout;

const items: MenuProps['items'] = [
    {
        label: 'Profile',
        key: 'profile',
        icon: <UserOutlined/>,
    },
    {
        label: 'Notifications',
        key: 'notifications',
        icon: <BellOutlined/>
    }
]

export const Top: React.FC = () => {
    return (
        <Header style={{display: 'flex', background: 'transparent'}}>
            <Menu
                style={{background: 'transparent', flex: 1, flexDirection: 'row-reverse'}}
                mode="horizontal"
                items={items}
            />
        </Header>
    )
}