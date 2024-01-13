import React from "react";

import {Card, Layout, theme} from 'antd';
import {LeftSider, MainContent, Top} from "components";

const App: React.FC = () => {
    const {
        token: {colorBgLayout},
    } = theme.useToken();

    return (
        <Layout style={{background: colorBgLayout, height: '100vh'}}>
            <Top/>
            <Layout>
                <LeftSider/>
                <MainContent>
                    <Card title="Card title">
                        <Card type="inner" title="Inner Card title" extra={<a href="#">More</a>}>
                            Inner Card content
                        </Card>
                        <Card
                            style={{ marginTop: 16 }}
                            type="inner"
                            title="Inner Card title"
                            extra={<a href="#">More</a>}
                        >
                            Inner Card content
                        </Card>
                    </Card>
                </MainContent>
            </Layout>
        </Layout>
    );
};

export default App;