use auto_impl::auto_impl;
use chrono::{Duration, NaiveDateTime, Utc};
use std::sync::Mutex;

#[auto_impl(Arc)]
pub trait TimeProvider: Clone + Send + Sync {
    fn now(&self) -> NaiveDateTime;
}

pub struct FakeTimeProvider {
    now: Mutex<NaiveDateTime>,
}

impl TimeProvider for FakeTimeProvider {
    fn now(&self) -> NaiveDateTime {
        *self.now.lock().unwrap()
    }
}

impl Clone for FakeTimeProvider {
    fn clone(&self) -> Self {
        FakeTimeProvider {
            now: Mutex::new(*self.now.lock().unwrap()),
        }
    }
}

impl FakeTimeProvider {
    pub fn new() -> Self {
        FakeTimeProvider {
            now: Mutex::new(NaiveDateTime::default()),
        }
    }

    pub fn advance(&self, duration: Duration) {
        let mut now = self.now.lock().unwrap();
        *now = *now + duration;
    }
}

#[derive(Clone)]
pub struct SystemTimeProvider;

impl TimeProvider for SystemTimeProvider {
    fn now(&self) -> NaiveDateTime {
        Utc::now().naive_utc()
    }
}
