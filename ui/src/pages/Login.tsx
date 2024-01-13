import { useForm } from "react-hook-form";
import { Button, Checkbox, Form, Input, Card } from "antd";

import { FormItem } from "react-hook-form-antd";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";

const schema = z.object({
  username: z
    .string()
    .min(1, { message: "Required" })
    .max(15, { message: "Username should be less than 15 characters" }),
  password: z.string().min(1, { message: "Required" }),
  remember: z.boolean(),
});

export const Login = () => {
  const { control, handleSubmit } = useForm({
    defaultValues: { username: "jsun969", password: "", remember: true },
    resolver: zodResolver(schema),
  });

  return (
    <Card title="Login">
      <Form
        style={{ maxWidth: 600 }}
        onFinish={handleSubmit((data) => {
          console.log(data);
        })}
      >
        <FormItem control={control} name="username" label="Username">
          <Input />
        </FormItem>
        <FormItem control={control} name="password" label="Password">
          <Input.Password />
        </FormItem>
        <FormItem control={control} name="remember" valuePropName="checked">
          <Checkbox>Remember me</Checkbox>
        </FormItem>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            Submit
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
};
