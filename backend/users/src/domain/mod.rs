use serde::Serialize;

pub mod port;

#[derive(Serialize)]
pub struct UserId(pub String);

impl UserId {
    pub fn new() -> Self {
        UserId(cuid::cuid2())
    }
}
